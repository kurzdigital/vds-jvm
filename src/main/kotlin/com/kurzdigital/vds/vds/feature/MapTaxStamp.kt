package com.kurzdigital.vds.vds.feature

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.security.decodeC40
import com.kurzdigital.vds.vds.getString

fun mapTaxStamp(features: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.ISSUER_ID to (
        features[1]?.decodeC40()
            ?: throw IllegalArgumentException("Missing issuer ID")
        ),
    Label.UI_SERIAL_NUMBER to (
        features[2]?.decodeC40()
            ?: throw IllegalArgumentException("Missing serial number")
        ),
    Label.MANUFACTURING_PLACE to features.getString(3),
    Label.MANUFACTURING_FACILITY to features.getString(4),
    Label.MACHINE to features.getString(5),
    Label.PRODUCT_DESCRIPTION to features.getString(6),
    Label.MARKET_OF_SALE to features.getString(7),
    Label.SHIPMENT_ROUTE to features.getString(8),
    Label.IMPORTER to features.getString(9),
    Label.LINK to features.getString(0xa),
)
