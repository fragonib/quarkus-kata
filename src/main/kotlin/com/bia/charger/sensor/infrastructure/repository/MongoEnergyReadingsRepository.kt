package com.bia.charger.sensor.infrastructure.repository

import com.bia.charger.sensor.application.EnergyReadingsRepository
import com.bia.charger.sensor.model.DeviceSN
import com.bia.charger.sensor.model.EnergyReading
import com.bia.charger.sensor.model.Power
import com.bia.charger.sensor.model.ReadingId
import com.bia.charger.shared.notNull
import io.quarkus.mongodb.panache.common.MongoEntity
import io.quarkus.mongodb.panache.kotlin.reactive.ReactivePanacheMongoRepositoryBase
import io.smallrye.mutiny.Uni
import org.bson.codecs.pojo.annotations.BsonId
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.*
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class MongoEnergyReadingsRepository
  : ReactivePanacheMongoRepositoryBase<MongoEnergyReading, String>, EnergyReadingsRepository {

  override fun retrieve(readingId: ReadingId): Uni<EnergyReading> =
    findById(readingId.toString()).notNull()
      .map { it.toModel() }

  override fun createNew(energyReading: EnergyReading): Uni<EnergyReading> =
    persist(MongoEnergyReading.fromModel(energyReading))
      .map { it.toModel() }

}


@MongoEntity(collection = "readings")
data class MongoEnergyReading(
  @field:BsonId
  var id: String? = null,
  var timestamp: Date? = null,
  var energyCounter: Long? = null,
  var power: Long? = null,
  var deviceId: DeviceSN? = null,
) {

  companion object {
    fun fromModel(reading: EnergyReading): MongoEnergyReading {
      return MongoEnergyReading(
        id = (reading.id ?: UUID.randomUUID()).toString(),
        timestamp = Date.from(reading.timestamp.toInstant()),
        energyCounter = reading.energyCounter.toLong(),
        power = reading.power.watts.toLong(),
        deviceId = reading.deviceId,
      )
    }
  }

  fun toModel(): EnergyReading {
    return EnergyReading(
      id = UUID.fromString(id),
      timestamp = OffsetDateTime.from(timestamp!!.toInstant().atZone(ZoneOffset.UTC)),
      energyCounter = energyCounter!!,
      power = Power(power!!, ChronoUnit.MINUTES),
      deviceId = deviceId!!,
    )
  }
}
