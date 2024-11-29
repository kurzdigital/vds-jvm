package com.kurzdigital.vds.vds.feature

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.security.decodeC40
import com.kurzdigital.vds.security.decodeC40Td2

fun mapResidencePermitProfile(features: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.MRZ to (
        features[2]?.decodeC40Td2()
            ?: throw IllegalArgumentException("Missing MRZ")
        ),
    Label.PASSPORT_NUMBER to (
        features[3]?.decodeC40()
            ?: throw IllegalArgumentException("Missing passport number")
        ),
)