package com.kurzdigital.vds.vds.message

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.security.decodeC40
import com.kurzdigital.vds.security.decodeC40Td2

fun mapSupplementSheet(messages: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.MRZ to (
        messages[4]?.decodeC40Td2()
            ?: throw IllegalArgumentException("Missing MRZ")
        ),
    Label.SHEET_NUMBER to (
        messages[5]?.decodeC40()
            ?: throw IllegalArgumentException("Missing sheet number")
        )
)
