package com.kurzdigital.vds.vds.message

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.security.decodeC40
import com.kurzdigital.vds.vds.getDateFromUInt24
import com.kurzdigital.vds.vds.getString
import com.kurzdigital.vds.vds.getTimestampFromUInt32
import com.kurzdigital.vds.vds.getUInt24

fun mapSupervisedAntigenTest(messages: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.SURNAME to messages.getString(1),
    Label.GIVEN_NAME to messages.getString(2),
    Label.DATE_OF_BIRTH to (
        getDateFromUInt24(
            messages[3]?.getUInt24()
                ?: throw IllegalArgumentException("Missing date of birth")
        ) ?: throw IllegalArgumentException("Invalid date of birth")
        ),
    Label.RESIDENCE to messages.getString(4),
    Label.TYPE_OF_ID to (
        messages[5]?.decodeC40()
            ?: throw IllegalArgumentException("Missing type of ID")
        ),
    Label.ID_NUMBER to (
        messages[6]?.decodeC40()
            ?: throw IllegalArgumentException("Missing ID number")
        ),
    Label.EMAIL to messages.getString(7),
    Label.TEST_NAME to messages.getString(8),
    Label.TEST_ID to messages.getString(9),
    Label.TESTED_DISEASE to messages.getString(0xa),
    Label.SUPERVISOR to messages.getString(0xb),
    Label.COMPANY to messages.getString(0xc),
    Label.TIMESTAMP to messages[0xd]?.getTimestampFromUInt32(),
    Label.VALIDITY to (
        (
            messages[0xe]
                ?: throw IllegalArgumentException("Missing validity")
            )[0]
        )
)
