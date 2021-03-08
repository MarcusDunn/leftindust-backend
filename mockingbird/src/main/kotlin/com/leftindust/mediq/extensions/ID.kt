package com.leftindust.mediq.extensions

import com.expediagroup.graphql.scalars.ID

fun gqlID(id: Int): ID = ID(id.toString())

fun gqlID(id: Long): ID = ID(id.toString())

fun ID.toInt() = this.value.toInt()

fun ID.toLong() = this.value.toLong()

