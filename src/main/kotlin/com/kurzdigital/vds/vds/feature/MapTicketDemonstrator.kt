package com.kurzdigital.vds.vds.feature

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.vds.getDate
import com.kurzdigital.vds.vds.getString
import com.kurzdigital.vds.vds.getTimestampFromUInt32

fun mapTicketDemonstrator(features: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.EVENT_TITLE to features.getString(1),
    Label.TIMESTAMP to features[2]?.getTimestampFromUInt32(),
    Label.TICKET_NUMBER to features.getString(3),
    Label.LOCATION_NAME to features.getString(4),
    Label.SEAT_SECTION to features.getString(5),
    Label.SEAT_ROW to features.getString(6),
    Label.SEAT_NUMBER to features.getString(7),
    Label.NAME to if (features[8] != null) features.getString(8) else null,
    Label.DATE_OF_BIRTH to features[9]?.getDate(),
    Label.BIOMETRICS to features[0xa],
)
