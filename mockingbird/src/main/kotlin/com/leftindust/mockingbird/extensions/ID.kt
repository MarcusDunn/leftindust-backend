package com.leftindust.mockingbird.extensions

import com.expediagroup.graphql.generator.scalars.ID


fun gqlID(id: Int): ID = ID(id.toString())

fun gqlID(id: Long): ID = ID(id.toString())

fun ID.toLong() = this.value.toLong()

