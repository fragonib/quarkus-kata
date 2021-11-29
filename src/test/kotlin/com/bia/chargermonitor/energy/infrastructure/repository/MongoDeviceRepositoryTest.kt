package com.bia.chargermonitor.energy.infrastructure.repository

import com.bia.chargermonitor.energy.model.Device
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*
import javax.inject.Inject


@QuarkusTest
internal class MongoDeviceRepositoryTest {

  @Inject
  internal lateinit var repo: MongoDeviceRepository

  @Test
  fun `should create new device given its serial number`() {

    // Given
    val sn = UUID.randomUUID().toString()

    // When
    val persistedDevice = repo.createNew(sn)
      .await().indefinitely()

    // Then

    val expectedDevice = Device(serialNumber = sn)
    val registeredDevice = repo.retrieve(sn)
    registeredDevice
      .subscribe().withSubscriber(UniAssertSubscriber.create())
      .awaitItem()
      .assertItem(expectedDevice)
      .assertCompleted()
  }

  @Test
  fun `should update a device with new readings`() {

    // Given
    val sn = UUID.randomUUID().toString()
    val newDevice = repo.createNew(sn).await().indefinitely()

    // When
    val modifiedDevice = newDevice
      .addReading(UUID.fromString("ffd30bb4-5ff6-4dc3-bbfd-50778d33df8b"))
      .addReading(UUID.fromString("b46ced9d-03f8-49ed-b9c2-f43abbc738bb"))
    repo.update(modifiedDevice).await().indefinitely()

    // Then
    val registeredDevice = repo.retrieve(sn).await().indefinitely()
    assertThat(registeredDevice.allReadings()).containsExactly(
      UUID.fromString("ffd30bb4-5ff6-4dc3-bbfd-50778d33df8b"),
      UUID.fromString("b46ced9d-03f8-49ed-b9c2-f43abbc738bb"),
    )
  }

}
