package com.bia.chargermonitor.energy.infrastructure.delivery

import com.bia.chargermonitor.energy.application.ReportEnergyReadingUseCase
import com.bia.chargermonitor.energy.model.EnergyReading
import com.bia.chargermonitor.energy.model.Power
import com.bia.chargermonitor.energy.model.UnacceptableReportException
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
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status


@QuarkusTest
internal class ParticularSensorResourceTest {

  @Inject
  lateinit var mapper: ObjectMapper

  @InjectMock
  lateinit var useCase: ReportEnergyReadingUseCase

  @Test
  fun `should respond OK with energy reading body when report is right`() {

    // Given
    val coherentReport = ParticularSensorReport(
      deviceSN = "34f8f749-0eb1-4edd-acfe-17b05c407917",
      timestamp = OffsetDateTime.now(),
      energy = 30L
    )
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
      .body(mapper.writeValueAsString(coherentReport))
      .contentType(ContentType.JSON)
      .`when`()
      .put("/api/sensors")

    // Then
      .then()
      .log().everything()
      .statusCode(201)
  }

  @Test
  fun `should respond NOT_ACCEPTABLE when report is wrong`() {

    // Given
    val incoherentSensorReport = ParticularSensorReport(
      deviceSN = "34f8f749-0eb1-4edd-acfe-17b05c407917",
      timestamp = OffsetDateTime.now(),
      energy = 30L
    )
    `when`(useCase.reportEnergyReading(any()))
      .thenReturn(Uni.createFrom().failure(UnacceptableReportException("Not coherent sensor report")))

    // When
    given()
      .body(mapper.writeValueAsString(incoherentSensorReport))
      .contentType(ContentType.JSON)
      .`when`()
      .put("/api/sensors")

    // Then
      .then()
      .log().everything()
      .statusCode(Status.NOT_ACCEPTABLE.statusCode)
  }

}
