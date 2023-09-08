package com.kurzdigital.vds.vds.message

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.vds.getDateFromUInt24
import com.kurzdigital.vds.vds.getString
import com.kurzdigital.vds.vds.getUInt24

fun mapVehicleVignette(messages: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.EXPIRATION_DATE to (
        getDateFromUInt24(
            messages[1]?.getUInt24()
                ?: throw IllegalArgumentException("Missing expiration date"),
        ) ?: throw IllegalArgumentException("Invalid expiration date")
        ),
    Label.CAR_LICENSE_PLATE to messages.getString(2),
    Label.CAR_MODEL to messages.getString(3),
    Label.CAR_COLOR to messages.getString(4),
    Label.OWNER to messages.getString(5),
)
