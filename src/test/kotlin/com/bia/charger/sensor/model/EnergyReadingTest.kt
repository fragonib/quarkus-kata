package com.bia.charger.sensor.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

internal class EnergyReadingTest {

  @Test
  fun nextReading() {

    // Given
    val deviceId = "fe1d592b-3aed-4260-98f0-305e75b35bc1"
    val currentTick = OffsetDateTime.now()
    val nextTick = currentTick.plusMinutes(30)
    val lastReading = EnergyReading(
      timestamp = currentTick,
      energyCounter = 4000L,
      power = Power(),
      deviceId = deviceId,
    )

    // When
    val nextReading = lastReading.nextReading(
      DeviceEnergyReport(
        timestamp = nextTick,
        deviceSn = deviceId,
        energyCounter = 10000L
      )
    )

    // Then
    assertThat(nextReading).isEqualTo(
      EnergyReading(
        timestamp = nextTick,
        energyCounter = 10000L,
        power = Power(200L, ChronoUnit.MINUTES),
        deviceId = deviceId,
      )
    )
  }

}
