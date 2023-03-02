package com.kurzdigital.vds.vds

import com.kurzdigital.vds.security.CertificateIterator
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
