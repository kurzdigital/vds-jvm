package com.kurzdigital.vds.vdsnc

import com.kurzdigital.vds.security.readCscaMasterList
import com.kurzdigital.vds.security.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.security.cert.TrustAnchor

class VdsNcTests {
    private var trustAnchors: Set<TrustAnchor>? = null

    @BeforeEach
    fun setUp() {
        if (trustAnchors != null) {
            return
        }
        trustAnchors = ByteArrayInputStream(
            ClassLoader.getSystemResource(
                "20230831_DEMasterList.ml",
            ).readBytes(),
        ).readCscaMasterList()
    }

    @Test
    fun proofOfTest() {
        val vdsNc = readAndParse("vdsnc_sample_proof_of_test")
        assertNotNull(vdsNc)
        assertEquals("UTO", vdsNc.header.issuingCountry)
        assertEquals("icao.test", vdsNc.header.type)
        assertEquals("Cook Gerald", vdsNc.messages[0].second)
        assertEquals("1990-01-29", vdsNc.messages[1].second)
        assertEquals("P", vdsNc.messages[2].second)
        assertEquals("E1234567P", vdsNc.messages[3].second)
        assertEquals("2021-02-11T14:00:00+08:00", vdsNc.messages[4].second)
        assertEquals("2020-12-12T12:00:00+08:00", vdsNc.messages[5].second)
        assertEquals("nasopharyngeal", vdsNc.messages[6].second)
        assertEquals("negative", vdsNc.messages[7].second)
        assertEquals("molecular(PCR)", vdsNc.messages[8].second)
        assertEquals(
            // The certificate is unknown because this is just a sample.
            VerificationResult.SIGNATURE_VALID_BUT_CERTIFICATE_UNKNOWN,
            vdsNc.verify(trustAnchors!!),
        )
    }

    @Test
    fun proofOfVaccination() {
        val vdsNc = readAndParse("vdsnc_sample_proof_of_vaccination")
        assertNotNull(vdsNc)
        assertEquals("UTO", vdsNc.header.issuingCountry)
        assertEquals("icao.vacc", vdsNc.header.type)
        assertEquals("U32870", vdsNc.messages[0].second)
        assertEquals("Smith Bill", vdsNc.messages[1].second)
        assertEquals("A1234567Z", vdsNc.messages[2].second)
        assertEquals("Comirnaty", vdsNc.messages[3].second)
        assertEquals("XM68M6", vdsNc.messages[4].second)
        assertEquals("RA01.0", vdsNc.messages[5].second)
        assertEquals("1", vdsNc.messages[6].second)
        assertEquals("2021-03-03", vdsNc.messages[7].second)
        assertEquals("UTO", vdsNc.messages[8].second)
        assertEquals("VC35679", vdsNc.messages[9].second)
        assertEquals("RIVM", vdsNc.messages[10].second)
        assertEquals("2", vdsNc.messages[11].second)
        assertEquals("2021-03-24", vdsNc.messages[12].second)
        assertEquals("UTO", vdsNc.messages[13].second)
        assertEquals("VC87540", vdsNc.messages[14].second)
        assertEquals("RIVM", vdsNc.messages[15].second)
        assertEquals(
            // The certificate is unknown because this is just a sample.
            VerificationResult.SIGNATURE_VALID_BUT_CERTIFICATE_UNKNOWN,
            vdsNc.verify(trustAnchors!!),
        )
    }

    @Test
    fun proofOfVaccinationAus() {
        val vdsNc = readAndParse("vdsnc_sample_proof_of_vaccination_aus")
        assertNotNull(vdsNc)
        assertEquals("AUS", vdsNc.header.issuingCountry)
        assertEquals("icao.vacc", vdsNc.header.type)
        assertEquals("VB0009990012", vdsNc.messages[0].second)
        assertEquals("CITIZEN  JANE SUE", vdsNc.messages[1].second)
        assertEquals("PA0941262", vdsNc.messages[2].second)
        assertEquals("AstraZeneca Vaxzevria", vdsNc.messages[3].second)
        assertEquals("XM68M6", vdsNc.messages[4].second)
        assertEquals("RA01.0", vdsNc.messages[5].second)
        assertEquals("1", vdsNc.messages[6].second)
        assertEquals("2021-09-15", vdsNc.messages[7].second)
        assertEquals("AUS", vdsNc.messages[8].second)
        assertEquals("300157P", vdsNc.messages[9].second)
        assertEquals("General Practitioner", vdsNc.messages[10].second)
        assertEquals(
            VerificationResult.SIGNATURE_VALID,
            vdsNc.verify(trustAnchors!!),
        )
    }

    private fun readAndParse(
        name: String,
    ) = ClassLoader.getSystemResource(
        name,
    ).readText().decodeVdsNc()
}
