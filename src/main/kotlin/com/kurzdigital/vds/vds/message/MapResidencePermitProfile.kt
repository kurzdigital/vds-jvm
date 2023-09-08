package com.kurzdigital.vds.vds.message

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.security.decodeC40
import com.kurzdigital.vds.security.decodeC40Td2

fun mapResidencePermitProfile(messages: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.MRZ to (
        messages[2]?.decodeC40Td2()
            ?: throw IllegalArgumentException("Missing MRZ")
        ),
    Label.PASSPORT_NUMBER to (
        messages[3]?.decodeC40()
            ?: throw IllegalArgumentException("Missing passport number")
        ),
)
