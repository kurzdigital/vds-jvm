package com.kurzdigital.vds.vds.feature

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.vds.getDate
import com.kurzdigital.vds.vds.getString

fun mapDemecanPatientId(features: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.DOCUMENT_NUMBER to features.getString(1),
    Label.NAME to features.getString(2),
    Label.DATE_OF_BIRTH to (
        features[3]?.getDate()
            ?: throw IllegalArgumentException("Missing date of birth")
        ),
    Label.ADDRESS_STREET to features.getString(4),
    Label.ADDRESS_CITY to features.getString(5),
)
