package com.kurzdigital.vds.vdsnc.feature

import com.kurzdigital.vds.Label
import org.json.JSONObject

fun mapProofOfVaccination(msg: JSONObject): List<Pair<Any, Any>> {
    val pid = msg.getJSONObject("pid")
    val list = mutableListOf<Pair<Any, Any>>(
        Pair(Label.UVCI, msg.getString("uvci")),
        Pair(Label.NAME, pid.getString("n")),
        Pair(Label.DOCUMENT_NUMBER, pid.getString("i")),
    )
    val events = msg.getJSONArray("ve")
    val numberOfEvents = events.length()
    for (i in 0 until numberOfEvents) {
        val event = events.getJSONObject(i)
        list.addAll(
            listOf(
                Pair(Label.VACCINE_BRAND, event.getString("nam")),
                Pair(Label.VACCINE_OR_PROPHYLAXIS, event.getString("des")),
                Pair(Label.TARGETED_DISEASE, event.getString("dis")),
            ),
        )
        val doses = event.getJSONArray("vd")
        val numberOfDoses = doses.length()
        for (j in 0 until numberOfDoses) {
            val dose = doses.getJSONObject(j)
            list.addAll(
                listOf(
                    Pair(Label.DOSE_NUMBER, dose.getInt("seq").toString()),
                    Pair(Label.DATE_OF_VACCINATION, dose.getString("dvc")),
                    Pair(Label.COUNTRY_OF_VACCINATION, dose.getString("ctr")),
                    Pair(Label.VACCINE_BATCH_NUMBER, dose.getString("lot")),
                    Pair(Label.ADMINISTERING_CENTER, dose.getString("adm")),
                ),
            )
        }
    }
    return list
}
