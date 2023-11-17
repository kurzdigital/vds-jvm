package com.kurzdigital.vds.vds.message

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.vds.getDate
import com.kurzdigital.vds.vds.getString

fun mapEmergencyTravelSingleJourney(messages: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.DOCUMENT_NUMBER to messages.getString(1),
    Label.SURNAME to messages.getString(2),
    Label.GIVEN_NAME to messages.getString(3),
    Label.NATIONALITY to messages.getString(4),
    Label.DATE_OF_BIRTH to (
        messages[5]?.getDate()
            ?: throw IllegalArgumentException("Missing date of birth")
        ),
    Label.SEX to messages.getString(6),
    Label.PLACE_OF_BIRTH to messages.getString(7),
    Label.DESTINATION to messages.getString(8),
    Label.EXPIRATION_DATE to (
        messages[9]?.getDate()
            ?: throw IllegalArgumentException("Missing expiration date")
        ),
    Label.BIOMETRICS to messages[0xa],
)
