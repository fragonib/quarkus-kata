package com.bia.charger.sensor.application

import com.bia.charger.sensor.model.*
import io.smallrye.mutiny.Uni
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.*


@ExtendWith(MockitoExtension::class)
internal class ReportEnergyReadingUseCaseTest {

  @Mock
  lateinit var deviceRepository: DeviceRepository

  @Mock
  lateinit var energyReadingsRepository: EnergyReadingsRepository

  @Test
  fun `should generate next energy reading`() {

    // Given
    val deviceSN = "dummy"

    val previousTimestamp = OffsetDateTime.now()
    val previousReadingId = UUID.randomUUID()
    val previousEnergyCounter = 100L
    val previousEnergyReading = EnergyReading(
      id = previousReadingId,
      deviceId = deviceSN,
      timestamp = previousTimestamp,
      energyCounter = previousEnergyCounter,
      power = anyPreviousPower(),
    )

    val nextTimestamp = previousTimestamp.plusMinutes(10)
    val nextEnergyCounter = 1100L
    val nextReadingId = UUID.randomUUID()
    setEnergyReadingsRepoBehaviour(previousEnergyReading, nextReadingId)

    val device = Device(serialNumber = deviceSN).addReading(previousReadingId)
    setDeviceRepoBehaviour(device)

    // When
    val useCase = ReportEnergyReadingUseCase(deviceRepository, energyReadingsRepository)
    val responseEnergyReading = useCase.reportEnergyReading(
      DeviceEnergyReport(
        deviceSn = deviceSN,
        timestamp = nextTimestamp,
        energyCounter = nextEnergyCounter
      )
    ).await().indefinitely()

    // Then behaviour
    assertThat(responseEnergyReading).isEqualTo(
      EnergyReading(
        id = nextReadingId,
        deviceId = deviceSN,
        timestamp = nextTimestamp,
        energyCounter = nextEnergyCounter,
        power = Power(100L, ChronoUnit.MINUTES),
      )
    )

    // Then interactions
    verify(deviceRepository, times(1)).retrieveOrCreateNew(any())
    verify(deviceRepository, times(1)).update(any())

    verify(energyReadingsRepository, times(1)).retrieve(any())
    verify(energyReadingsRepository, times(1)).createNew(any())
  }

  private fun setDeviceRepoBehaviour(device: Device) {
    `when`(deviceRepository.retrieveOrCreateNew(any()))
      .thenReturn(Uni.createFrom().item(device))

    `when`(deviceRepository.update(any()))
      .then { Uni.createFrom().item(it.getArgument(0, Device::class.java)) }
  }

  private fun setEnergyReadingsRepoBehaviour(
    previousEnergyReading: EnergyReading,
    nextReadingId: UUID?
  ): ReadingId {
    `when`(energyReadingsRepository.retrieve(any()))
      .thenReturn(Uni.createFrom().item(previousEnergyReading))

    `when`(energyReadingsRepository.createNew(any()))
      .then { Uni.createFrom().item(it.getArgument(0, EnergyReading::class.java).copy(id = nextReadingId)) }

    return previousEnergyReading.id!!
  }

  private fun anyPreviousPower() = Power(Random().nextLong(), ChronoUnit.MINUTES)

}
