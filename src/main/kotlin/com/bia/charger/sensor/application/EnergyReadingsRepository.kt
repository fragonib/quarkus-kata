package com.bia.charger.sensor.application

import com.bia.charger.sensor.model.DeviceSN
import com.bia.charger.sensor.model.EnergyReading
import com.bia.charger.sensor.model.ReadingId
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import java.time.OffsetDateTime

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
   * Find every reading belonging to [deviceSN] and reported between [from] - [to]
   *
   * @param deviceSN Device serial number
   * @param from From date
   * @param to To date
   *
   * @return Multi of energy readings (possible empty)
   */
  fun findBetween(deviceSN: DeviceSN, from: OffsetDateTime, to: OffsetDateTime): Multi<EnergyReading>

  /**
   * Persist a new given [energyReading]
   *
   * @param energyReading Energy reading data
   *
   * @return Reactive response with registered data
   */
  fun createNew(energyReading: EnergyReading): Uni<EnergyReading>

}
