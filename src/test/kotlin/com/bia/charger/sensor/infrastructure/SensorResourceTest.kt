package com.bia.charger.sensor.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import javax.inject.Inject


@QuarkusTest
internal class SensorResourceTest {

  @Inject
  private lateinit var mapper: ObjectMapper

  @Test
  fun `charger sensor can report a reading`() {
    given()
      .body(
        mapper.writeValueAsString(
          EnergyReadingReport(
            device_sn = "34f8f749-0eb1-4edd-acfe-17b05c407917",
            timestamp = OffsetDateTime.now(),
            energy = 30L
          )
        )
      )
      .contentType(ContentType.JSON)
      .`when`().put("/api/v1/sensor")
      .then()
      .log().everything()
      .statusCode(201)
  }

}
