package com.kurzdigital.vds.vdsnc

import com.kurzdigital.vds.security.verify
import com.kurzdigital.vds.security.verifyDocumentCertificate
import java.security.cert.TrustAnchor

enum class VerificationResult {
    SIGNATURE_INVALID,
    SIGNATURE_VALID,
    SIGNATURE_VALID_BUT_CERTIFICATE_UNKNOWN
}

fun VdsNc.verify(trustAnchors: Set<TrustAnchor>) = if (
    !verify(
        certificate.publicKey,
        sha256,
        signature
    )
) {
    VerificationResult.SIGNATURE_INVALID
} else {
    when (trustAnchors.verifyDocumentCertificate(certificate)) {
        true -> VerificationResult.SIGNATURE_VALID
        false -> VerificationResult.SIGNATURE_VALID_BUT_CERTIFICATE_UNKNOWN
    }
}
