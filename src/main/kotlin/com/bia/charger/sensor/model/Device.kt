package com.bia.charger.sensor.model

import java.util.*


typealias DeviceSN = String

class Device(
  val serialNumber: DeviceSN,
  val sensorReadings: Stack<ReadingId> = Stack()
) {

  /**
   * Fresh new device without any readings yet
   */
  constructor(deviceSN: DeviceSN) : this(serialNumber = deviceSN)

  fun mostRecentReading(): Optional<ReadingId> =
    if (sensorReadings.empty()) Optional.empty() else Optional.of(sensorReadings.peek())

  fun addReading(id: ReadingId): Device {
    sensorReadings.push(id)
    return this
  }

}
