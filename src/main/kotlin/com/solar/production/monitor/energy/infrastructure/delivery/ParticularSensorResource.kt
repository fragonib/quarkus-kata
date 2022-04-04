package com.solar.production.monitor.energy.infrastructure.delivery

import com.fasterxml.jackson.annotation.JsonProperty
import com.solar.production.monitor.energy.application.ReportEnergyReadingUseCase
import com.solar.production.monitor.energy.model.DeviceEnergyReport
import com.solar.production.monitor.energy.model.EnergyReading
import io.smallrye.mutiny.Uni
import io.vertx.mutiny.core.eventbus.EventBus
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo


@Path("/api/sensors")
@Tag(name = "sensors")
class ReportEnergyResource {

  private val log = Logger.getLogger(ReportEnergyReadingUseCase::class.java)

  @Inject
  lateinit var bus: EventBus

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Operation(summary = "Receives & registers particular sensor report")
  fun reportEnergyReading(sensorReport: ParticularSensorReport, @Context uriInfo: UriInfo): Uni<Response> {
    log.info("Received particular sensor report: $sensorReport")
    return bus.request<EnergyReading>(
      DeviceEnergyReport::class.java.simpleName,
      DeviceEnergyReport(
        deviceSn = sensorReport.deviceSN,
        timestamp = sensorReport.timestamp,
        energyCounter = sensorReport.energy
      )
    )
      .map { energyReading ->
        Response
          .status(Response.Status.CREATED)
          .entity(energyReading.body())
          .build()
      }
      .onFailure().recoverWithItem { th ->
        Response
          .status(Response.Status.NOT_ACCEPTABLE)
          .entity(mapOf("error" to th.message))
          .build()
      }
  }

}

data class ParticularSensorReport(
  @JsonProperty(required = true, value = "device_sn") val deviceSN: String,
  @JsonProperty(required = true) val timestamp: OffsetDateTime,
  @JsonProperty(required = true) val energy: Long
)
