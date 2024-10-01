package com.kurzdigital.vds.vds.feature

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.vds.getString
import com.kurzdigital.vds.vds.getUInt16LittleEndian
import com.kurzdigital.vds.vds.getUInt32LittleEndian

fun mapMaliWsl(features: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.UNIQUE_CODE to features.getString(1),
    Label.SERIAL_NUMBER to features.getString(2),
    Label.TYPE to features.getString(3),
    Label.CHARGE to (
        features[4]?.getUInt32LittleEndian()
            ?: throw IllegalArgumentException("Missing charge")
        ),
    Label.YEAR to (
        features[5]?.getUInt16LittleEndian()
            ?: throw IllegalArgumentException("Missing year")
        ),
)
