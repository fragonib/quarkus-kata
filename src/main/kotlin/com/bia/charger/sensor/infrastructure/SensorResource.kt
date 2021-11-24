package com.bia.charger.sensor.infrastructure

import com.bia.charger.sensor.application.Device
import com.bia.charger.sensor.application.DeviceEnergyReport
import com.bia.charger.sensor.application.ReportEnergyReadingUseCase
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.core.*


@Path("/api/v1/sensor")
class ReportEnergyResource {

  @Inject
  private lateinit var useCase: ReportEnergyReadingUseCase

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  suspend fun reportEnergyReading(reportData: EnergyReadingReport, @Context uriInfo: UriInfo): Response {
    val dataPoint = useCase.report(
      DeviceEnergyReport(
        device = Device(reportData.device_sn),
        timestamp = reportData.timestamp,
        energyCounter = reportData.energy
      )
    )

    val uriBuilder: UriBuilder = uriInfo.absolutePathBuilder
    uriBuilder.path(dataPoint.id.toString())
    return Response.created(uriBuilder.build()).build()
  }

}

data class EnergyReadingReport(
  val timestamp: OffsetDateTime,
  val device_sn: String,
  val energy: Long
)
