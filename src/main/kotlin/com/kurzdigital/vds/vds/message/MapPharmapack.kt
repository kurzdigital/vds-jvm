package com.kurzdigital.vds.vds.message

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.vds.getString

fun mapPharmapack(messages: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.PRODUCT_NAME to messages.getString(1),
    Label.PRODUCTION_DATE to messages.getString(2),
    Label.PRODUCTION_PLANT to messages.getString(3),
    Label.BATCH_NUMBER to messages.getString(4),
    Label.SERIAL_NUMBER to messages.getString(5),
    Label.BEST_BEFORE to messages.getString(6),
    Label.QUANTITY to messages.getString(7),
    Label.CONTACT_DETAILS_NAME to messages.getString(8),
    Label.CONTACT_DETAILS_STREET to messages.getString(9),
    Label.CONTACT_DETAILS_ADDRESS to messages.getString(0xa),
    Label.CONTACT_DETAILS_PHONE to messages.getString(0xb),
    Label.CONTACT_DETAILS_EMAIL to messages.getString(0xc),
    Label.CONTACT_DETAILS_WEBSITE to messages.getString(0xd)
)
