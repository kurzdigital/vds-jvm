package com.kurzdigital.vds.vdsnc

import java.io.Serializable
import java.security.cert.Certificate

data class VdsNc(
    val type: VdsNcType,
    val header: VdsNcHeader,
    val messages: List<Pair<Any, Any>>,
    val sha256: ByteArray,
    val signature: ByteArray,
    val certificate: Certificate
) : Serializable

data class VdsNcHeader(
    val issuingCountry: String,
    val type: String,
    val version: Int
) : Serializable
