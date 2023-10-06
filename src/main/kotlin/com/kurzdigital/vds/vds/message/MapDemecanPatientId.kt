package com.kurzdigital.vds.vds.message

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.vds.getDateFromUInt24
import com.kurzdigital.vds.vds.getString
import com.kurzdigital.vds.vds.getUInt24

fun mapDemecanPatientId(messages: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.DOCUMENT_NUMBER to messages.getString(1),
    Label.NAME to messages.getString(2),
    Label.DATE_OF_BIRTH to (
        getDateFromUInt24(
            messages[3]?.getUInt24()
                ?: throw IllegalArgumentException("Missing date of birth"),
        ) ?: throw IllegalArgumentException("Invalid date of birth")
        ),
    Label.ADDRESS_STREET to messages.getString(4),
    Label.ADDRESS_CITY to messages.getString(5),
)
