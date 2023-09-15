package com.kurzdigital.vds.vds.message

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.vds.getDateFromUInt24
import com.kurzdigital.vds.vds.getString
import com.kurzdigital.vds.vds.getUInt24

fun mapEmergencyTravelSingleJourney(messages: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.DOCUMENT_NUMBER to messages.getString(1),
    Label.SURNAME to messages.getString(2),
    Label.GIVEN_NAME to messages.getString(3),
    Label.NATIONALITY to messages.getString(4),
    Label.DATE_OF_BIRTH to (
        getDateFromUInt24(
            messages[5]?.getUInt24()
                ?: throw IllegalArgumentException("Missing date of birth"),
        ) ?: throw IllegalArgumentException("Invalid date of birth")
        ),
    Label.SEX to messages.getString(6),
    Label.PLACE_OF_BIRTH to messages.getString(7),
    Label.DESTINATION to messages.getString(8),
    Label.EXPIRATION_DATE to (
        getDateFromUInt24(
            messages[9]?.getUInt24()
                ?: throw IllegalArgumentException("Missing expiration date"),
        ) ?: throw IllegalArgumentException("Invalid expiration date")
        ),
    Label.BIOMETRICS to messages[0xa],
)
