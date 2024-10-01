package com.kurzdigital.vds.vds

import com.kurzdigital.vds.security.concatenatedRSToASN1DER
import com.kurzdigital.vds.security.decodeC40
import com.kurzdigital.vds.security.sha256
import com.kurzdigital.vds.vds.feature.mapAddressStickerGermanIDCard
import com.kurzdigital.vds.vds.feature.mapAddressStickerGermanPassport
import com.kurzdigital.vds.vds.feature.mapArrivalAttestation
import com.kurzdigital.vds.vds.feature.mapDemecanPatientId
import com.kurzdigital.vds.vds.feature.mapEmergencyTravel
import com.kurzdigital.vds.vds.feature.mapEmergencyTravelSingleJourney
import com.kurzdigital.vds.vds.feature.mapEmergencyTravelWithBio
import com.kurzdigital.vds.vds.feature.mapGeneralPurpose
import com.kurzdigital.vds.vds.feature.mapHealthInsuranceCard
import com.kurzdigital.vds.vds.feature.mapMaliWsl
import com.kurzdigital.vds.vds.feature.mapPharmapack
import com.kurzdigital.vds.vds.feature.mapResidencePermitProfile
import com.kurzdigital.vds.vds.feature.mapSchoolAccess
import com.kurzdigital.vds.vds.feature.mapSocialInsuranceCard
import com.kurzdigital.vds.vds.feature.mapSupervisedAntigenTest
import com.kurzdigital.vds.vds.feature.mapSupplementSheet
import com.kurzdigital.vds.vds.feature.mapTaxStamp
import com.kurzdigital.vds.vds.feature.mapTicketDemonstrator
import com.kurzdigital.vds.vds.feature.mapVaccinationCertificate
import com.kurzdigital.vds.vds.feature.mapVehicleVignette
import com.kurzdigital.vds.vds.feature.mapVisa
import java.nio.ByteBuffer

fun ByteArray.decodeVdsOrNull(): Vds? = try {
    decodeVds()
} catch (e: Exception) {
    null
}

fun ByteArray.decodeVds() = ByteBuffer.wrap(this).readVds()

private fun ByteBuffer.readVds(): Vds {
    if (get() != 0xdc.toByte()) {
        throw IllegalArgumentException("Magic Constant mismatch")
    }
    val header = readHeader()
    val features = HashMap<Byte, ByteArray>()
    while (true) {
        val pos = position()
        val tag = get()
        val value = getByteArray(get().toUByte().toInt())
        if (tag == 0xff.toByte()) {
            rewind()
            val (id, mapper) = header.getMapper()
            return Vds(
                id,
                header,
                mapper.invoke(features),
                getByteArray(pos).sha256(),
                value.concatenatedRSToASN1DER(),
                value,
            )
        }
        features[tag] = value
    }
}

private fun VdsHeader.getMapper(): Pair<VdsType, (Map<Byte, ByteArray>) -> Map<Any, Any?>> = when (id) {
    0x5D01 -> Pair(VdsType.VISA, ::mapVisa)
    0x5E03 -> Pair(VdsType.EMERGENCY_TRAVEL, ::mapEmergencyTravel)
    0x7E04 -> Pair(VdsType.EMERGENCY_TRAVEL_SINGLE_JOURNEY, ::mapEmergencyTravelSingleJourney)
    0x5F03 -> Pair(VdsType.EMERGENCY_TRAVEL_WITH_BIO, ::mapEmergencyTravelWithBio)
    0x6F01 -> Pair(VdsType.SUPERVISED_ANTIGEN_TEST, ::mapSupervisedAntigenTest)
    0x7001 -> Pair(VdsType.VACCINATION_CERTIFICATE, ::mapVaccinationCertificate)
    0x7A01 -> Pair(VdsType.TAX_STAMP, ::mapTaxStamp)
    0x7B01 -> Pair(VdsType.PHARMAPACK, ::mapPharmapack)
    0x7C01 -> Pair(VdsType.GENERAL_PURPOSE, ::mapGeneralPurpose)
    0x7D01 -> Pair(VdsType.VEHICLE_VIGNETTE, ::mapVehicleVignette)
    0xF80A -> Pair(VdsType.ADDRESS_STICKER_GERMAN_PASSPORT, ::mapAddressStickerGermanPassport)
    0xF908 -> Pair(VdsType.ADDRESS_STICKER_GERMAN_ID_CARD, ::mapAddressStickerGermanIDCard)
    0xFA06 -> Pair(VdsType.SUPPLEMENT_SHEET, ::mapSupplementSheet)
    0xFB06 -> Pair(VdsType.RESIDENCE_PERMIT, ::mapResidencePermitProfile)
    0xFC04 -> Pair(VdsType.SOCIAL_INSURANCE_CARD, ::mapSocialInsuranceCard)
    0xFD02 -> Pair(VdsType.ARRIVAL_ATTESTATION, ::mapArrivalAttestation)
    0x7F04 -> Pair(VdsType.DEMECAN_PATIENT_ID, ::mapDemecanPatientId)
    0x8a02 -> Pair(VdsType.MALI_WSL, ::mapMaliWsl)
    0x8b04 -> Pair(VdsType.TICKET_DEMONSTRATOR, ::mapTicketDemonstrator)
    0x8c04 -> Pair(VdsType.HEALTH_INSURANCE_CARD, ::mapHealthInsuranceCard)
    0x8D04 -> Pair(VdsType.SCHOOL_ACCESS, ::mapSchoolAccess)
    else -> throw IllegalArgumentException("Unknown document")
}

private fun ByteBuffer.readHeader() = VdsHeader(
    version = get(),
    countryId = getByteArray(2).decodeC40(),
    signerIdentifier = getByteArray(4).decodeC40(),
    certificateReference = getByteArray(2).decodeC40(),
    documentIssueDate = getDate(),
    signatureCreationDate = getDate(),
    docFeatureDefRef = get(),
    docTypeCategory = get(),
)

private fun ByteBuffer.getByteArray(size: Int): ByteArray {
    val buffer = ByteArray(size)
    get(buffer)
    return buffer
}
