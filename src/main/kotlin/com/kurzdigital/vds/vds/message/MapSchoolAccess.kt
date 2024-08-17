package com.kurzdigital.vds.vds.message

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.vds.getString

fun mapSchoolAccess(messages: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.STUDENT_ID to messages.getString(1),
    Label.SCHOOL_NAME to messages.getString(2),
    Label.SURNAME to messages.getString(3),
    Label.GIVEN_NAME to messages.getString(4),
    Label.CLASS to messages.getString(5),
    Label.BIOMETRICS to messages[6],
)
