package com.bia.charger.sensor.infrastructure

import com.bia.charger.sensor.application.ReportEnergyReadingUseCase
import com.bia.charger.sensor.model.DeviceEnergyReport
import com.fasterxml.jackson.annotation.JsonProperty
import io.smallrye.mutiny.Uni
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.core.*


@Path("/api/sensors")
@Tag(name = "sensors")
class ReportEnergyResource {

  private val log = Logger.getLogger(ReportEnergyReadingUseCase::class.java)

  @Inject
  lateinit var useCase: ReportEnergyReadingUseCase

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Operation(summary = "Receives & registers particular sensor report", )
  fun reportEnergyReading(sensorReport: ParticularSensorReport, @Context uriInfo: UriInfo): Uni<Response> {
    log.info("Received particular sensor report: $sensorReport")
    return useCase
      .reportEnergyReading(
        DeviceEnergyReport(
          deviceSn = sensorReport.deviceSN,
          timestamp = sensorReport.timestamp,
          energyCounter = sensorReport.energy
        )
      )
      .map { registeredReading ->
        val uriBuilder = uriInfo.absolutePathBuilder
        Response
          .created(uriBuilder.path(registeredReading.id.toString()).build())
          .entity(registeredReading)
          .build()
      }
  }

}

data class ParticularSensorReport(
  @JsonProperty(required = true, value = "device_sn") val deviceSN: String,
  @JsonProperty(required = true) val timestamp: OffsetDateTime,
  @JsonProperty(required = true) val energy: Long
)
