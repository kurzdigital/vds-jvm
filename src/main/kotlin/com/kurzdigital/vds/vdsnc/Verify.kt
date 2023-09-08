package com.kurzdigital.vds.vdsnc

import com.kurzdigital.vds.security.validAt
import com.kurzdigital.vds.security.verify
import com.kurzdigital.vds.security.verifyDocumentCertificate
import java.security.cert.TrustAnchor
import java.util.Date

enum class VerificationResult {
    SIGNATURE_INVALID,
    SIGNATURE_VALID,
    SIGNATURE_VALID_BUT_CERTIFICATE_UNKNOWN,
}

fun VdsNc.verify(
    trustAnchors: Set<TrustAnchor>,
    date: Date = Date(),
) = if (
    !verify(
        certificate.publicKey,
        sha256,
        signature,
    ) || !certificate.validAt(date)
) {
    VerificationResult.SIGNATURE_INVALID
} else {
    when (trustAnchors.verifyDocumentCertificate(certificate)) {
        true -> VerificationResult.SIGNATURE_VALID
        false -> VerificationResult.SIGNATURE_VALID_BUT_CERTIFICATE_UNKNOWN
    }
}
