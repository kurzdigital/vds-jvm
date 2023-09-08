package com.kurzdigital.vds.vds.message

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.security.decodeC40

fun mapAddressStickerGermanIDCard(messages: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.DOCUMENT_NUMBER to (
        messages[1]?.decodeC40()
            ?: throw IllegalArgumentException("Missing document number")
        ),
    Label.MUNICIPALITY_NUMBER to (
        messages[2]?.decodeC40()
            ?: throw IllegalArgumentException("Missing municipality number")
        ),
    Label.RESIDENTIAL_ADDRESS to (
        messages[3]?.decodeC40()
            ?: throw IllegalArgumentException("Missing residential address")
        ),
)
