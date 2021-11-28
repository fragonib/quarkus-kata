package com.bia.charger.sensor.infrastructure.delivery

import java.lang.reflect.Type
import java.time.OffsetDateTime
import javax.ws.rs.ext.ParamConverter
import javax.ws.rs.ext.ParamConverterProvider
import javax.ws.rs.ext.Provider

@Provider
class DateParamConverterProvider : ParamConverterProvider {

  override fun <T : Any> getConverter(
    rawType: Class<T>,
    genericType: Type,
    annotations: Array<out Annotation>
  ): ParamConverter<T>? {

    if (rawType.name == "java.time.OffsetDateTime") {
      return DateParamConverter() as ParamConverter<T>
    }
    return null
  }

}

class DateParamConverter : ParamConverter<OffsetDateTime> {
  override fun toString(value: OffsetDateTime?): String {
    return value?.toString() ?: ""
  }

  override fun fromString(value: String?): OffsetDateTime {
    return OffsetDateTime.parse(value)
  }

}
