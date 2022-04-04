package com.solar.production.monitor.energy.infrastructure.repository

import com.solar.production.monitor.energy.application.EnergyReadingsRepository
import com.solar.production.monitor.energy.model.DeviceSN
import com.solar.production.monitor.energy.model.EnergyReading
import com.solar.production.monitor.energy.model.Power
import com.solar.production.monitor.energy.model.ReadingId
import com.solar.production.monitor.shared.notNull
import io.quarkus.mongodb.panache.common.MongoEntity
import io.quarkus.mongodb.panache.kotlin.reactive.ReactivePanacheMongoRepositoryBase
import io.quarkus.panache.common.Parameters
import io.quarkus.panache.common.Sort
import io.smallrye.mutiny.Multi
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

  override fun findBetween(
      deviceSN: DeviceSN,
      from: OffsetDateTime,
      to: OffsetDateTime,
  ): Multi<EnergyReading> {
    val query = """
      |{
      |"timestamp": { ${'$'}gte: new ISODate(:from), ${'$'}lte: new ISODate(:to) },
      |"deviceId": :deviceId
      |}
      |""".trimMargin()
    return find(query,
        Sort.by("timestamp").descending(),
        Parameters
            .with("deviceId", deviceSN)
            .and("to", to.toString())
            .and("from", from.toString()))
      .stream()
      .map { it.toModel() }
  }

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
