package com.kurzdigital.vds.vds.feature

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.vds.getString

fun mapPharmapack(features: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.PRODUCT_NAME to features.getString(1),
    Label.PRODUCTION_DATE to features.getString(2),
    Label.PRODUCTION_PLANT to features.getString(3),
    Label.BATCH_NUMBER to features.getString(4),
    Label.SERIAL_NUMBER to features.getString(5),
    Label.BEST_BEFORE to features.getString(6),
    Label.QUANTITY to features.getString(7),
    Label.CONTACT_DETAILS_NAME to features.getString(8),
    Label.CONTACT_DETAILS_STREET to features.getString(9),
    Label.CONTACT_DETAILS_ADDRESS to features.getString(0xa),
    Label.CONTACT_DETAILS_PHONE to features.getString(0xb),
    Label.CONTACT_DETAILS_EMAIL to features.getString(0xc),
    Label.CONTACT_DETAILS_WEBSITE to features.getString(0xd),
)
