package com.bia.chargermonitor.energy.infrastructure.delivery

import com.bia.chargermonitor.energy.application.ReportEnergyReadingUseCase
import com.bia.chargermonitor.energy.model.EnergyReading
import com.bia.chargermonitor.energy.model.Power
import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.mockito.InjectMock
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.smallrye.mutiny.Uni
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject


@QuarkusTest
internal class ParticularSensorResourceTest {

  @Inject
  lateinit var mapper: ObjectMapper

  @InjectMock
  lateinit var useCase: ReportEnergyReadingUseCase

  @Test
  fun `charger sensor can report a reading`() {

    // Given
    `when`(useCase.reportEnergyReading(any()))
      .thenReturn(
        Uni.createFrom().item(
          EnergyReading(
            id = UUID.randomUUID(),
            timestamp = OffsetDateTime.now(),
            energyCounter = 5L,
            power = Power(10L, ChronoUnit.MINUTES),
            deviceId = "dummy-sn",
          )
        )
      )

    // When
    given()
      .body(
        mapper.writeValueAsString(
          ParticularSensorReport(
            deviceSN = "34f8f749-0eb1-4edd-acfe-17b05c407917",
            timestamp = OffsetDateTime.now(),
            energy = 30L
          )
        )
      )
      .contentType(ContentType.JSON)
      .`when`()
      .put("/api/sensors")

      // Then
      .then()
      .log().everything()
      .statusCode(201)
  }

}
