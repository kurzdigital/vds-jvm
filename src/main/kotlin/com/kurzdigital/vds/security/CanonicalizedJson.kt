package com.kurzdigital.vds.security

import org.erdtman.jcs.JsonCanonicalizer

fun String.canonicalizedJson() = JsonCanonicalizer(this).encodedString
