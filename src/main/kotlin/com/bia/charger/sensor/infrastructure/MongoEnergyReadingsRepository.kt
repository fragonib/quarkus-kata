package com.bia.charger.sensor.infrastructure

import com.bia.charger.sensor.application.EnergyReadingsRepository
import com.bia.charger.sensor.model.DeviceSN
import com.bia.charger.sensor.model.EnergyReading
import com.bia.charger.sensor.model.Power
import com.bia.charger.sensor.model.ReadingId
import com.bia.charger.shared.notNull
import io.quarkus.mongodb.panache.common.MongoEntity
import io.quarkus.mongodb.panache.kotlin.PanacheMongoEntity
import io.quarkus.mongodb.panache.kotlin.reactive.ReactivePanacheMongoRepositoryBase
import io.smallrye.mutiny.Uni
import org.bson.types.ObjectId
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.*
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class MongoEnergyReadingsRepository
  : ReactivePanacheMongoRepositoryBase<MongoEnergyReading, ReadingId>, EnergyReadingsRepository {

  override fun retrieve(readingId: ReadingId): Uni<EnergyReading> =
    findById(readingId).notNull()
      .map { it.toModel() }

  override fun createNew(energyReading: EnergyReading): Uni<EnergyReading> =
    persist(MongoEnergyReading.fromModel(energyReading))
      .map { it.toModel() }

}


@MongoEntity(collection = "readings")
data class MongoEnergyReading(
  val timestamp: Date,
  val energyCounter: Long,
  val power: Long,
  val deviceId: DeviceSN,
) : PanacheMongoEntity() {

  companion object {
    fun fromModel(reading: EnergyReading): MongoEnergyReading {
      val mongoEnergyReading = MongoEnergyReading(
        timestamp = Date.from(reading.timestamp.toInstant()),
        energyCounter = reading.energyCounter.toLong(),
        power = reading.power.watts.toLong(),
        deviceId = reading.deviceId,
      )
      mongoEnergyReading.id = reading.id?.let { ObjectId(it) }
      return mongoEnergyReading
    }
  }

  fun toModel(): EnergyReading {
    return EnergyReading(
      id = id.toString(),
      timestamp = OffsetDateTime.from(timestamp.toInstant().atZone(ZoneOffset.UTC)),
      energyCounter = energyCounter,
      power = Power(power, ChronoUnit.MINUTES),
      deviceId = deviceId,
    )
  }
}
