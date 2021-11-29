package com.bia.chargermonitor.energy.infrastructure.delivery

import com.bia.chargermonitor.energy.application.FindEnergyReadingUseCase
import com.bia.chargermonitor.energy.model.EnergyReading
import com.bia.chargermonitor.energy.model.Power
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.mockito.InjectMock
import io.restassured.RestAssured
import io.smallrye.mutiny.Multi
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.*


@QuarkusTest
internal class EnergyReadingsResourceTest {

  @InjectMock
  lateinit var useCase: FindEnergyReadingUseCase

  @Test
  fun `should return when no results`() {

    // Given
    Mockito.`when`(useCase.findEnergyReadings(any(), any(), any()))
      .thenReturn(Multi.createFrom().empty())

    // When
    RestAssured.given()
      .`when`()
      .queryParam("deviceId", "dummy-sn")
      .queryParam("from", anyDate())
      .queryParam("to", anyDate())
      .get("/api/readings")

      // Then
      .then()
      .log().everything()
      .statusCode(200)
      .body("$.size()", `is`(0))

  }

  @Test
  fun `should return when results`() {

    // Given
    Mockito.`when`(useCase.findEnergyReadings(any(), any(), any()))
      .thenReturn(Multi.createFrom()
        .items(
          EnergyReading(
            id = UUID.randomUUID(),
            deviceId = "dummy-sn",
            timestamp = OffsetDateTime.now(),
            energyCounter = 500L,
            power = Power(10L, ChronoUnit.MINUTES),
          ),
          EnergyReading(
            id = UUID.randomUUID(),
            deviceId = "dummy-sn",
            timestamp = OffsetDateTime.now().plusMinutes(10),
            energyCounter = 1000L,
            power = Power(10L, ChronoUnit.SECONDS),
          )
        ))

    // When
    RestAssured.given()
      .`when`()
      .queryParam("deviceId", "dummy-sn")
      .queryParam("from", anyDate())
      .queryParam("to", anyDate())
      .get("/api/readings")

      // Then
      .then()
      .log().everything()
      .statusCode(200)
      .body("$.size()", `is`(2))

  }

  private fun anyDate() = OffsetDateTime.now().toString()

}
