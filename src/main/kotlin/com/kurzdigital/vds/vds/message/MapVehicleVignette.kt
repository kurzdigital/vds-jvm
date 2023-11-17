package com.kurzdigital.vds.vds.message

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.vds.getDate
import com.kurzdigital.vds.vds.getString

fun mapVehicleVignette(messages: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.EXPIRATION_DATE to (
        messages[1]?.getDate()
            ?: throw IllegalArgumentException("Missing expiration date")
        ),
    Label.CAR_LICENSE_PLATE to messages.getString(2),
    Label.CAR_MODEL to messages.getString(3),
    Label.CAR_COLOR to messages.getString(4),
    Label.OWNER to messages.getString(5),
)
