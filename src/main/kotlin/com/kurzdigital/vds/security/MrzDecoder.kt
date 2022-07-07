package com.kurzdigital.vds.security

import com.kurzdigital.mrz.MrzInfo
import com.kurzdigital.mrz.MrzMrvAParser
import com.kurzdigital.mrz.MrzMrvBParser
import com.kurzdigital.mrz.MrzTd2Parser
import com.kurzdigital.mrz.MrzTd3Parser
import kotlin.math.max

fun ByteArray.decodeC40MrvA(): MrzInfo? = MrzMrvAParser.parse(
    prepareMrz(88)
)

fun ByteArray.decodeC40MrvB(): MrzInfo? = MrzMrvBParser.parse(
    prepareMrz(72)
)

fun ByteArray.decodeC40Td2(): MrzInfo? = MrzTd2Parser.parse(
    prepareMrz(72)
)

fun ByteArray.decodeC40Td3(): MrzInfo? = MrzTd3Parser.parse(
    prepareMrz(88)
)

private fun ByteArray.prepareMrz(min: Int): String =
    decodeC40().replace(' ', '<').expandMrz(min)

private fun String.expandMrz(min: Int): String =
    this + "<".repeat(max(0, min - length))
