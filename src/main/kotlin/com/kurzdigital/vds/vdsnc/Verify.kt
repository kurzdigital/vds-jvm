package com.kurzdigital.vds.vdsnc

import com.kurzdigital.vds.security.validAt
import com.kurzdigital.vds.security.verify
import com.kurzdigital.vds.security.verifyDocumentCertificate
import java.security.cert.TrustAnchor
import java.util.Date

data class VerificationResult(
    val signatureValid: Boolean,
    val certificateNotExpired: Boolean,
    val certificateKnown: Boolean,
) {
    fun isValid() = signatureValid && certificateNotExpired && certificateKnown
}

fun VdsNc.verify(
    trustAnchors: Set<TrustAnchor>,
    date: Date = Date(),
) = VerificationResult(
    verify(
        certificate.publicKey,
        sha256,
        signature,
    ),
    certificate.validAt(date),
    trustAnchors.verifyDocumentCertificate(certificate),
)
