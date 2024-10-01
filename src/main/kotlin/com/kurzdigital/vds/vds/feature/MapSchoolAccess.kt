package com.kurzdigital.vds.vds.feature

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.vds.getString

fun mapSchoolAccess(features: Map<Byte, ByteArray>) = mapOf<Any, Any?>(
    Label.STUDENT_ID to features.getString(1),
    Label.SCHOOL_NAME to features.getString(2),
    Label.SURNAME to features.getString(3),
    Label.GIVEN_NAME to features.getString(4),
    Label.CLASS to features.getString(5),
    Label.BIOMETRICS to features[6],
)
