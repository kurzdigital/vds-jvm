package com.kurzdigital.vds.security

import org.spongycastle.asn1.ASN1Encodable
import org.spongycastle.asn1.ASN1InputStream
import org.spongycastle.asn1.ASN1ObjectIdentifier
import org.spongycastle.asn1.ASN1Primitive
import org.spongycastle.asn1.ASN1Sequence
import org.spongycastle.asn1.ASN1Set
import org.spongycastle.asn1.ASN1TaggedObject
import org.spongycastle.asn1.DEROctetString
import org.spongycastle.asn1.cms.SignedData
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.security.cert.CertPathBuilder
import java.security.cert.CertStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.CollectionCertStoreParameters
import java.security.cert.PKIXBuilderParameters
import java.security.cert.PKIXCertPathBuilderResult
import java.security.cert.TrustAnchor
import java.security.cert.X509CertSelector
import java.security.cert.X509Certificate
import java.util.Date

fun InputStream.readCscaMasterList() = getTrustedCertificates(
    CertificateFactory.getInstance("X.509", provider),
).toSet()

fun Set<TrustAnchor>.verifyDocumentCertificate(
    certificate: Certificate,
): Boolean {
    val x509 = certificate.toX509()
    if (x509.notAfter.before(Date())) {
        return false
    }
    val builder: CertPathBuilder
    val buildParams: PKIXBuilderParameters
    try {
        builder = CertPathBuilder.getInstance("PKIX", provider)
        buildParams = PKIXBuilderParameters(
            this,
            X509CertSelector(),
        ).apply {
            isRevocationEnabled = false
            maxPathLength = 0
            addCertStore(inCertStore(x509))
        }
    } catch (e: Exception) {
        return false
    }
    val result = try {
        builder.build(buildParams) as PKIXCertPathBuilderResult
    } catch (e: Exception) {
        // The document certificate was not signed by one of the
        // given trust anchors.
        return false
    }
    // certPath should contain exactly one certificate: the document
    // certificate. The trust anchor is the CA certificate from a
    // trusted "Country Signing Certificate Authority".
    return result.certPath.certificates.size > 0 && result.trustAnchor != null
}

private fun Certificate.toX509() = CertificateFactory.getInstance(
    "X.509",
).generateCertificate(
    ByteArrayInputStream(encoded),
) as X509Certificate

private fun inCertStore(vararg certificates: X509Certificate) = CertStore.getInstance(
    "Collection",
    CollectionCertStoreParameters(listOf(*certificates)),
    provider,
)

private fun InputStream.getTrustedCertificates(
    certFactory: CertificateFactory,
): List<TrustAnchor> {
    // Both the root certificates and the link certificates in the Master List
    // will be fully trusted. Trust a link certificate even if no self-signed
    // root certificate can be found in the Master List.
    // ICAO Doc9303 Part 12 Chapter 2:
    // https://www.icao.int/publications/Documents/9303_p12_cons_en.pdf
    // - CSCA Link certificates are not processed as intermediate
    //   certificates in a certification path.
    // - certification paths include precisely one certificate (e.g. Document Signer)
    // - A Master List is a digitally signed list of the CSCA certificates that are
    //   "trusted" by the receiving State that issued the Master List.
    //   CSCA self-signed Root certificates and CSCA Link certificates
    //   may be included in a Master List.
    val encapContentInfo = readSignedData().encapContentInfo
    val eContent = encapContentInfo.content as DEROctetString
    val contentIs = eContent.toASN1InputStream()
    val parentSequence = contentIs.readObject() as ASN1Sequence
    val setOfCerts = parentSequence.getObjectAt(1) as ASN1Set
    val anchors = mutableListOf<TrustAnchor>()
    val now = Date()
    for (c in setOfCerts) {
        val cert = certFactory.generateCertificate(c.toInputStream())
        if (cert is X509Certificate && cert.notAfter.after(now)) {
            anchors.add(TrustAnchor(cert, null))
        }
    }
    return anchors
}

private fun DEROctetString.toASN1InputStream() = ASN1InputStream(
    ByteArrayInputStream(this.octets),
)

private fun ASN1Encodable.toInputStream() = ByteArrayInputStream(
    toASN1Primitive().encoded,
)

private fun InputStream.readSignedData(): SignedData {
    val sequence = ASN1InputStream(this).readObject() as ASN1Sequence
    if (sequence.size() != 2) {
        throw IOException(
            "Was expecting a DER sequence of length 2, found a DER sequence of length ${sequence.size()}",
        )
    }
    val contentTypeOID = (sequence.getObjectAt(0) as ASN1ObjectIdentifier).id
    if (contentTypeOID != "1.2.840.113549.1.7.2") {
        throw IOException(
            "Was expecting signed-data content type OID, found $contentTypeOID",
        )
    }
    return SignedData.getInstance(
        sequence.getObjectAt(1).getObjectFromTaggedObject() as? ASN1Sequence
            ?: throw IOException("Was expecting an ASN.1 sequence as content"),
    )
}

private fun ASN1Encodable.getObjectFromTaggedObject(): ASN1Primitive? {
    if (this !is ASN1TaggedObject) {
        throw IOException(
            "Was expecting an ASN1TaggedObject, found ${javaClass.canonicalName}",
        )
    }
    if (tagNo != 0) {
        throw IOException("Was expecting tag 0, found ${Integer.toHexString(tagNo)}")
    }
    return getObject()
}
