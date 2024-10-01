package com.kurzdigital.vds.vds.feature

import com.kurzdigital.vds.vds.getString

fun mapGeneralPurpose(features: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    features.getString(1) to features.getString(2),
    features.getString(3) to features.getString(4),
    features.getString(5) to features.getString(6),
    features.getString(7) to features.getString(8),
)
