package com.kurzdigital.vds.vds.feature

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.vds.getDate
import com.kurzdigital.vds.vds.getString

fun mapHealthInsuranceCard(features: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.SERIAL_NUMBER to features.getString(1),
    Label.NAME to features.getString(2),
    Label.DATE_OF_BIRTH to (
        features[3]?.getDate()
            ?: throw IllegalArgumentException("Missing date of birth")
        ),
    Label.BENEFITS to features.getString(4),
    Label.ACCOMMODATION to features.getString(5),
    Label.EXPIRATION_DATE to (
        features[6]?.getDate()
            ?: throw IllegalArgumentException("Missing expiration date")
        ),
    Label.SERVICE_NETWORK to features.getString(7),
    Label.BIOMETRICS to features[8],
)
