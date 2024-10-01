package com.kurzdigital.vds.vdsnc

import com.kurzdigital.vds.security.canonicalizedJson
import com.kurzdigital.vds.security.concatenatedRSToASN1DER
import com.kurzdigital.vds.security.provider
import com.kurzdigital.vds.security.sha256
import com.kurzdigital.vds.vdsnc.feature.mapProofOfTest
import com.kurzdigital.vds.vdsnc.feature.mapProofOfVaccination
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.security.cert.CertificateFactory

fun String.decodeVdsNcOrNull(): VdsNc? = try {
    decodeVdsNc()
} catch (e: Exception) {
    null
}

fun String.decodeVdsNc(): VdsNc {
    val root = JSONObject(this)
    val data = root.getJSONObject("data")
    val signature = root.getJSONObject("sig")
    val header = data.getJSONObject("hdr")
    val vdsNcHeader = VdsNcHeader(
        header.getString("is"),
        header.getString("t"),
        header.getInt("v"),
    )
    val msg = data.getJSONObject("msg")
    val features: List<Pair<Any, Any>>
    val type = if (vdsNcHeader.type.contains("test")) {
        features = mapProofOfTest(msg)
        VdsNcType.PROOF_OF_TEST
    } else {
        features = mapProofOfVaccination(msg)
        VdsNcType.PROOF_OF_VACCINATION
    }
    return VdsNc(
        type,
        vdsNcHeader,
        features,
        data.toString().canonicalizedJson().toByteArray().sha256(),
        signature.getString(
            "sigvl",
        ).decodeUrlSafeBase64().concatenatedRSToASN1DER(),
        CertificateFactory.getInstance(
            "X.509",
            provider,
        ).generateCertificate(
            ByteArrayInputStream(
                signature.getString(
                    "cer",
                ).decodeUrlSafeBase64(),
            ),
        ),
    )
}

private fun String.decodeUrlSafeBase64(): ByteArray {
    var converted = replace("-", "+").replace("_", "/")
    val rest = converted.length % 4
    if (rest > 0) {
        converted += "=".repeat(4 - rest)
    }
    return java.util.Base64.getDecoder().decode(converted)
}
