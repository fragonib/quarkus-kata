package com.bia.charger.sensor.infrastructure

import com.bia.charger.sensor.model.Device
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber
import org.junit.jupiter.api.Test
import javax.inject.Inject


@QuarkusTest
internal class MongoDeviceRepositoryTest {

  @Inject
  internal lateinit var repo: MongoDeviceRepository

  @Test
  fun `should create new device given its serial number`() {

    // Given
    val sn = "device-serial-number"

    // When
    val persistedDevice = repo.createNew(sn)

    // Then
    val expectedDevice = Device(serialNumber = sn)
    persistedDevice
      .subscribe().withSubscriber(UniAssertSubscriber.create())
      .awaitItem()
      .assertItem(expectedDevice)
      .assertCompleted()

    val registeredDevice = repo.retrieve(sn)
    registeredDevice
      .subscribe().withSubscriber(UniAssertSubscriber.create())
      .awaitItem()
      .assertItem(expectedDevice)
      .assertCompleted()
  }

}
