package com.kurzdigital.vds.vds.message

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.vds.getString
import com.kurzdigital.vds.vds.getUInt16LittleEndian
import com.kurzdigital.vds.vds.getUInt32LittleEndian

fun mapMaliWsl(messages: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.UNIQUE_CODE to messages.getString(1),
    Label.SERIAL_NUMBER to messages.getString(2),
    Label.TYPE to messages.getString(3),
    Label.CHARGE to (
        messages[4]?.getUInt32LittleEndian()
            ?: throw IllegalArgumentException("Missing charge")
        ),
    Label.YEAR to (
        messages[5]?.getUInt16LittleEndian()
            ?: throw IllegalArgumentException("Missing year")
        ),
)
