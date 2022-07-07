package com.kurzdigital.vds.vds

import java.util.Date

data class Vds(
    val type: VdsType,
    val header: VdsHeader,
    val messages: Map<Any, Any?>,
    val sha256: ByteArray,
    val signature: ByteArray
)

data class VdsHeader(
    val version: Byte,
    val countryId: String,
    val signerAndCertificateReference: ByteArray,
    val documentIssueDate: Date?,
    val signatureCreationDate: Date?,
    val docFeatureDefRef: Byte,
    val docTypeCategory: Byte,
    val id: Int = (docFeatureDefRef.toUByte().toInt() shl 8) or
        docTypeCategory.toUByte().toInt()
)
