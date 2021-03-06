package com.kurzdigital.vds.vds

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.security.CertificateListIterator
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.security.cert.Certificate
import java.security.cert.CertificateFactory

class VdsTests {
    private val certificates = ArrayList<Certificate>()

    @BeforeEach
    fun setUp() {
        if (certificates.size > 0) {
            return
        }
        val cf = CertificateFactory.getInstance("X.509")
        listOf(
            "KDS_DEV_VDS_01.crt",
            "KDS_ETD.crt",
            "KDS_TAXSTAMPS.crt",
            "KDS_TEST_VACC.crt",
            "sealgen_UTTS5B.crt"
        ).forEach {
            certificates.add(
                cf.generateCertificate(
                    ByteArrayInputStream(
                        ClassLoader.getSystemResource(it).readBytes()
                    )
                )
            )
        }
    }

    @Test
    fun arrivalAttestation() {
        val vds = readAndParse("vds_sample_arrival_attestation")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals(
            "Wed Jan 01 00:00:00 CET 2020",
            vds.header.documentIssueDate.toString()
        )
        assertEquals(
            "Thu Apr 01 00:00:00 CEST 2021",
            vds.header.signatureCreationDate.toString()
        )
        assertEquals(
            "MED<<6525845096<<<<<<<<<<<<<<<7008038M2201018USA<<<<<<<<<<<6MANNSENS<<MANNY<<<<<<<<<<<<<<<",
            vds.messages[Label.MRZ].toString()
        )
        assertEquals("ABC123456DEF", vds.messages[Label.ARZ])
        assertEquals(true, vds.verify())
    }

    @Test
    fun emergencyTravel() {
        val vds = readAndParse("vds_sample_emergency_travel")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals(
            "I<GBR6525845096<<<<<<<<<<<<<<<7008038M2201018USA<<<<<<<<<<<6SUPAMANN<<MARY<<<<<<<<<<<<<<<<",
            vds.messages[Label.MRZ].toString()
        )
        assertEquals(true, vds.verify())
    }

    @Test
    fun residencePermit() {
        val vds = readAndParse("vds_sample_residence_permit")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals(
            "ATD<<6525845096<<<<<<<<<<<<<<<7008038M2201018USA<<<<<<<<<<<6RESIDORCE<<ROLAND<<<<<<<<<<<<<",
            vds.messages[Label.MRZ].toString()
        )
        assertEquals("UFO001979", vds.messages[Label.PASSPORT_NUMBER])
        assertEquals(true, vds.verify())
    }

    @Test
    fun socialInsurance() {
        val vds = readAndParse("vds_sample_social_insurance")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals("65170839J003", vds.messages[Label.SIN])
        assertEquals("Perschwei??", vds.messages[Label.SURNAME])
        assertEquals("Oscar", vds.messages[Label.GIVEN_NAME])
        assertEquals("J??cob??nidicturius", vds.messages[Label.BIRTH_NAME])
        assertEquals(true, vds.verify())
    }

    @Test
    fun supplementSheet() {
        val vds = readAndParse("vds_sample_supplement_sheet")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals(
            "ATD<<6525845096<<<<<<<<<<<<<<<7008038M2201018USA<<<<<<<<<<<6RESIDORCE<<ROLAND<<<<<<<<<<<<<",
            vds.messages[Label.MRZ].toString()
        )
        assertEquals("PA0000005", vds.messages[Label.SHEET_NUMBER])
        // This signature is intentionally invalid.
        assertEquals(false, vds.verify())
    }

    @Test
    fun germanIdChangeSticker() {
        val vds = readAndParse("vds_sample_german_id_card_change_sticker")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals("T2000AK47", vds.messages[Label.DOCUMENT_NUMBER])
        assertEquals("05314000", vds.messages[Label.MUNICIPALITY_NUMBER])
        assertEquals(
            "53175HEINEMANNSTR11",
            vds.messages[Label.RESIDENTIAL_ADDRESS]
        )
        assertEquals(true, vds.verify())
    }

    @Test
    fun germanPassportChangeSticker() {
        val vds = readAndParse("vds_sample_german_passport_change_sticker")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals("PA5500K11", vds.messages[Label.DOCUMENT_NUMBER])
        assertEquals("03359010", vds.messages[Label.MUNICIPALITY_NUMBER])
        assertEquals("21614", vds.messages[Label.POSTAL_CODE])
        assertEquals(true, vds.verify())
    }

    @Test
    fun visaIcao() {
        val vds = readAndParse("vds_sample_visa_icao")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals(
            "VCD<<1234567XY7<<<<<<<<<<<<<<<5203116M2005250GBR<<<<<<<<<<<4DENT<<ARTHUR<PHILIP<<<<<<<<<<<",
            vds.messages[Label.MRZ].toString()
        )
        assertEquals(2, (vds.messages[Label.NUMBER_OF_ENTRIES] as Byte).toInt())
        assertEquals(5898240, vds.messages[Label.DURATION_OF_STAY])
        assertEquals("ABC424242", vds.messages[Label.PASSPORT_NUMBER])
        // Because we don't have the ICAO certificate/public key we
        // cannot verify the ICAO signature, unfortunately.
    }

    @Test
    fun covidVaccinationCert() {
        val vds = readAndParse("vds_sample_covid_vaccination_cert")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals("Mustermann", vds.messages[Label.SURNAME])
        assertEquals("Max", vds.messages[Label.GIVEN_NAME])
        assertEquals(
            "Fri Mar 28 00:00:00 CET 1997",
            vds.messages[Label.DATE_OF_BIRTH].toString()
        )
        assertEquals("Germany", vds.messages[Label.RESIDENCE])
        assertEquals("PA", vds.messages[Label.TYPE_OF_ID])
        assertEquals("LG98GT5JR", vds.messages[Label.ID_NUMBER])
        assertEquals("max.mustermann@kurzdigital.com", vds.messages[Label.EMAIL])
        assertEquals("Covid-19 Vaccine Moderna", vds.messages[Label.VACCINE])
        assertEquals("A2021041456", vds.messages[Label.BATCH_NUMBER])
        assertEquals("SARS-CoV-2", vds.messages[Label.VACCINATED_DISEASE])
        assertEquals(
            "Dr. John Doe; Schwabacher Str. 106, 90763 F??rth, Germany",
            vds.messages[Label.DOCTOR]
        )
        assertEquals(
            "Thu Jan 20 11:38:00 CET 2022",
            vds.messages[Label.TIMESTAMP].toString()
        )
        assertEquals(true, vds.verify())
    }

    @Test
    fun generalPurpose() {
        val vds = readAndParse("vds_sample_general_purpose")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals("Value One", vds.messages["Header One"])
        assertEquals("Value 2??????", vds.messages["Header 2??????"])
        assertEquals("www.kurzdigital.com", vds.messages["BlaBliBlu Header"])
        assertEquals("", vds.messages[""])
        assertEquals(true, vds.verify())
    }

    @Test
    fun pharmaPack() {
        val vds = readAndParse("vds_sample_pharmapack")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals("FALSISTOP forte", vds.messages[Label.PRODUCT_NAME])
        assertEquals("22.08.2021", vds.messages[Label.PRODUCTION_DATE])
        assertEquals("F??rth, Germany", vds.messages[Label.PRODUCTION_PLANT])
        assertEquals("5558632476", vds.messages[Label.BATCH_NUMBER])
        assertEquals("67500050044278964002", vds.messages[Label.SERIAL_NUMBER])
        assertEquals("01/2024", vds.messages[Label.BEST_BEFORE])
        assertEquals("20", vds.messages[Label.QUANTITY])
        assertEquals(
            "LEONHARD KURZ Stiftung & Co. KG",
            vds.messages[Label.CONTACT_DETAILS_NAME]
        )
        assertEquals(
            "Schwabacher Stra??e 482",
            vds.messages[Label.CONTACT_DETAILS_STREET]
        )
        assertEquals(
            "90763 F??rth/Germany",
            vds.messages[Label.CONTACT_DETAILS_ADDRESS]
        )
        assertEquals(
            "Mobil: +49 152 567 19 045",
            vds.messages[Label.CONTACT_DETAILS_PHONE]
        )
        assertEquals(
            "info@trustconcept.com",
            vds.messages[Label.CONTACT_DETAILS_EMAIL]
        )
        assertEquals(
            "https://www.trustconcept.com",
            vds.messages[Label.CONTACT_DETAILS_WEBSITE]
        )
        assertEquals(true, vds.verify())
    }

    @Test
    fun supervisedAntigenTest() {
        val vds = readAndParse("vds_sample_supervised_covid_test")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals("Mustermann", vds.messages[Label.SURNAME])
        assertEquals("Max", vds.messages[Label.GIVEN_NAME])
        assertEquals(
            "Fri Mar 28 00:00:00 CET 1997",
            vds.messages[Label.DATE_OF_BIRTH].toString()
        )
        assertEquals("Germany", vds.messages[Label.RESIDENCE])
        assertEquals("PA", vds.messages[Label.TYPE_OF_ID])
        assertEquals("LG98GT5JR", vds.messages[Label.ID_NUMBER])
        assertEquals("max.mustermann@kurzdigital.com", vds.messages[Label.EMAIL])
        assertEquals("Hotgen 2019-nCoV Antigen", vds.messages[Label.TEST_NAME])
        assertEquals("W20210306000", vds.messages[Label.TEST_ID])
        assertEquals("SARS-CoV-2", vds.messages[Label.TESTED_DISEASE])
        assertEquals("John Doe", vds.messages[Label.SUPERVISOR])
        assertEquals(
            "KURZ Digital Solutions GmbH & Co. KG; Schwabacher Str. 106, 90763 F??rth, Germany",
            vds.messages[Label.COMPANY]
        )
        assertEquals(
            "Thu Jan 20 11:40:00 CET 2022",
            vds.messages[Label.TIMESTAMP].toString()
        )
        assertEquals(1, (vds.messages[Label.VALIDITY] as Byte).toInt())
        assertEquals(true, vds.verify())
    }

    @Test
    fun taxStamp() {
        val vds = readAndParse("vds_sample_taxstamp")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals("WAAK", vds.messages[Label.ISSUER_ID])
        assertEquals("A348948923U", vds.messages[Label.UI_SERIAL_NUMBER])
        assertEquals("Germany", vds.messages[Label.MANUFACTURING_PLACE])
        assertEquals(
            "Schwabacher Str. 482, 90763",
            vds.messages[Label.MANUFACTURING_FACILITY]
        )
        assertEquals("LM523", vds.messages[Label.MACHINE])
        assertEquals("Marlboro Cigarettes", vds.messages[Label.PRODUCT_DESCRIPTION])
        assertEquals("Malaysia", vds.messages[Label.MARKET_OF_SALE])
        assertEquals("by air", vds.messages[Label.SHIPMENT_ROUTE])
        assertEquals("Multifoil Sdn Bhd 5 Jalan 4/4C", vds.messages[Label.IMPORTER])
        assertEquals("t1p.de/0w8z", vds.messages[Label.LINK])
        assertEquals(true, vds.verify())
    }

    private fun readAndParse(
        name: String
    ) = ClassLoader.getSystemResource(
        name
    ).readBytes().decodeVds()

    private fun Vds.verify() = verify(
        CertificateListIterator(certificates)
    )
}
