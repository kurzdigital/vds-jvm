package com.kurzdigital.vds.vds.message

import com.kurzdigital.vds.vds.getString

fun mapGeneralPurpose(messages: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    messages.getString(1) to messages.getString(2),
    messages.getString(3) to messages.getString(4),
    messages.getString(5) to messages.getString(6),
    messages.getString(7) to messages.getString(8),
)
