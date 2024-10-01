# VDS/VDS-NC Parser for the JVM

Parse and verify a [Visible Digital Seal][vds] or VDS-NC (Non-Constrained).

## How to use

### Reading the barcode

Reading a barcode is not part of this library. Here are a few options:

#### Google Code Scanner on Android

On Android, you can use the [Google Code Scanner][codescanner] from ML Kit.

#### Google ML Kit

If you want more control over the scanning process, you can use ML Kit's
[barcode scanning API][vision] on Android and/or iOS.

#### ZXing

For everything on the JVM, you might want to use the [ZXing][zxing]
barcode scanning library.

**Note**: VDS DataMatrix barcodes contain binary data. To extract the raw
byte array from a ZXing [Result][result] object, the `BYTE_SEGMENTS` need to
be appended manually. Unfortunately, [Result.getRawBytes()][getrawbytes] cannot
be used because it returns the raw encoded data, not the payload in it.

Here's how to do it in Kotlin:

```kotlin
import com.google.zxing.Result
import com.google.zxing.ResultMetadataType

fun Result.getRawData(): ByteArray? {
	val metadata = resultMetadata ?: return null
	val segments = metadata[ResultMetadataType.BYTE_SEGMENTS] ?: return null
	var bytes = ByteArray(0)
	@Suppress("UNCHECKED_CAST")
	for (seg in segments as Iterable<ByteArray>) {
		bytes += seg
	}
	// If the byte segments are shorter than the converted string, the
	// content of the QR Code has been encoded with different encoding
	// modes (e.g. some parts in alphanumeric, some in byte encoding).
	// This is because Zxing only records byte segments for byte encoded
	// parts. Please note the byte segments can actually be longer than
	// the string because Zxing cuts off prefixes like "WIFI:".
	return if (bytes.size >= text.length) bytes else null
}
```

#### ZXing C++ fork

For other platforms, there is an excellent [C++ fork][zxingcpp] of the ZXing
library that runs on a variety of systems.

Including [Android][barcodescannerview].

### Parse the barcode contents

VDS are binary data, VDS-NC are just text.

#### VDS

Once you have a `ByteArray` with the content of a VDS barcode, you can
parse and verify it like this:

```kotlin
import com.kurzdigital.vds.security.CertificateListIterator
import com.kurzdigital.vds.vds.decodeVds
import com.kurzdigital.vds.vds.labelStringPairs
import com.kurzdigital.vds.Label
import java.security.cert.Certificate

fun parseAndVerifyVds(
	content: ByteArray,
	certificates: List<Certificate>
): Boolean {
	val vds = content.decodeVdsOrNull() ?: return false

	// Inspect vds.header here if desired.

	// Either inspect specific features of specific types.
	when (vds.type) {
		VISA -> {
			// Do something with:
			vds.features[Label.MRZ].toString()
			vds.features[Label.ARZ]
		}
		// …
	}

	// Or just enumerate all features.
	for (feature in vds.features.labelStringPairs()) {
		// feature is of type Pair<Label, String>
	}

	// Verify with your list of certificates.
	return vds.verify(
		CertificateListIterator(certificates)
	)
}
```

See the test sources for a sample of how to load a list of `Certificate`s.

#### VDS-NC

This is how to parse and verify VDS-NC:

```kotlin
import com.kurzdigital.vds.vds.decodeVdsNcOrNull
import java.security.cert.TrustAnchor

fun parseAndVerifyVdsNc(
	content: String,
	trustAnchors: Set<TrustAnchor>
): Boolean {
	val vdsNc = content.decodeVdsNcOrNull() ?: return false

	// Inspect vdsNc.header here if desired.

	// Either inspect specific features of specific types.
	when (vdsNc.type) {
		PROOF_OF_TEST -> // …
		PROOF_OF_VACCINATION -> // …
	}

	// Or just enumerate all features.
	for (feature in vdsNc.features) {
		// feature is of type Pair<String, String>
	}

	// Verify with your set of trust anchors.
	return when (vdsNc.verify(trustAnchors)) {
		SIGNATURE_INVALID -> false
		SIGNATURE_VALID -> true
		SIGNATURE_VALID_BUT_CERTIFICATE_UNKNOWN -> false
	}
}
```

You can read the `TrustAnchor`s from an `InputStream` that holds a
CSCA Master List with `com.kurzdigital.vds.security.readCscaMasterList`.
See the test sources for a sample of how to do this.

### Read VDS and VDS-NC

The simplest approach is to just try and parse:

```kotlin
import com.google.zxing.Result
import com.kurzdigital.vds.vds.Vds
import com.kurzdigital.vds.vds.VdsNc

fun parseAndVerify(result: Result) {
	val raw = result.getRawData() // Not getRawBytes()! See above.
	val vds = raw?.decodeVdsOrNull() ?: result.text.decodeVdsNcOrNull()
	when (vds) {
		is Vds -> // …
		is VdsNc -> // …
		else -> // …
	}
}
```

### What about Java?

In Java, you would call the Kotlin extension functions like
`ByteArray.decodeVdsOrNull()` this way:

```java
import com.kurzdigital.vds.vds.DecoderKt;
import com.kurzdigital.vds.vds.Vds;

class VdsDecoder {
	public static boolean parseAndVerify(byte[] content) {
		Vds vds = DecoderKt.decodeVdsOrNull(content);
		// …
	}
}
```

## How to include

### Gradle

Add the JitPack repository to your root `build.gradle` at the end of
repositories:

```groovy
allprojects {
	repositories {
		// …
		maven { url 'https://jitpack.io' }
	}
}
```

Then add the dependency in your `app/build.gradle`:

```groovy
dependencies {
	// …
	implementation ('com.github.kurzdigital:vds-jvm:1.0.0', {
		exclude group:'org.json', module:'json'
	})
}
```

**Note**: The `json` module needs to be excluded on Android because Android
already contains the JSON classes.

### Maven

Add the JitPack repository to your `pom.xml`:

```xml
	<repositories>
		<repository>
			<id>jitpack.io</id>
			<url>https://jitpack.io</url>
		</repository>
	</repositories>
```

Add the dependency:

```xml
	<dependency>
		<groupId>com.github.kurzdigital</groupId>
		<artifactId>vds-jvm</artifactId>
		<version>1.3.15</version>
	</dependency>
```

[vds]: https://visibledigitalseal.org/
[codescanner]: https://developers.google.com/ml-kit/code-scanner
[vision]: https://developers.google.com/ml-kit/vision/barcode-scanning
[zxing]: https://github.com/zxing/zxing
[result]: https://zxing.github.io/zxing/apidocs/com/google/zxing/Result.html
[getrawbytes]: https://zxing.github.io/zxing/apidocs/com/google/zxing/Result.html#getRawBytes--
[zxingcpp]: https://github.com/nu-book/zxing-cpp
[barcodescannerview]: https://github.com/markusfisch/BarcodeScannerView
