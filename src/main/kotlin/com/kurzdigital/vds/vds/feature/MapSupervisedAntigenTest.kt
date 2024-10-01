package com.kurzdigital.vds.vds.feature

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.security.decodeC40
import com.kurzdigital.vds.vds.getDate
import com.kurzdigital.vds.vds.getString
import com.kurzdigital.vds.vds.getTimestampFromUInt32

fun mapSupervisedAntigenTest(features: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.SURNAME to features.getString(1),
    Label.GIVEN_NAME to features.getString(2),
    Label.DATE_OF_BIRTH to (
        features[3]?.getDate()
            ?: throw IllegalArgumentException("Missing date of birth")
        ),
    Label.RESIDENCE to features.getString(4),
    Label.TYPE_OF_ID to (
        features[5]?.decodeC40()
            ?: throw IllegalArgumentException("Missing type of ID")
        ),
    Label.ID_NUMBER to (
        features[6]?.decodeC40()
            ?: throw IllegalArgumentException("Missing ID number")
        ),
    Label.EMAIL to features.getString(7),
    Label.TEST_NAME to features.getString(8),
    Label.TEST_ID to features.getString(9),
    Label.TESTED_DISEASE to features.getString(0xa),
    Label.SUPERVISOR to features.getString(0xb),
    Label.COMPANY to features.getString(0xc),
    Label.TIMESTAMP to features[0xd]?.getTimestampFromUInt32(),
    Label.VALIDITY to (features[0xe] ?: throw IllegalArgumentException("Missing validity"))[0],
)
