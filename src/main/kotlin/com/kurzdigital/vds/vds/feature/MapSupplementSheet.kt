package com.kurzdigital.vds.vds.feature

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.security.decodeC40
import com.kurzdigital.vds.security.decodeC40Td2

fun mapSupplementSheet(features: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.MRZ to (
        features[4]?.decodeC40Td2()
            ?: throw IllegalArgumentException("Missing MRZ")
        ),
    Label.SHEET_NUMBER to (
        features[5]?.decodeC40()
            ?: throw IllegalArgumentException("Missing sheet number")
        ),
)
