package com.bia.charger.sensor.model

import java.util.*

typealias DeviceSN = String

class Device(
  val serialNumber: DeviceSN,
  val sensorReadings: MutableList<ReadingId> = mutableListOf()
) {

  fun mostRecentReading(): Optional<ReadingId> =
    sensorReadings.stream().findFirst()

  fun addReading(id: ReadingId): Device {
    sensorReadings.add(id)
    return this
  }

}
