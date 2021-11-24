package com.bia.charger.sensor.application

import java.time.OffsetDateTime
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ReportEnergyReadingUseCase {
  fun report(deviceEnergyReport: DeviceEnergyReport): DeviceEnergyReport {
    return deviceEnergyReport
  }
}

data class DeviceEnergyReport(
  val id: UUID = UUID.randomUUID(),
  val timestamp: OffsetDateTime,
  val device: Device,
  val energyCounter: Number,
)

data class Device(
  val sn: String
)
