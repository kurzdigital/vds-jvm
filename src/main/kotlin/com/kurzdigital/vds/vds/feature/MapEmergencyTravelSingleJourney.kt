package com.kurzdigital.vds.vds.feature

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.vds.getDate
import com.kurzdigital.vds.vds.getString

fun mapEmergencyTravelSingleJourney(features: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.DOCUMENT_NUMBER to features.getString(1),
    Label.SURNAME to features.getString(2),
    Label.GIVEN_NAME to features.getString(3),
    Label.NATIONALITY to features.getString(4),
    Label.DATE_OF_BIRTH to (
        features[5]?.getDate()
            ?: throw IllegalArgumentException("Missing date of birth")
        ),
    Label.SEX to features.getString(6),
    Label.PLACE_OF_BIRTH to features.getString(7),
    Label.DESTINATION to features.getString(8),
    Label.EXPIRATION_DATE to (
        features[9]?.getDate()
            ?: throw IllegalArgumentException("Missing expiration date")
        ),
    Label.BIOMETRICS to features[0xa],
)
