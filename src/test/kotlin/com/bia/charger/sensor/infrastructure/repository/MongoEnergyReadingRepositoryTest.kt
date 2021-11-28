package com.bia.charger.sensor.infrastructure.repository

import com.bia.charger.sensor.model.EnergyReading
import com.bia.charger.sensor.model.Power
import io.quarkus.test.junit.QuarkusTest
import org.assertj.core.api.Assertions.assertThat
import org.jboss.logging.Logger
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject


@QuarkusTest
internal class MongoEnergyReadingRepositoryTest {

  private val log = Logger.getLogger(MongoEnergyReadingRepositoryTest::class.java)

  @Inject
  internal lateinit var repo: MongoEnergyReadingsRepository

  @Test
  fun `should create new sensor reading`() {

    // Given
    val sn = UUID.randomUUID().toString()
    val energyReading = EnergyReading(
      deviceId = sn,
      timestamp = OffsetDateTime.now(),
      energyCounter = 100L,
      power = Power(100, ChronoUnit.MINUTES),
    )

    // When
    val persistedDevice = repo.createNew(energyReading)
      .await().indefinitely()

    // Then
    assertThat(persistedDevice.id).isNotNull
    assertThat(persistedDevice).usingRecursiveComparison().ignoringFields(EnergyReading::id.name)

    val retrievedItem = repo.retrieve(persistedDevice.id!!)
      .await().indefinitely()
    assertThat(retrievedItem.id).isNotNull
    assertThat(retrievedItem).usingRecursiveComparison().ignoringFields(EnergyReading::id.name)

  }

}
