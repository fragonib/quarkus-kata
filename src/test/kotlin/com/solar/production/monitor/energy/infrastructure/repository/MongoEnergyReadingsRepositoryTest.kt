package com.solar.production.monitor.energy.infrastructure.repository

import com.solar.production.monitor.energy.model.EnergyReading
import com.solar.production.monitor.energy.model.Power
import io.quarkus.test.junit.QuarkusTest
import org.assertj.core.api.Assertions.assertThat
import org.jboss.logging.Logger
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject


@QuarkusTest
internal class MongoEnergyReadingsRepositoryTest {

  private val log = Logger.getLogger(MongoEnergyReadingsRepositoryTest::class.java)

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

  @Test
  fun `should find readings`() {

    // Given
    val initialTimestamp = OffsetDateTime.parse("2021-11-20T12:00:00Z")
    val readingIdA = UUID.randomUUID()
    val readingIdB = UUID.randomUUID()
    val readingIdC = UUID.randomUUID()
    repo.createNew(readingFor(readingIdA, initialTimestamp))
      .call { _ -> repo.createNew(readingFor(readingIdB, initialTimestamp.plusMinutes(30))) }
      .call { _ -> repo.createNew(readingFor(readingIdC, initialTimestamp.plusMinutes(60))) }
      .await().indefinitely()

    // When
    val results = repo.findBetween("dummy-sn", initialTimestamp, initialTimestamp.plusMinutes(40))
      .collect().asList()
      .await().indefinitely()

    // Then
    assertThat(results)
      .isNotNull
      .hasSize(2)
      .extracting<UUID> { ita: EnergyReading -> ita.id }
      .containsExactly(readingIdB, readingIdA)

  }

  private fun readingFor(readingId: UUID, timestamp: OffsetDateTime): EnergyReading {
    return EnergyReading(
      id = readingId,
      deviceId = "dummy-sn",
      timestamp = timestamp,
      energyCounter = 100L,
      power = Power(100, ChronoUnit.MINUTES),
    )
  }

}
