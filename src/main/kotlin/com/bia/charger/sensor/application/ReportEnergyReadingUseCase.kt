package com.bia.charger.sensor.application

import com.bia.charger.sensor.model.DeviceEnergyReport
import com.bia.charger.sensor.model.EnergyReading
import com.bia.charger.shared.notNull
import com.bia.charger.shared.toUni
import io.smallrye.mutiny.Uni
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class ReportEnergyReadingUseCase(
  private val deviceRepository: DeviceRepository,
  private val energyReadingsRepository: EnergyReadingsRepository
) {

  fun reportEnergyReading(energyReport: DeviceEnergyReport): Uni<EnergyReading> {
    return deviceRepository.retrieveOrCreateNew(energyReport.deviceSn)
      .flatMap { device ->
        device.mostRecentReading().toUni().notNull()
          .flatMap (energyReadingsRepository::retrieve )
          .onFailure().recoverWithItem { _ -> EnergyReading.fallbackReading(energyReport.deviceSn) }
          .map { mostRecentReading -> mostRecentReading.nextReading(energyReport) }
          .flatMap (energyReadingsRepository::createNew )
          .call { it -> deviceRepository.update(device.addReading(it.id!!)) }
      }
  }

}
