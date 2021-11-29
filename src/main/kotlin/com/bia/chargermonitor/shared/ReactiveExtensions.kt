package com.bia.chargermonitor.shared

import io.smallrye.mutiny.Uni
import java.util.*


fun <T> Uni<T?>.notNull(): Uni<T> {
  return this.onItem().ifNull().fail()
    .onItem().ifNotNull().transform { it!! }
}

fun <T> Optional<T>.toUni(): Uni<T?> {
  return Uni.createFrom().item { this.orElseGet(null) }
}
