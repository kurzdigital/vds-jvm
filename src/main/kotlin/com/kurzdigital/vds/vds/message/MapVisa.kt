package com.kurzdigital.vds.vds.message

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.security.decodeC40
import com.kurzdigital.vds.security.decodeC40MrvA
import com.kurzdigital.vds.security.decodeC40MrvB
import com.kurzdigital.vds.vds.getUInt24LittleEndian

fun mapVisa(messages: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.MRZ to (
        (
            messages[1]?.decodeC40MrvA()
                ?: messages[2]?.decodeC40MrvB()
            )
            ?: throw IllegalArgumentException("Missing MRZ")
        ),
    Label.NUMBER_OF_ENTRIES to messages[3]?.get(0),
    Label.DURATION_OF_STAY to (
        messages[4]?.getUInt24LittleEndian()
            ?: throw IllegalArgumentException("Missing duration of stay")
        ),
    Label.PASSPORT_NUMBER to (
        messages[5]?.decodeC40()
            ?: throw IllegalArgumentException("Missing passport number")
        ),
    Label.VISA_TYPE to messages[6],
    Label.ADDITIONAL_FEATURE to messages[7],
)
