package com.bia.charger.sensor.application

import com.bia.charger.sensor.model.EnergyReading
import com.bia.charger.sensor.model.ReadingId
import io.smallrye.mutiny.Uni

interface EnergyReadingsRepository {

  /**
   * Retrieves a previously registered energy reading info for given [readingId]
   *
   * @param readingId Id of the energy reading
   *
   * @return Reactive response, fails if not registered
   */
  fun retrieve(readingId: ReadingId): Uni<EnergyReading>

  /**
   * Persist a new given [energyReading]
   *
   * @param energyReading Energy reading data
   *
   * @return Reactive response with registered data
   */
  fun createNew(energyReading: EnergyReading): Uni<EnergyReading>

}
