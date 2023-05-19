package com.kurzdigital.vds.vds

import com.kurzdigital.vds.security.CertificateIterator
import com.kurzdigital.vds.security.validAt
import java.security.cert.Certificate
import java.util.Date

fun Vds.verify(
    certificateIterator: CertificateIterator,
    date: Date = Date()
) = com.kurzdigital.vds.security.verify(
    certificateIterator,
    sha256,
    signature,
    date
)

fun Vds.verify(
    certificate: Certificate,
    date: Date = Date()
) = certificate.validAt(date) && com.kurzdigital.vds.security.verify(
    certificate.publicKey,
    sha256,
    signature
)
