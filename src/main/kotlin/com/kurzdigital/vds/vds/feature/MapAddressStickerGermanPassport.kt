package com.kurzdigital.vds.vds.feature

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.security.decodeC40

fun mapAddressStickerGermanPassport(features: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.DOCUMENT_NUMBER to (
        features[1]?.decodeC40()
            ?: throw IllegalArgumentException("Missing document number")
        ),
    Label.MUNICIPALITY_NUMBER to (
        features[2]?.decodeC40()
            ?: throw IllegalArgumentException("Missing municipality number")
        ),
    Label.POSTAL_CODE to (
        features[3]?.decodeC40()
            ?: throw IllegalArgumentException("Missing postal code")
        ),
)
