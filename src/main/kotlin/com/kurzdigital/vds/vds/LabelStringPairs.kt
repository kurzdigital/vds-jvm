package com.kurzdigital.vds.vds

import com.kurzdigital.mrz.MrzInfo
import com.kurzdigital.vds.Label
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Map<Any, Any?>.labelStringPairs() = mutableListOf<Pair<Any, Any>>().also {
    for (entry in this) {
        when (entry.value) {
            null -> Unit
            is MrzInfo -> it.addAll((entry.value as MrzInfo).toMap().toList())
            is Date -> it.add(Pair(entry.key, (entry.value as Date).format()))
            is ByteArray -> it.add(
                Pair(
                    entry.key,
                    (entry.value as ByteArray).toHexString(),
                ),
            )
            else -> it.add(Pair(entry.key, entry.value.toString()))
        }
    }
}

private fun MrzInfo.toMap() = mapOf(
    Label.SURNAME to primaryIdentifier,
    Label.GIVEN_NAME to secondaryIdentifier,
    Label.DATE_OF_BIRTH to dateOfBirth.reformatMrzDate(),
    Label.DATE_OF_EXPIRY to dateOfExpiry.reformatMrzDate(),
    Label.DOCUMENT_NUMBER to documentNumber,
    Label.ISSUING_STATE to issuingState,
    Label.NATIONALITY to nationality,
)

private fun String.reformatMrzDate() = try {
    SimpleDateFormat("yyMMdd", Locale.US).parse(this)?.let {
        java.text.DateFormat.getDateInstance(
            java.text.DateFormat.SHORT,
        ).format(it)
    } ?: this
} catch (e: IllegalArgumentException) {
    this
} catch (e: ParseException) {
    this
}

private fun Date.format() = java.text.DateFormat.getDateTimeInstance(
    java.text.DateFormat.LONG,
    java.text.DateFormat.LONG,
).format(this)

private fun ByteArray.toHexString() = joinToString("") { "%02X".format(it) }
