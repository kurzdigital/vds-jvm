package com.kurzdigital.vds.vds.message

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.vds.getDate
import com.kurzdigital.vds.vds.getString

fun mapHealthInsuranceCard(messages: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.SERIAL_NUMBER to messages.getString(1),
    Label.NAME to messages.getString(2),
    Label.DATE_OF_BIRTH to (
        messages[3]?.getDate()
            ?: throw IllegalArgumentException("Missing date of birth")
        ),
    Label.BENEFITS to messages.getString(4),
    Label.ACCOMMODATION to messages.getString(5),
    Label.EXPIRATION_DATE to (
        messages[6]?.getDate()
            ?: throw IllegalArgumentException("Missing expiration date")
        ),
    Label.SERVICE_NETWORK to messages.getString(7),
    Label.BIOMETRICS to messages[8],
)
