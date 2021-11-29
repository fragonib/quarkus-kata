package com.bia.chargermonitor.energy.application

import com.bia.chargermonitor.energy.model.DeviceSN
import com.bia.chargermonitor.energy.model.EnergyReading
import io.smallrye.mutiny.Multi
import java.time.OffsetDateTime
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class FindEnergyReadingUseCase(
  private val energyReadingsRepository: EnergyReadingsRepository
) {

  fun findEnergyReadings(deviceSN: DeviceSN, from: OffsetDateTime, to: OffsetDateTime): Multi<EnergyReading> {
    return energyReadingsRepository.findBetween(deviceSN, from, to)
  }

}
