package com.kurzdigital.vds.vds.feature

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.vds.getDate
import com.kurzdigital.vds.vds.getString

fun mapVehicleVignette(features: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.EXPIRATION_DATE to (
        features[1]?.getDate()
            ?: throw IllegalArgumentException("Missing expiration date")
        ),
    Label.CAR_LICENSE_PLATE to features.getString(2),
    Label.CAR_MODEL to features.getString(3),
    Label.CAR_COLOR to features.getString(4),
    Label.OWNER to features.getString(5),
)
