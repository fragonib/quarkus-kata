package com.bia.charger.sensor.model

import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import java.util.*
import kotlin.math.floor


typealias ReadingId = UUID

data class DeviceEnergyReport(
  val deviceSn: DeviceSN,
  val timestamp: OffsetDateTime,
  val energyCounter: Number,
)

data class EnergyReading(
  val timestamp: OffsetDateTime,
  val energyCounter: Number,
  val power: Power,
  val deviceId: DeviceSN,
) {

  companion object {
    fun first(deviceId: DeviceSN): EnergyReading {
      return EnergyReading(
        timestamp = OffsetDateTime.now(),
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
    val elapsedTime = ChronoUnit.MINUTES.between(this.timestamp, newReading.timestamp)
    return Power(
      watts = floor(energyDelivered.toDouble() / elapsedTime.toDouble()).toLong(),
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
