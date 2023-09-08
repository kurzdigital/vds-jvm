package com.kurzdigital.vds.vds.message

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.security.decodeC40
import com.kurzdigital.vds.vds.getString

fun mapTaxStamp(messages: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.ISSUER_ID to (
        messages[1]?.decodeC40()
            ?: throw IllegalArgumentException("Missing issuer ID")
        ),
    Label.UI_SERIAL_NUMBER to (
        messages[2]?.decodeC40()
            ?: throw IllegalArgumentException("Missing serial number")
        ),
    Label.MANUFACTURING_PLACE to messages.getString(3),
    Label.MANUFACTURING_FACILITY to messages.getString(4),
    Label.MACHINE to messages.getString(5),
    Label.PRODUCT_DESCRIPTION to messages.getString(6),
    Label.MARKET_OF_SALE to messages.getString(7),
    Label.SHIPMENT_ROUTE to messages.getString(8),
    Label.IMPORTER to messages.getString(9),
    Label.LINK to messages.getString(0xa),
)
