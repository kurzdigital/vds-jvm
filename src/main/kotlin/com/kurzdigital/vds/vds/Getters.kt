package com.kurzdigital.vds.vds

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Map<Byte, ByteArray>.getString(key: Byte) = String(
    get(key) ?: throw IllegalArgumentException("Missing tag $key"),
)

fun ByteArray.getTimestampFromUInt32(): Date {
    val secondsSince1970 = (get(0).toUByte().toLong() shl 24) or
        (get(1).toUByte().toLong() shl 16) or
        (get(2).toUByte().toLong() shl 8) or
        get(3).toUByte().toLong()
    return Calendar.getInstance().apply {
        timeInMillis = secondsSince1970 * 1000L
    }.time
}

fun getDateFromUInt24(value: Int): Date? = SimpleDateFormat(
    "MMddyyyy",
    Locale.US,
).parse(String.format("%08d", value))

fun ByteArray.getUInt24(): Int {
    if (size != 3) {
        throw IllegalArgumentException("Insufficient length for UInt24")
    }
    return decodeUInt24(get(0), get(1), get(2))
}

fun decodeUInt24(high: Byte, middle: Byte, low: Byte): Int =
    (high.toUByte().toInt() shl 16) or
        (middle.toUByte().toInt() shl 8) or
        low.toUByte().toInt()
