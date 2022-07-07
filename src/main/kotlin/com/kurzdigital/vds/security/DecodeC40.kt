package com.kurzdigital.vds.security

import java.nio.ByteBuffer

fun ByteArray.decodeC40(): String = ByteBuffer.wrap(this).decodeC40()

fun ByteBuffer.decodeC40(): String {
    val sb = StringBuilder()
    while (true) {
        val r = remaining()
        when {
            r < 1 -> break
            // This case is not required by BSI-TR-03137 because
            // C40 data is always padded to be a multiple of two
            // (see "Padding", point 2).
            r == 1 -> sb.appendChar(get())
            else -> sb.decodeAndAppend(get(), get())
        }
    }
    return sb.toString()
}

// "C40 Encoding of Strings (Normative)" is described in BSI-TR-03137 Part 1,
// Annex B. At the time of writing this document was, and hopefully still is,
// available here:
// https://www.bsi.bund.de/SharedDocs/Downloads/EN/BSI/Publications/TechGuidelines/TR03137/BSI-TR-03137_Part1.pdf
private fun StringBuilder.decodeAndAppend(high: Byte, low: Byte) {
    val highInt = high.toInt() and 0xff
    if (highInt == 0xfe) {
        // 0xfe in the high byte means "back to ASCII".
        appendChar(low)
        return
    }
    val lowInt = low.toInt() and 0xff
    val value = highInt shl 8 or lowInt - 1
    if (value > -1) {
        // Reverse C40 encoding: c1 * 1600 + c2 * 40 + c3 + 1
        appendIfValid(value / 1600)
        appendIfValid(value % 1600 / 40)
        appendIfValid(value % 1600 % 40)
    }
}

private fun StringBuilder.appendChar(byte: Byte) = append((byte - 1).toChar())

private fun StringBuilder.appendIfValid(value: Int) {
    c40ToChar(value)?.let {
        append(it)
    }
}

private fun c40ToChar(value: Int) = when (value) {
    3 -> ' '
    in 4..13 -> (value + 44).toChar() // 0 to 9
    in 14..39 -> (value + 51).toChar() // A to Z
    else -> null
}
