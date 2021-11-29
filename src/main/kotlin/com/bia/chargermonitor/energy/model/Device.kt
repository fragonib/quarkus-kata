package com.bia.chargermonitor.energy.model

import java.util.*


typealias DeviceSN = String

data class Device(
  val serialNumber: DeviceSN,
  private val sensorReadings: Stack<ReadingId> = Stack()
) {

  /**
   * Fresh new device without any readings yet
   */
  constructor(deviceSN: DeviceSN) : this(serialNumber = deviceSN)

  fun allReadings(): List<ReadingId> =
    sensorReadings.toList()

  fun mostRecentReading(): Optional<ReadingId> =
    if (sensorReadings.empty()) Optional.empty() else Optional.of(sensorReadings.peek())

  fun addReading(id: ReadingId): Device {
    sensorReadings.push(id)
    return this
  }

  fun addAll(readings: List<ReadingId>): Device {
    sensorReadings.addAll(readings)
    return this
  }

}
