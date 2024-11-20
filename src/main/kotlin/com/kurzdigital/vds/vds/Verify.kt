package com.kurzdigital.vds.vds

import com.kurzdigital.vds.security.CertificateIterator
import com.kurzdigital.vds.security.validAt
import java.security.cert.Certificate
import java.util.Date

data class VerificationResult(
    val signatureValid: Boolean,
    val certificateNotExpired: Boolean,
) {
    fun isValid() = signatureValid && certificateNotExpired
}

fun Vds.verify(
    certificateIterator: CertificateIterator,
    date: Date = Date(),
): VerificationResult {
    while (true) {
        val certificate = certificateIterator.next() ?: break
        if (
            com.kurzdigital.vds.security.verify(
                certificate.publicKey,
                sha256,
                signature,
            )
        ) {
            return VerificationResult(
                true,
                certificate.validAt(date),
            )
        }
    }
    return VerificationResult(false, false)
}

fun Vds.verify(
    certificate: Certificate,
    date: Date = Date(),
) = VerificationResult(
    com.kurzdigital.vds.security.verify(
        certificate.publicKey,
        sha256,
        signature,
    ),
    certificate.validAt(date),
)
