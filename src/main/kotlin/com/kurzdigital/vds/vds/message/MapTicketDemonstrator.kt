package com.kurzdigital.vds.vds.message

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.vds.getDate
import com.kurzdigital.vds.vds.getString
import com.kurzdigital.vds.vds.getTimestampFromUInt32

fun mapTicketDemonstrator(messages: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.EVENT_TITLE to messages.getString(1),
    Label.TIMESTAMP to messages[2]?.getTimestampFromUInt32(),
    Label.TICKET_NUMBER to messages.getString(3),
    Label.LOCATION_NAME to messages.getString(4),
    Label.SEAT_SECTION to messages.getString(5),
    Label.SEAT_ROW to messages.getString(6),
    Label.SEAT_NUMBER to messages.getString(7),
    Label.NAME to if (messages[8] != null) messages.getString(8) else null,
    Label.DATE_OF_BIRTH to messages[9]?.getDate(),
    Label.BIOMETRICS to messages[0xa],
)
