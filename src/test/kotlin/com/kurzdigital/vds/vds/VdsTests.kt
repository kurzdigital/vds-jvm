package com.kurzdigital.vds.vds

import com.kurzdigital.vds.Label
import com.kurzdigital.vds.security.CertificateListIterator
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
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
            "sealgen_UTTS5B.crt",
            "SchoolAccess.crt",
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
            vds.features[Label.MRZ].toString(),
        )
        assertEquals("ABC123456DEF", vds.features[Label.ARZ])
        assertTrue(vds.verify())
    }

    @Test
    fun emergencyTravel() {
        val vds = readAndParse("vds_sample_emergency_travel")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals(
            "I<GBR6525845096<<<<<<<<<<<<<<<7008038M2201018USA<<<<<<<<<<<6SUPAMANN<<MARY<<<<<<<<<<<<<<<<",
            vds.features[Label.MRZ].toString(),
        )
        assertTrue(vds.verify())
    }

    @Test
    fun emergencyTravelSingleJourney() {
        val vds = readAndParse("vds_sample_emergency_travel_single_journey")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals(
            "652584509",
            vds.features[Label.DOCUMENT_NUMBER].toString(),
        )
        assertEquals(
            "Smith",
            vds.features[Label.SURNAME].toString(),
        )
        assertEquals(
            "John",
            vds.features[Label.GIVEN_NAME].toString(),
        )
        assertEquals(
            "USA",
            vds.features[Label.NATIONALITY].toString(),
        )
        assertEquals(
            "Tue Jul 18 00:00:00 CEST 1995",
            vds.features[Label.DATE_OF_BIRTH].toString(),
        )
        assertEquals(
            "M",
            vds.features[Label.SEX].toString(),
        )
        assertEquals(
            "Fürth",
            vds.features[Label.PLACE_OF_BIRTH].toString(),
        )
        assertEquals(
            "Utopia",
            vds.features[Label.DESTINATION].toString(),
        )
        assertEquals(
            "Thu Jul 18 00:00:00 CEST 2024",
            vds.features[Label.EXPIRATION_DATE].toString(),
        )
        assertArrayEquals(
            byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
            vds.features[Label.BIOMETRICS] as ByteArray,
        )
        // The certificate for this sample has expired, unfortunately.
        // So this sample can no longer be verified.
    }

    @Test
    fun emergencyTravelWithBio() {
        val vds = readAndParse("vds_sample_emergency_travel_with_bio")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals(
            "I<UTO6525845096<<<<<<<<<<<<<<<9507188M2402099USA<<<<<<<<<<<0SMITH<<JOHN<<<<<<<<<<<<<<<<<<<",
            vds.features[Label.MRZ].toString(),
        )
        assertEquals(
            "W5d5Y22FWGGiU6V7NVVykFZZfmqUZ3qDRyhhYHhSXWpLbWeNc3Kgc1BpcbWhhXlrkTaikZl1h01rn0yLf2hodLWVSWikY3l6Wz4rlNGmQ3VhepKJYHNghpJ5aK55h3aKXG9Sc4hqdqNOjHtvfX5PV40yjJ2DnJOEkF9Hb4xvh4A=",
            java.util.Base64.getEncoder().encodeToString(
                vds.features[Label.BIOMETRICS] as ByteArray,
            ),
        )
        assertTrue(vds.verify())
    }

    @Test
    fun residencePermit() {
        val vds = readAndParse("vds_sample_residence_permit")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals(
            "ATD<<6525845096<<<<<<<<<<<<<<<7008038M2201018USA<<<<<<<<<<<6RESIDORCE<<ROLAND<<<<<<<<<<<<<",
            vds.features[Label.MRZ].toString(),
        )
        assertEquals("UFO001979", vds.features[Label.PASSPORT_NUMBER])
        assertTrue(vds.verify())
    }

    @Test
    fun socialInsurance() {
        val vds = readAndParse("vds_sample_social_insurance")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals("65170839J003", vds.features[Label.SIN])
        assertEquals("Perschweiß", vds.features[Label.SURNAME])
        assertEquals("Oscar", vds.features[Label.GIVEN_NAME])
        assertEquals("Jâcobénidicturius", vds.features[Label.BIRTH_NAME])
        assertTrue(vds.verify())
    }

    @Test
    fun supplementSheet() {
        val vds = readAndParse("vds_sample_supplement_sheet")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals(
            "ATD<<6525845096<<<<<<<<<<<<<<<7008038M2201018USA<<<<<<<<<<<6RESIDORCE<<ROLAND<<<<<<<<<<<<<",
            vds.features[Label.MRZ].toString(),
        )
        assertEquals("PA0000005", vds.features[Label.SHEET_NUMBER])
        // This signature is intentionally invalid.
        assertEquals(false, vds.verify())
    }

    @Test
    fun germanIdChangeSticker() {
        val vds = readAndParse("vds_sample_german_id_card_change_sticker")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals("T2000AK47", vds.features[Label.DOCUMENT_NUMBER])
        assertEquals("05314000", vds.features[Label.MUNICIPALITY_NUMBER])
        assertEquals(
            "53175HEINEMANNSTR11",
            vds.features[Label.RESIDENTIAL_ADDRESS],
        )
        assertTrue(vds.verify())
    }

    @Test
    fun germanPassportChangeSticker() {
        val vds = readAndParse("vds_sample_german_passport_change_sticker")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals("PA5500K11", vds.features[Label.DOCUMENT_NUMBER])
        assertEquals("03359010", vds.features[Label.MUNICIPALITY_NUMBER])
        assertEquals("21614", vds.features[Label.POSTAL_CODE])
        assertTrue(vds.verify())
    }

    @Test
    fun visaIcao() {
        val vds = readAndParse("vds_sample_visa_icao")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals(
            "VCD<<1234567XY7<<<<<<<<<<<<<<<5203116M2005250GBR<<<<<<<<<<<4DENT<<ARTHUR<PHILIP<<<<<<<<<<<",
            vds.features[Label.MRZ].toString(),
        )
        assertEquals(2, (vds.features[Label.NUMBER_OF_ENTRIES] as Byte).toInt())
        assertEquals(90, vds.features[Label.DURATION_OF_STAY])
        assertEquals("ABC424242", vds.features[Label.PASSPORT_NUMBER])
        // Because we don't have the ICAO certificate/public key we
        // cannot verify the ICAO signature, unfortunately.
    }

    @Test
    fun covidVaccinationCert() {
        val vds = readAndParse("vds_sample_covid_vaccination_cert")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals("Mustermann", vds.features[Label.SURNAME])
        assertEquals("Max", vds.features[Label.GIVEN_NAME])
        assertEquals(
            "Fri Mar 28 00:00:00 CET 1997",
            vds.features[Label.DATE_OF_BIRTH].toString(),
        )
        assertEquals("Germany", vds.features[Label.RESIDENCE])
        assertEquals("PA", vds.features[Label.TYPE_OF_ID])
        assertEquals("LG98GT5JR", vds.features[Label.ID_NUMBER])
        assertEquals("max.mustermann@kurzdigital.com", vds.features[Label.EMAIL])
        assertEquals("Covid-19 Vaccine Moderna", vds.features[Label.VACCINE])
        assertEquals("A2021041456", vds.features[Label.BATCH_NUMBER])
        assertEquals("SARS-CoV-2", vds.features[Label.VACCINATED_DISEASE])
        assertEquals(
            "Dr. John Doe; Schwabacher Str. 106, 90763 Fürth, Germany",
            vds.features[Label.DOCTOR],
        )
        assertEquals(
            "Thu Jan 20 11:38:00 CET 2022",
            vds.features[Label.TIMESTAMP].toString(),
        )
        assertTrue(vds.verify())
    }

    @Test
    fun generalPurpose() {
        val vds = readAndParse("vds_sample_general_purpose")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals("Value One", vds.features["Header One"])
        assertEquals("Value 2️⃣", vds.features["Header 2️⃣"])
        assertEquals("www.kurzdigital.com", vds.features["BlaBliBlu Header"])
        assertEquals("", vds.features[""])
        assertTrue(vds.verify())
    }

    @Test
    fun vehicleVignette() {
        val vds = readAndParse("vds_sample_vehicle_vignette")
        assertNotNull(vds)
        assertEquals(
            "Fri Mar 01 00:00:00 CET 2024",
            vds.features[Label.EXPIRATION_DATE].toString(),
        )
        assertEquals("OVD 123 KD", vds.features[Label.CAR_LICENSE_PLATE])
        assertEquals("Ford Ranger", vds.features[Label.CAR_MODEL])
        assertEquals("White", vds.features[Label.CAR_COLOR])
        assertEquals("Taban Sibonelo", vds.features[Label.OWNER])
        assertTrue(vds.verify())
    }

    @Test
    fun pharmaPack() {
        val vds = readAndParse("vds_sample_pharmapack")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals("FALSISTOP forte", vds.features[Label.PRODUCT_NAME])
        assertEquals("22.08.2021", vds.features[Label.PRODUCTION_DATE])
        assertEquals("Fürth, Germany", vds.features[Label.PRODUCTION_PLANT])
        assertEquals("5558632476", vds.features[Label.BATCH_NUMBER])
        assertEquals("67500050044278964002", vds.features[Label.SERIAL_NUMBER])
        assertEquals("01/2024", vds.features[Label.BEST_BEFORE])
        assertEquals("20", vds.features[Label.QUANTITY])
        assertEquals(
            "LEONHARD KURZ Stiftung & Co. KG",
            vds.features[Label.CONTACT_DETAILS_NAME],
        )
        assertEquals(
            "Schwabacher Straße 482",
            vds.features[Label.CONTACT_DETAILS_STREET],
        )
        assertEquals(
            "90763 Fürth/Germany",
            vds.features[Label.CONTACT_DETAILS_ADDRESS],
        )
        assertEquals(
            "Mobil: +49 152 567 19 045",
            vds.features[Label.CONTACT_DETAILS_PHONE],
        )
        assertEquals(
            "info@trustconcept.com",
            vds.features[Label.CONTACT_DETAILS_EMAIL],
        )
        assertEquals(
            "https://www.trustconcept.com",
            vds.features[Label.CONTACT_DETAILS_WEBSITE],
        )
        assertTrue(vds.verify())
    }

    @Test
    fun supervisedAntigenTest() {
        val vds = readAndParse("vds_sample_supervised_covid_test")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals("Mustermann", vds.features[Label.SURNAME])
        assertEquals("Max", vds.features[Label.GIVEN_NAME])
        assertEquals(
            "Fri Mar 28 00:00:00 CET 1997",
            vds.features[Label.DATE_OF_BIRTH].toString(),
        )
        assertEquals("Germany", vds.features[Label.RESIDENCE])
        assertEquals("PA", vds.features[Label.TYPE_OF_ID])
        assertEquals("LG98GT5JR", vds.features[Label.ID_NUMBER])
        assertEquals("max.mustermann@kurzdigital.com", vds.features[Label.EMAIL])
        assertEquals("Hotgen 2019-nCoV Antigen", vds.features[Label.TEST_NAME])
        assertEquals("W20210306000", vds.features[Label.TEST_ID])
        assertEquals("SARS-CoV-2", vds.features[Label.TESTED_DISEASE])
        assertEquals("John Doe", vds.features[Label.SUPERVISOR])
        assertEquals(
            "KURZ Digital Solutions GmbH & Co. KG; Schwabacher Str. 106, 90763 Fürth, Germany",
            vds.features[Label.COMPANY],
        )
        assertEquals(
            "Thu Jan 20 11:40:00 CET 2022",
            vds.features[Label.TIMESTAMP].toString(),
        )
        assertEquals(1, (vds.features[Label.VALIDITY] as Byte).toInt())
        assertTrue(vds.verify())
    }

    @Test
    fun taxStamp() {
        val vds = readAndParse("vds_sample_taxstamp")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals("WAAK", vds.features[Label.ISSUER_ID])
        assertEquals("A348948923U", vds.features[Label.UI_SERIAL_NUMBER])
        assertEquals("Germany", vds.features[Label.MANUFACTURING_PLACE])
        assertEquals(
            "Schwabacher Str. 482, 90763",
            vds.features[Label.MANUFACTURING_FACILITY],
        )
        assertEquals("LM523", vds.features[Label.MACHINE])
        assertEquals("Marlboro Cigarettes", vds.features[Label.PRODUCT_DESCRIPTION])
        assertEquals("Malaysia", vds.features[Label.MARKET_OF_SALE])
        assertEquals("by air", vds.features[Label.SHIPMENT_ROUTE])
        assertEquals("Multifoil Sdn Bhd 5 Jalan 4/4C", vds.features[Label.IMPORTER])
        assertEquals("t1p.de/0w8z", vds.features[Label.LINK])
        assertTrue(vds.verify())
    }

    @Test
    fun demecanPatientID() {
        val vds = readAndParse("vds_sample_demecan_patient_id")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals(
            "UTO-012345",
            vds.features[Label.DOCUMENT_NUMBER].toString(),
        )
        assertEquals(
            "Sample Pia Angela",
            vds.features[Label.NAME].toString(),
        )
        assertEquals(
            "Mon Jun 12 00:00:00 CEST 1995",
            vds.features[Label.DATE_OF_BIRTH].toString(),
        )
        assertEquals(
            "Schwabacher Str. 106",
            vds.features[Label.ADDRESS_STREET].toString(),
        )
        assertEquals(
            "90763 Fürth",
            vds.features[Label.ADDRESS_CITY].toString(),
        )
        // The sample is signed with a key that would require Bouncy Castle
        // so we skip verification here.
    }

    @Test
    fun maliWsl() {
        val vds = readAndParse("vds_sample_mali_wsl")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals(
            "QUAEM6DEE4EO",
            vds.features[Label.UNIQUE_CODE].toString(),
        )
        assertEquals(
            "013210",
            vds.features[Label.SERIAL_NUMBER].toString(),
        )
        assertEquals(
            "V/2",
            vds.features[Label.TYPE].toString(),
        )
        assertEquals(
            9000,
            vds.features[Label.CHARGE],
        )
        assertEquals(
            2024,
            vds.features[Label.YEAR],
        )
        // The sample is signed with a key that would require Bouncy Castle
        // so we skip verification here.
    }

    @Test
    fun healthInsuranceCard() {
        val vds = readAndParse("vds_sample_health_insurance_card")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals(
            "652584509",
            vds.features[Label.SERIAL_NUMBER],
        )
        assertEquals(
            "Carlos Araújo",
            vds.features[Label.NAME],
        )
        assertEquals(
            "Thu Feb 23 00:00:00 CET 1989",
            vds.features[Label.DATE_OF_BIRTH].toString(),
        )
        assertEquals(
            "Plano PRO+",
            vds.features[Label.BENEFITS],
        )
        assertEquals(
            "Apartamento | enfermaria",
            vds.features[Label.ACCOMMODATION],
        )
        assertEquals(
            "Fri Jul 10 00:00:00 CEST 2026",
            vds.features[Label.EXPIRATION_DATE].toString(),
        )
        assertEquals(
            "SP RJ DF",
            vds.features[Label.SERVICE_NETWORK],
        )
        assertEquals(
            "XZh6ZnCFVl6iUah6N1JzkVdafGmSaXuERyhgXndTWmhMb2aKcXKfc1JobrWkhnlqkTihkJZ2iE5pnE2KfWlodbmXSmimYXl6Wz4qkdKmQXRheZSKYnNghpN3Zq94hXiHXXFScolseKJQj31xfn9NWI4yjpuFnJGGjmBGb4tvioA=",
            java.util.Base64.getEncoder().encodeToString(
                vds.features[Label.BIOMETRICS] as ByteArray,
            ),
        )
        // The sample is signed with a key that would require Bouncy Castle
        // so we skip verification here.
    }

    @Test
    fun ticketDemonstrator() {
        val vds = readAndParse("vds_sample_ticket_demonstrator")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals(
            "Scorpions",
            vds.features[Label.EVENT_TITLE],
        )
        assertEquals(
            "Sat Jun 01 19:00:00 CEST 2024",
            vds.features[Label.TIMESTAMP].toString(),
        )
        assertEquals(
            "002359",
            vds.features[Label.TICKET_NUMBER],
        )
        assertEquals(
            "Quasimodo",
            vds.features[Label.LOCATION_NAME],
        )
        assertEquals(
            "113",
            vds.features[Label.SEAT_SECTION],
        )
        assertEquals(
            "AK",
            vds.features[Label.SEAT_ROW],
        )
        assertEquals(
            "45",
            vds.features[Label.SEAT_NUMBER],
        )
        assertEquals(
            "John Smith",
            vds.features[Label.NAME],
        )
        assertEquals(
            "Sat Jan 01 00:00:00 CET 2000",
            vds.features[Label.DATE_OF_BIRTH].toString(),
        )
        assertEquals(
            "XZh6ZnCFVl6iUah6N1JzkVdafGmSaXuERyhgXndTWmhMb2aKcXKfc1JobrWkhnlqkTihkJZ2iE5pnE2KfWlodbmXSmimYXl6Wz4qkdKmQXRheZSKYnNghpN3Zq94hXiHXXFScolseKJQj31xfn9NWI4yjpuFnJGGjmBGb4tvioA=",
            java.util.Base64.getEncoder().encodeToString(
                vds.features[Label.BIOMETRICS] as ByteArray,
            ),
        )
        // The sample is signed with a key that would require Bouncy Castle
        // so we skip verification here.
    }

    @Test
    fun schoolAccess() {
        val vds = readAndParse("vds_sample_school_access")
        assertNotNull(vds)
        assertEquals("UTO", vds.header.countryId)
        assertEquals(
            "652584509",
            vds.features[Label.STUDENT_ID],
        )
        assertEquals(
            "Ecole de Internationale",
            vds.features[Label.SCHOOL_NAME],
        )
        assertEquals(
            "Smith",
            vds.features[Label.SURNAME],
        )
        assertEquals(
            "John",
            vds.features[Label.GIVEN_NAME],
        )
        assertEquals(
            "3 F",
            vds.features[Label.CLASS],
        )
        assertEquals(
            "W5d5Y22FWGGiU6V7NVVykFZZfmqUZ3qDRyhhYHhSXWpLbWeNc3Kgc1BpcbWhhXlrkTaikZl1h01rn0yLf2hodLWVSWikY3l6Wz4rlNGmQ3VhepKJYHNghpJ5aK55h3aKXG9Sc4hqdqNOjHtvfX5PV40yjJ2DnJOEkF9Hb4xvh4A=",
            java.util.Base64.getEncoder().encodeToString(
                vds.features[Label.BIOMETRICS] as ByteArray,
            ),
        )
        assertTrue(vds.verify())
    }

    private fun readAndParse(
        name: String,
    ) = ClassLoader.getSystemResource(
        name,
    ).readBytes().decodeVds()

    private fun Vds.verify() = verify(
        CertificateListIterator(certificates),
    ).isValid()
}
