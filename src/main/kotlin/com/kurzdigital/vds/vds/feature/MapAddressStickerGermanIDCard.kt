package com.kurzdigital.vds.vds.feature

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.security.decodeC40

fun mapAddressStickerGermanIDCard(features: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.DOCUMENT_NUMBER to (
        features[1]?.decodeC40()
            ?: throw IllegalArgumentException("Missing document number")
        ),
    Label.MUNICIPALITY_NUMBER to (
        features[2]?.decodeC40()
            ?: throw IllegalArgumentException("Missing municipality number")
        ),
    Label.RESIDENTIAL_ADDRESS to (
        features[3]?.decodeC40()
            ?: throw IllegalArgumentException("Missing residential address")
        ),
)
