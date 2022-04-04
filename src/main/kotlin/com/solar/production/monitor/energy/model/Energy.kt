package com.solar.production.monitor.energy.model

import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import java.util.*


typealias ReadingId = UUID

data class DeviceEnergyReport(
    val deviceSn: DeviceSN,
    val timestamp: OffsetDateTime,
    val energyCounter: Number,
)

data class EnergyReading(
    val id: ReadingId? = null,
    val timestamp: OffsetDateTime,
    val energyCounter: Number,
    val power: Power,
    val deviceId: DeviceSN,
) {

  companion object {
    fun fallbackReading(deviceId: DeviceSN): EnergyReading {
      return EnergyReading(
        timestamp = Instant.EPOCH.atOffset(ZoneOffset.UTC),
        energyCounter = 0L,
        power = Power(),
        deviceId = deviceId,
      )
    }
  }

  fun nextReading(energyReport: DeviceEnergyReport): EnergyReading {
    return EnergyReading(
      deviceId = energyReport.deviceSn,
      timestamp = energyReport.timestamp,
      energyCounter = energyReport.energyCounter,
      power = computePower(energyReport)
    )
  }

  private fun computePower(newReading: DeviceEnergyReport): Power {
    val energyDelivered = newReading.energyCounter.toLong() - this.energyCounter.toLong()
    if (energyDelivered < 0)
      throw UnacceptableReportException("New energy report [${newReading.energyCounter}] is less than last reading [${this.energyCounter}]!!")

    val elapsedTime = ChronoUnit.MINUTES.between(this.timestamp, newReading.timestamp)
    if (elapsedTime <= 0)
      throw UnacceptableReportException("New time report [${newReading.timestamp}] is set before last reading [${this.timestamp}]!!")

    return Power(
      watts = energyDelivered / elapsedTime,
      timeUnit = ChronoUnit.MINUTES
    )
  }

}


data class Power(
  val watts: Number,
  val timeUnit: TemporalUnit
) {
  constructor() : this(0L, ChronoUnit.MINUTES)
}

class UnacceptableReportException(message:String): Exception(message)
