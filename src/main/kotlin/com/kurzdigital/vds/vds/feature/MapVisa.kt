package com.kurzdigital.vds.vds.feature

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.security.decodeC40
import com.kurzdigital.vds.security.decodeC40MrvA
import com.kurzdigital.vds.security.decodeC40MrvB
import com.kurzdigital.vds.vds.getUInt24LittleEndian

fun mapVisa(features: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.MRZ to (
        (
            features[1]?.decodeC40MrvA()
                ?: features[2]?.decodeC40MrvB()
            )
            ?: throw IllegalArgumentException("Missing MRZ")
        ),
    Label.NUMBER_OF_ENTRIES to features[3]?.get(0),
    Label.DURATION_OF_STAY to (
        features[4]?.getUInt24LittleEndian()
            ?: throw IllegalArgumentException("Missing duration of stay")
        ),
    Label.PASSPORT_NUMBER to (
        features[5]?.decodeC40()
            ?: throw IllegalArgumentException("Missing passport number")
        ),
    Label.VISA_TYPE to features[6],
    Label.ADDITIONAL_FEATURE to features[7],
)
