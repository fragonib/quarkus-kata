package com.bia.chargermonitor.energy.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

internal class DeviceTest {

  @Test
  fun `should use LIFO like energy readings`() {

    // Given
    val device = Device("device-serial-number")
      .addReading(UUID.fromString("f3e6cf58-764e-4c6a-a2a3-484b9237dcbf"))
      .addAll(
        listOf(
          UUID.fromString("46a62f3b-4adf-4658-abcd-e119e8614512"),
          UUID.fromString("4ecc9fe0-8b49-4999-ab97-f33f78267f16"),
          UUID.fromString("3dea0b97-9dd0-499f-a872-28cdc25adc56"),
        )
      )

    // Then
    assertThat(device.allReadings()).hasSize(4)
    assertThat(device.mostRecentReading())
      .isPresent
      .get().isEqualTo(UUID.fromString("3dea0b97-9dd0-499f-a872-28cdc25adc56"))

  }

  @Test
  fun `should return empty optional where energy readings are empty`() {

    // Given
    val device = Device("device-serial-number")

    // Then
    assertThat(device.allReadings()).isEmpty()
    assertThat(device.mostRecentReading()).isNotPresent

  }

}
