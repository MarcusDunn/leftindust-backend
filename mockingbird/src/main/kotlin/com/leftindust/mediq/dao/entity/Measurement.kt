package com.leftindust.mediq.dao.entity

import javax.persistence.Embeddable

@Embeddable
class Measurement(
    var magnitude: Float,
    var unit: String,
)