package com.bia.charger.sensor.infrastructure

import com.bia.charger.sensor.application.EnergyReadingsRepository
import com.bia.charger.sensor.model.EnergyReading
import com.bia.charger.sensor.model.ReadingId
import com.bia.charger.sensor.model.notNull
import io.quarkus.mongodb.panache.kotlin.reactive.ReactivePanacheMongoRepositoryBase
import io.smallrye.mutiny.Uni
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class MongoEnergyReadingsRepository
  : ReactivePanacheMongoRepositoryBase<EnergyReading, ReadingId>, EnergyReadingsRepository
{

  override fun retrieve(readingId: ReadingId): Uni<EnergyReading> = findById(readingId).notNull()

  override fun createNew(energyReading: EnergyReading): Uni<EnergyReading> = persist(energyReading)

}
