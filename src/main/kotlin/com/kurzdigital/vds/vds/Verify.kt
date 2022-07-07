package com.kurzdigital.vds.vds

import com.kurzdigital.vds.security.CertificateIterator

fun Vds.verify(
    certificateIterator: CertificateIterator
) = com.kurzdigital.vds.security.verify(
    certificateIterator,
    sha256,
    signature
)
