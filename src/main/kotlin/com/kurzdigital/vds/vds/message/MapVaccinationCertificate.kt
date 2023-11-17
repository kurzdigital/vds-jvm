package com.kurzdigital.vds.vds.message

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.security.decodeC40
import com.kurzdigital.vds.vds.getDate
import com.kurzdigital.vds.vds.getString
import com.kurzdigital.vds.vds.getTimestampFromUInt32

fun mapVaccinationCertificate(messages: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.SURNAME to messages.getString(1),
    Label.GIVEN_NAME to messages.getString(2),
    Label.DATE_OF_BIRTH to (
        messages[3]?.getDate()
            ?: throw IllegalArgumentException("Missing date of birth")
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
    Label.VACCINE to messages.getString(8),
    Label.BATCH_NUMBER to (
        messages[9]?.decodeC40()
            ?: throw IllegalArgumentException("Missing batch number")
        ),
    Label.VACCINATED_DISEASE to messages.getString(0xa),
    Label.DOCTOR to messages.getString(0xb),
    Label.TIMESTAMP to messages[0xc]?.getTimestampFromUInt32(),
)
