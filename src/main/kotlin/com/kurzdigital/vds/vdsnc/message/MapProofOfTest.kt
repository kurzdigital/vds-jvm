package com.kurzdigital.vds.vdsnc.message

import com.kurzdigital.vds.Label
import org.json.JSONObject

fun mapProofOfTest(msg: JSONObject): List<Pair<Any, Any>> {
    val dates = msg.getJSONObject("dat")
    val pid = msg.getJSONObject("pid")
    val testResult = msg.getJSONObject("tr")
    return listOf(
        Pair(Label.NAME, pid.getString("n")),
        Pair(Label.DATE_OF_BIRTH, pid.getString("dob")),
        Pair(Label.DOCUMENT_TYPE, pid.getString("dt")),
        Pair(Label.DOCUMENT_NUMBER, pid.getString("dn")),
        Pair(Label.DATE_OF_RESULT, dates.getString("ri")),
        Pair(Label.DATE_OF_SAMPLING, dates.getString("sc")),
        Pair(Label.TEST_METHOD, testResult.getString("m")),
        Pair(Label.TEST_RESULT, testResult.getString("r")),
        Pair(Label.TEST_CATEGORY, testResult.getString("tc")),
    )
}
