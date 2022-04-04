package com.solar.production.monitor.energy.application

import com.solar.production.monitor.energy.model.DeviceSN
import com.solar.production.monitor.energy.model.EnergyReading
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
