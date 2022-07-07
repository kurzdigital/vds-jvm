package com.kurzdigital.vds.security

import java.security.cert.Certificate

class CertificateListIterator(
    private val list: List<Certificate>
) : CertificateIterator {
    private var i = 0

    override fun next() = if (i < list.size) list[i++] else null
}
