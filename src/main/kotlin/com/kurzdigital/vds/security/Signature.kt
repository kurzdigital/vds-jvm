package com.kurzdigital.vds.security

import org.spongycastle.asn1.ASN1Integer
import org.spongycastle.asn1.DERSequenceGenerator
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest
import java.security.PublicKey
import java.security.Signature
import java.security.cert.Certificate
import java.security.cert.X509Certificate
import java.util.Date

fun ByteArray.sha256(): ByteArray = MessageDigest.getInstance("SHA-256").run {
    update(this@sha256)
    digest()
}

fun verify(
    certificateIterator: CertificateIterator,
    sha256: ByteArray,
    signature: ByteArray,
    date: Date = Date(),
): Boolean {
    while (true) {
        val certificate = certificateIterator.next() ?: break
        if (certificate.validAt(date) &&
            verify(certificate.publicKey, sha256, signature)
        ) {
            return true
        }
    }
    return false
}

fun verify(
    publicKey: PublicKey,
    sha256: ByteArray,
    signature: ByteArray,
): Boolean = try {
    Signature.getInstance("NONEwithECDSA", provider).run {
        initVerify(publicKey)
        update(sha256)
        verify(signature)
    }
} catch (e: Exception) {
    // Catch all kinds of nasty exceptions we can't do anything about
    // so let's just say verification failed and return false.
    // Remember, `bytes` is arbitrary, potentially hostile user data
    // and from the perspective of the app we just don't care why
    // malformed or even hostile data could not be decoded.
    false
}

fun Certificate.validAt(date: Date) = this !is X509Certificate ||
    !(date.before(notBefore) || date.after(notAfter))

fun ByteArray.concatenatedRSToASN1DER(): ByteArray {
    val len = size / 2
    val arraySize = len + 1
    val r = ByteArray(arraySize)
    val s = ByteArray(arraySize)
    System.arraycopy(this, 0, r, 1, len)
    System.arraycopy(this, len, s, 1, len)
    val rBigInteger = BigInteger(r)
    val sBigInteger = BigInteger(s)
    val bos = ByteArrayOutputStream()
    try {
        val seqGen = DERSequenceGenerator(bos)
        seqGen.addObject(ASN1Integer(rBigInteger.toByteArray()))
        seqGen.addObject(ASN1Integer(sBigInteger.toByteArray()))
        seqGen.close()
        bos.close()
    } catch (e: IOException) {
        throw RuntimeException("Failed to generate ASN.1 DER signature", e)
    }
    return bos.toByteArray()
}
