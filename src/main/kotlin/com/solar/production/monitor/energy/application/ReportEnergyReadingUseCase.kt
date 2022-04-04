package com.solar.production.monitor.energy.application

import com.solar.production.monitor.energy.model.DeviceEnergyReport
import com.solar.production.monitor.energy.model.EnergyReading
import com.solar.production.monitor.shared.notNull
import com.solar.production.monitor.shared.toUni
import io.quarkus.vertx.ConsumeEvent
import io.smallrye.mutiny.Uni
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class ReportEnergyReadingUseCase(
  private val deviceRepository: DeviceRepository,
  private val energyReadingsRepository: EnergyReadingsRepository
) {

  @ConsumeEvent("DeviceEnergyReport")
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
