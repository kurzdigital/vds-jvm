package com.kurzdigital.vds.vds

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.security.CertificateListIterator
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.jupiter.api.Assertions.assertArrayEquals
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
            "KDS_EasyCard_VDS.crt",
            "KDS_ETD.crt",
            "KDS_TAXSTAMPS.crt",
            "KDS_TEST_VACC.crt",
            "KDS_VEHICLE_VIGNETTE.crt",
            "SingleJourney_ETD.crt",
            "sealgen_UTTS5B.crt",
        ).forEach {
            certificates.add(
                cf.generateCertificate(
                    ByteArrayInputStream(
                        ClassLoader.getSystemResource(it).readBytes(),
                    ),
                ),
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
            vds.header.documentIssueDate.toString(),
        )
        assertEquals(
            "Thu Apr 01 00:00:00 CEST 2021",
            vds.header.signatureCreationDate.toString(),
        )
        assertEquals(
            "MED<<6525845096<<<<<<<<<<<<<<<7008038M2201018USA<<<<<<<<<<<6MANNSENS<<MANNY<<<<<<<<<<<<<<<",
            vds.messages[Label.MRZ].toString(),
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
            vds.messages[Label.MRZ].toString(),
        )
        assertEquals(true, vds.verify())
    }

    @Test
    fun emergencyTravelSingleJourney() {
        val vds = readAndParse("vds_sample_emergency_travel_single_journey")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals(
            "652584509",
            vds.messages[Label.DOCUMENT_NUMBER].toString(),
        )
        assertEquals(
            "Smith",
            vds.messages[Label.SURNAME].toString(),
        )
        assertEquals(
            "John",
            vds.messages[Label.GIVEN_NAME].toString(),
        )
        assertEquals(
            "USA",
            vds.messages[Label.NATIONALITY].toString(),
        )
        assertEquals(
            "Tue Jul 18 00:00:00 CEST 1995",
            vds.messages[Label.DATE_OF_BIRTH].toString(),
        )
        assertEquals(
            "M",
            vds.messages[Label.SEX].toString(),
        )
        assertEquals(
            "Fürth",
            vds.messages[Label.PLACE_OF_BIRTH].toString(),
        )
        assertEquals(
            "Utopia",
            vds.messages[Label.DESTINATION].toString(),
        )
        assertEquals(
            "Thu Jul 18 00:00:00 CEST 2024",
            vds.messages[Label.EXPIRATION_DATE].toString(),
        )
        assertArrayEquals(
            byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
            vds.messages[Label.BIOMETRICS] as ByteArray,
        )
        assertEquals(true, vds.verify())
    }

    @Test
    fun emergencyTravelWithBio() {
        val vds = readAndParse("vds_sample_emergency_travel_with_bio")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals(
            "I<UTO6525845096<<<<<<<<<<<<<<<9507188M2402099USA<<<<<<<<<<<0SMITH<<JOHN<<<<<<<<<<<<<<<<<<<",
            vds.messages[Label.MRZ].toString(),
        )
        assertEquals(
            "W5d5Y22FWGGiU6V7NVVykFZZfmqUZ3qDRyhhYHhSXWpLbWeNc3Kgc1BpcbWhhXlrkTaikZl1h01rn0yLf2hodLWVSWikY3l6Wz4rlNGmQ3VhepKJYHNghpJ5aK55h3aKXG9Sc4hqdqNOjHtvfX5PV40yjJ2DnJOEkF9Hb4xvh4A=",
            java.util.Base64.getEncoder().encodeToString(
                vds.messages[Label.BIOMETRICS] as ByteArray,
            ),
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
            vds.messages[Label.MRZ].toString(),
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
        assertEquals("Perschweiß", vds.messages[Label.SURNAME])
        assertEquals("Oscar", vds.messages[Label.GIVEN_NAME])
        assertEquals("Jâcobénidicturius", vds.messages[Label.BIRTH_NAME])
        assertEquals(true, vds.verify())
    }

    @Test
    fun supplementSheet() {
        val vds = readAndParse("vds_sample_supplement_sheet")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals(
            "ATD<<6525845096<<<<<<<<<<<<<<<7008038M2201018USA<<<<<<<<<<<6RESIDORCE<<ROLAND<<<<<<<<<<<<<",
            vds.messages[Label.MRZ].toString(),
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
            vds.messages[Label.RESIDENTIAL_ADDRESS],
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
            vds.messages[Label.MRZ].toString(),
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
            vds.messages[Label.DATE_OF_BIRTH].toString(),
        )
        assertEquals("Germany", vds.messages[Label.RESIDENCE])
        assertEquals("PA", vds.messages[Label.TYPE_OF_ID])
        assertEquals("LG98GT5JR", vds.messages[Label.ID_NUMBER])
        assertEquals("max.mustermann@kurzdigital.com", vds.messages[Label.EMAIL])
        assertEquals("Covid-19 Vaccine Moderna", vds.messages[Label.VACCINE])
        assertEquals("A2021041456", vds.messages[Label.BATCH_NUMBER])
        assertEquals("SARS-CoV-2", vds.messages[Label.VACCINATED_DISEASE])
        assertEquals(
            "Dr. John Doe; Schwabacher Str. 106, 90763 Fürth, Germany",
            vds.messages[Label.DOCTOR],
        )
        assertEquals(
            "Thu Jan 20 11:38:00 CET 2022",
            vds.messages[Label.TIMESTAMP].toString(),
        )
        assertEquals(true, vds.verify())
    }

    @Test
    fun generalPurpose() {
        val vds = readAndParse("vds_sample_general_purpose")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals("Value One", vds.messages["Header One"])
        assertEquals("Value 2️⃣", vds.messages["Header 2️⃣"])
        assertEquals("www.kurzdigital.com", vds.messages["BlaBliBlu Header"])
        assertEquals("", vds.messages[""])
        assertEquals(true, vds.verify())
    }

    @Test
    fun vehicleVignette() {
        val vds = readAndParse("vds_sample_vehicle_vignette")
        assertNotNull(vds)
        assertEquals(
            "Fri Mar 01 00:00:00 CET 2024",
            vds.messages[Label.EXPIRATION_DATE].toString(),
        )
        assertEquals("OVD 123 KD", vds.messages[Label.CAR_LICENSE_PLATE])
        assertEquals("Ford Ranger", vds.messages[Label.CAR_MODEL])
        assertEquals("White", vds.messages[Label.CAR_COLOR])
        assertEquals("Taban Sibonelo", vds.messages[Label.OWNER])
        assertEquals(true, vds.verify())
    }

    @Test
    fun pharmaPack() {
        val vds = readAndParse("vds_sample_pharmapack")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals("FALSISTOP forte", vds.messages[Label.PRODUCT_NAME])
        assertEquals("22.08.2021", vds.messages[Label.PRODUCTION_DATE])
        assertEquals("Fürth, Germany", vds.messages[Label.PRODUCTION_PLANT])
        assertEquals("5558632476", vds.messages[Label.BATCH_NUMBER])
        assertEquals("67500050044278964002", vds.messages[Label.SERIAL_NUMBER])
        assertEquals("01/2024", vds.messages[Label.BEST_BEFORE])
        assertEquals("20", vds.messages[Label.QUANTITY])
        assertEquals(
            "LEONHARD KURZ Stiftung & Co. KG",
            vds.messages[Label.CONTACT_DETAILS_NAME],
        )
        assertEquals(
            "Schwabacher Straße 482",
            vds.messages[Label.CONTACT_DETAILS_STREET],
        )
        assertEquals(
            "90763 Fürth/Germany",
            vds.messages[Label.CONTACT_DETAILS_ADDRESS],
        )
        assertEquals(
            "Mobil: +49 152 567 19 045",
            vds.messages[Label.CONTACT_DETAILS_PHONE],
        )
        assertEquals(
            "info@trustconcept.com",
            vds.messages[Label.CONTACT_DETAILS_EMAIL],
        )
        assertEquals(
            "https://www.trustconcept.com",
            vds.messages[Label.CONTACT_DETAILS_WEBSITE],
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
            vds.messages[Label.DATE_OF_BIRTH].toString(),
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
            "KURZ Digital Solutions GmbH & Co. KG; Schwabacher Str. 106, 90763 Fürth, Germany",
            vds.messages[Label.COMPANY],
        )
        assertEquals(
            "Thu Jan 20 11:40:00 CET 2022",
            vds.messages[Label.TIMESTAMP].toString(),
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
            vds.messages[Label.MANUFACTURING_FACILITY],
        )
        assertEquals("LM523", vds.messages[Label.MACHINE])
        assertEquals("Marlboro Cigarettes", vds.messages[Label.PRODUCT_DESCRIPTION])
        assertEquals("Malaysia", vds.messages[Label.MARKET_OF_SALE])
        assertEquals("by air", vds.messages[Label.SHIPMENT_ROUTE])
        assertEquals("Multifoil Sdn Bhd 5 Jalan 4/4C", vds.messages[Label.IMPORTER])
        assertEquals("t1p.de/0w8z", vds.messages[Label.LINK])
        assertEquals(true, vds.verify())
    }

    @Test
    fun demecanPatientID() {
        val vds = readAndParse("vds_sample_demecan_patient_id")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals(
            "UTO-012345",
            vds.messages[Label.DOCUMENT_NUMBER].toString(),
        )
        assertEquals(
            "Sample Pia Angela",
            vds.messages[Label.NAME].toString(),
        )
        assertEquals(
            "Mon Jun 12 00:00:00 CEST 1995",
            vds.messages[Label.DATE_OF_BIRTH].toString(),
        )
        assertEquals(
            "Schwabacher Str. 106",
            vds.messages[Label.ADDRESS_STREET].toString(),
        )
        assertEquals(
            "90763 Fürth",
            vds.messages[Label.ADDRESS_CITY].toString(),
        )
        // The sample is signed with a key that would require Bouncy Castle
        // so we skip verification here.
    }

    private fun readAndParse(
        name: String,
    ) = ClassLoader.getSystemResource(
        name,
    ).readBytes().decodeVds()

    private fun Vds.verify() = verify(
        CertificateListIterator(certificates),
    )
}
