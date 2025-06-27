package com.kurzdigital.vds.vdsnc

import com.kurzdigital.vds.security.readCscaMasterList
import com.kurzdigital.vds.security.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
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
                "DE_ML_2025-06-25-09-31-57.ml",
            ).readBytes(),
        ).readCscaMasterList()
    }

    @Test
    fun proofOfTest() {
        val vdsNc = readAndParse("vdsnc_sample_proof_of_test")
        assertNotNull(vdsNc)
        assertEquals("UTO", vdsNc.header.issuingCountry)
        assertEquals("icao.test", vdsNc.header.type)
        assertEquals("Cook Gerald", vdsNc.features[0].second)
        assertEquals("1990-01-29", vdsNc.features[1].second)
        assertEquals("P", vdsNc.features[2].second)
        assertEquals("E1234567P", vdsNc.features[3].second)
        assertEquals("2021-02-11T14:00:00+08:00", vdsNc.features[4].second)
        assertEquals("2020-12-12T12:00:00+08:00", vdsNc.features[5].second)
        assertEquals("nasopharyngeal", vdsNc.features[6].second)
        assertEquals("negative", vdsNc.features[7].second)
        assertEquals("molecular(PCR)", vdsNc.features[8].second)
        val result = vdsNc.verify(trustAnchors!!)
        // Check only signature because the certificate is unknown
        // since this is just a sample.
        assertTrue(result.signatureValid)
        assertTrue(result.certificateNotExpired)
        assertFalse(result.certificateKnown)
    }

    @Test
    fun proofOfVaccination() {
        val vdsNc = readAndParse("vdsnc_sample_proof_of_vaccination")
        assertNotNull(vdsNc)
        assertEquals("UTO", vdsNc.header.issuingCountry)
        assertEquals("icao.vacc", vdsNc.header.type)
        assertEquals("U32870", vdsNc.features[0].second)
        assertEquals("Smith Bill", vdsNc.features[1].second)
        assertEquals("A1234567Z", vdsNc.features[2].second)
        assertEquals("Comirnaty", vdsNc.features[3].second)
        assertEquals("XM68M6", vdsNc.features[4].second)
        assertEquals("RA01.0", vdsNc.features[5].second)
        assertEquals("1", vdsNc.features[6].second)
        assertEquals("2021-03-03", vdsNc.features[7].second)
        assertEquals("UTO", vdsNc.features[8].second)
        assertEquals("VC35679", vdsNc.features[9].second)
        assertEquals("RIVM", vdsNc.features[10].second)
        assertEquals("2", vdsNc.features[11].second)
        assertEquals("2021-03-24", vdsNc.features[12].second)
        assertEquals("UTO", vdsNc.features[13].second)
        assertEquals("VC87540", vdsNc.features[14].second)
        assertEquals("RIVM", vdsNc.features[15].second)
        val result = vdsNc.verify(trustAnchors!!)
        // Check only signature because the certificate is unknown
        // since this is just a sample.
        assertTrue(result.signatureValid)
        assertTrue(result.certificateNotExpired)
        assertFalse(result.certificateKnown)
    }

    @Test
    fun proofOfVaccinationAus() {
        val vdsNc = readAndParse("vdsnc_sample_proof_of_vaccination_aus")
        assertNotNull(vdsNc)
        assertEquals("AUS", vdsNc.header.issuingCountry)
        assertEquals("icao.vacc", vdsNc.header.type)
        assertEquals("VB0009990012", vdsNc.features[0].second)
        assertEquals("CITIZEN  JANE SUE", vdsNc.features[1].second)
        assertEquals("PA0941262", vdsNc.features[2].second)
        assertEquals("AstraZeneca Vaxzevria", vdsNc.features[3].second)
        assertEquals("XM68M6", vdsNc.features[4].second)
        assertEquals("RA01.0", vdsNc.features[5].second)
        assertEquals("1", vdsNc.features[6].second)
        assertEquals("2021-09-15", vdsNc.features[7].second)
        assertEquals("AUS", vdsNc.features[8].second)
        assertEquals("300157P", vdsNc.features[9].second)
        assertEquals("General Practitioner", vdsNc.features[10].second)
        assertTrue(vdsNc.verify(trustAnchors!!).isValid())
    }

    private fun readAndParse(
        name: String,
    ) = ClassLoader.getSystemResource(
        name,
    ).readText().decodeVdsNc()
}
