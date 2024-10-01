package com.kurzdigital.vds.vds.feature

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.security.decodeC40

fun mapSocialInsuranceCard(features: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.SIN to (
        features[1]?.decodeC40()
            ?: throw IllegalArgumentException("Missing SIN")
        ),
    Label.SURNAME to String(
        features[2] ?: throw IllegalArgumentException("Missing surname"),
    ),
    Label.GIVEN_NAME to String(
        features[3] ?: throw IllegalArgumentException("Missing first name"),
    ),
    Label.BIRTH_NAME to String(
        features[4] ?: throw IllegalArgumentException("Missing birth name"),
    ),
)
