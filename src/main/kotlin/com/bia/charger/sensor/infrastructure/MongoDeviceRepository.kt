package com.bia.charger.sensor.infrastructure

import com.bia.charger.sensor.application.DeviceRepository
import com.bia.charger.sensor.model.Device
import com.bia.charger.sensor.model.DeviceSN
import com.bia.charger.sensor.model.ReadingId
import com.bia.charger.shared.notNull
import io.quarkus.mongodb.panache.common.MongoEntity
import io.quarkus.mongodb.panache.kotlin.reactive.ReactivePanacheMongoRepository
import io.smallrye.mutiny.Uni
import org.bson.codecs.pojo.annotations.BsonId
import java.util.*
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class MongoDeviceRepository
  : ReactivePanacheMongoRepository<MongoDevice>, DeviceRepository {

  override fun retrieve(deviceSN: DeviceSN): Uni<Device> {
    return find("serialNumber", deviceSN)
      .firstResult()
      .notNull()
      .map { it.toModel() }
  }

  override fun retrieveOrCreateNew(deviceSN: DeviceSN): Uni<Device> {
    return retrieve(deviceSN)
      .onFailure().recoverWithUni { _ -> createNew(deviceSN) }
  }

  override fun createNew(deviceSN: DeviceSN): Uni<Device> {
    val newDevice = Device(serialNumber = deviceSN)
    return persist(MongoDevice.fromModel(newDevice))
      .map { it.toModel() }
  }

  override fun update(device: Device): Uni<Device> {
    val entity = MongoDevice.fromModel(device)
    return super.update(entity)
      .map { it.toModel() }
  }

}

@MongoEntity(collection = "devices")
data class MongoDevice(
  @field:BsonId
  val serialNumber: DeviceSN,
  val sensorReadings: Stack<ReadingId>
) {

  companion object {
    fun fromModel(device: Device): MongoDevice {
      return MongoDevice(
        serialNumber = device.serialNumber,
        sensorReadings = device.sensorReadings
      )
    }
  }

  fun toModel(): Device {
    return Device(
      serialNumber = serialNumber,
      sensorReadings = sensorReadings
    )
  }
}
