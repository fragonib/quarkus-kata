package com.bia.charger.sensor.application

import com.bia.charger.sensor.model.Device
import com.bia.charger.sensor.model.DeviceSN
import io.smallrye.mutiny.Uni

interface DeviceRepository {

  /**
   * Retrieves a registered device info for given [deviceSN]
   *
   * @param deviceSN Device serial number
   *
   * @return Reactive response, fails if not registered
   */
  fun retrieve(deviceSN: DeviceSN): Uni<Device>

  /**
   * Retrieves device info or registers a device info for given its [deviceSN]
   *
   * @param deviceSN Device serial number
   *
   * @return Reactive response, retriever or registered device
   */
  fun retrieveOrCreateNew(deviceSN: DeviceSN): Uni<Device>

  /**
   * Registers a fresh new device with no readings
   *
   * @param deviceSN Device serial number
   *
   * @return New fresh device
   */
  fun createNew(deviceSN: DeviceSN): Uni<Device>

  /**
   * Updates an existing device (identified by its sn) with new energy reading data
   *
   * @param device Device new energy reading data
   *
   * @return Updated device data
   */
  fun update(device: Device): Uni<Device>

}
