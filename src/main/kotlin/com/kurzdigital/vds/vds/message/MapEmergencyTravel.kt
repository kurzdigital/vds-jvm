package com.kurzdigital.vds.vds.message

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.security.decodeC40Td2

fun mapEmergencyTravel(messages: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.MRZ to (
        messages[2]?.decodeC40Td2()
            ?: throw IllegalArgumentException("Missing MRZ")
        ),
)
