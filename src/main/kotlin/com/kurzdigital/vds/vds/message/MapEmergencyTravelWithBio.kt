package com.kurzdigital.vds.vds.message

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.security.decodeC40Td2

fun mapEmergencyTravelWithBio(messages: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.MRZ to (
        messages[2]?.decodeC40Td2()
            ?: throw IllegalArgumentException("Missing MRZ")
        ),
    Label.BIOMETRICS to messages[3]
        ?: throw IllegalArgumentException("Missing biometrics")
)
