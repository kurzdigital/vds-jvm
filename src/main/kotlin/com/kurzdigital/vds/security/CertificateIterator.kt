package com.kurzdigital.vds.security

import java.security.cert.Certificate

// Required to use a List (in the Unit tests) or a Cursor (in the app).
interface CertificateIterator {
    fun next(): Certificate?
}
