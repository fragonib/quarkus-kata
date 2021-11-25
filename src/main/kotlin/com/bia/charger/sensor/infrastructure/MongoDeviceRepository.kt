package com.bia.charger.sensor.infrastructure

import com.bia.charger.sensor.application.DeviceRepository
import com.bia.charger.sensor.model.Device
import com.bia.charger.sensor.model.DeviceSN
import com.bia.charger.sensor.model.notNull
import io.quarkus.mongodb.panache.kotlin.reactive.ReactivePanacheMongoRepositoryBase
import io.smallrye.mutiny.Uni
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class MongoDeviceRepository
  : ReactivePanacheMongoRepositoryBase<Device, DeviceSN>, DeviceRepository
{

  override fun retrieve(deviceSN: DeviceSN): Uni<Device> {
    return findById(deviceSN).notNull()
  }

  override fun retrieveOrCreateNew(deviceSN: DeviceSN): Uni<Device> {
    return retrieve(deviceSN)
      .onFailure().recoverWithUni { _ -> createNew(deviceSN) }
  }

  override fun createNew(deviceSN: DeviceSN): Uni<Device> {
    return persist(Device(deviceSN))
  }

}
