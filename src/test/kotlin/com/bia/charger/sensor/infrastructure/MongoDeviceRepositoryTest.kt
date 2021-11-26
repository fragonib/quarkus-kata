package com.bia.charger.sensor.infrastructure

import com.bia.charger.sensor.model.Device
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber
import org.junit.jupiter.api.Test
import javax.inject.Inject


@QuarkusTest
internal class MongoDeviceRepositoryTest {

  @Inject
  internal lateinit var repo: MongoDeviceRepository

    @Test
    fun retrieve() {
      val sn = "myserialnumber"
      val createNew = repo.createNew(sn)

      createNew
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .assertCompleted().assertItem(
          Device(
            serialNumber = sn
          )
        )
    }

}
