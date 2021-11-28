package com.bia.charger.sensor.infrastructure.delivery

import com.bia.charger.sensor.application.FindEnergyReadingUseCase
import com.bia.charger.sensor.model.DeviceSN
import com.bia.charger.sensor.model.EnergyReading
import io.smallrye.mutiny.Uni
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.UriInfo


@Path("/api/readings")
@Tag(name = "readings")
class EnergyReadingsResource {

  private val log = Logger.getLogger(EnergyReadingsResource::class.java)

  @Inject
  lateinit var useCase: FindEnergyReadingUseCase

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Finds energy readings by timestamp")
  fun findEnergyReadings(
    @QueryParam("deviceId") deviceSN: DeviceSN?,
    @QueryParam("from") from: OffsetDateTime?,
    @QueryParam("to") to: OffsetDateTime?,
    @Context uriInfo: UriInfo
  ): Uni<List<EnergyReading>> {

    if (deviceSN == null || from == null || to == null)
      return Uni.createFrom().failure(BadRequestException())

    log.info("Received particular find energy point query: device [${deviceSN}] between [${from} - ${to}]")

    return useCase.findEnergyReadings(deviceSN, from, to)
      .collect().asList()

  }

}

class FindRequest(
  @QueryParam("from") var from: OffsetDateTime,
  @QueryParam("to") var to: OffsetDateTime,
  @QueryParam("deviceId") var deviceSN: DeviceSN,
)
