package com.bia.charger.sensor.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

internal class EnergyReadingTest {

  @Test
  fun `should calculate next reading from new report`() {

    // Given
    val deviceId = "dummy-sn"
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

  @Test
  fun `should complain when report timestamp is before previous`() {

    // Given
    val deviceId = "dummy-sn"
    val currentTick = OffsetDateTime.now()
    val timestampBeforePrevious = currentTick.minusMinutes(30)
    val lastReading = EnergyReading(
      timestamp = currentTick,
      energyCounter = 4000L,
      power = Power(),
      deviceId = deviceId,
    )

    // When
    val catchThrowable = catchThrowable {
      lastReading.nextReading(
        DeviceEnergyReport(
          timestamp = timestampBeforePrevious,
          deviceSn = deviceId,
          energyCounter = 10000L
        )
      )
    }

    // Then
    assertThat(catchThrowable).isNotNull
  }

  @Test
  fun `should complain when report energy is less than previous`() {

    // Given
    val deviceId = "dummy-sn"
    val currentTick = OffsetDateTime.now()
    val beforeEnergyCounter = 1000L
    val nextEnergyCounter = 900L

    val lastReading = EnergyReading(
      timestamp = currentTick,
      energyCounter = beforeEnergyCounter,
      power = Power(),
      deviceId = deviceId,
    )

    // When
    val catchThrowable = catchThrowable {
      lastReading.nextReading(
        DeviceEnergyReport(
          timestamp = currentTick.plusMinutes(30),
          deviceSn = deviceId,
          energyCounter = nextEnergyCounter
        )
      )
    }

    // Then
    assertThat(catchThrowable).isNotNull
  }

}
