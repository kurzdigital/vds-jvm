package com.kurzdigital.vds.vds.message

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.security.decodeC40

fun mapSocialInsuranceCard(messages: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.SIN to (
        messages[1]?.decodeC40()
            ?: throw IllegalArgumentException("Missing SIN")
        ),
    Label.SURNAME to String(
        messages[2] ?: throw IllegalArgumentException("Missing surname"),
    ),
    Label.GIVEN_NAME to String(
        messages[3] ?: throw IllegalArgumentException("Missing first name"),
    ),
    Label.BIRTH_NAME to String(
        messages[4] ?: throw IllegalArgumentException("Missing birth name"),
    ),
)
