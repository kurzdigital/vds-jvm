package com.kurzdigital.vds.security

import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.Test

class C40Tests {
    @Test
    fun padded() {
        // Last character is padded with 0xfe.
        assertEquals(
            "THE FOX JUMPS OVER THE LAZY DOG",
            byteArrayOfInts(
                0xd1, 0x9b, 0x15, 0xd5,
                0xe7, 0xd0, 0xd8, 0xae,
                0xc8, 0x95, 0xdd, 0xb0,
                0x17, 0xfe, 0x71, 0x12,
                0x5d, 0xbf, 0x15, 0x85,
                0xfe, 0x48,
            ).decodeC40(),
        )
    }

    @Test
    fun multipleOfTwo() {
        // Data is exactly a multiple of two.
        assertEquals(
            "THE FOX JUMPS OVER THE LAZY DO",
            byteArrayOfInts(
                0xd1, 0x9b, 0x15, 0xd5,
                0xe7, 0xd0, 0xd8, 0xae,
                0xc8, 0x95, 0xdd, 0xb0,
                0x17, 0xfe, 0x71, 0x12,
                0x5d, 0xbf, 0x15, 0x85,
            ).decodeC40(),
        )
    }

    @Test
    fun notAMultipleOfTwo() {
        // This is a special case where the data is not a
        // multiple of two. This case is not relevant to
        // VDS because C40 data is padded to be always a
        // a multiple of two there.
        assertEquals(
            "THE FOX JUMPS OVER THE LAZY D",
            byteArrayOfInts(
                0xd1, 0x9b, 0x15, 0xd5,
                0xe7, 0xd0, 0xd8, 0xae,
                0xc8, 0x95, 0xdd, 0xb0,
                0x17, 0xfe, 0x71, 0x12,
                0x5d, 0xbf, 0xfe, 0x21,
                0x45,
            ).decodeC40(),
        )
    }

    @Test
    fun justOneCharacter() {
        // Just one character. Again, padded with 0xfe.
        assertEquals(
            "A",
            byteArrayOfInts(0xfe, 0x42).decodeC40(),
        )
    }

    @Test
    fun broadInputRange() {
        assertEquals(
            "A123B4C56D7E890F",
            byteArrayOfInts(
                0x58, 0x4f, 0x2e, 0x21,
                0x65, 0x73, 0x6c, 0x0b,
                0x4d, 0x0d, 0xfe, 0x47,
            ).decodeC40(),
        )
    }
}

private fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos ->
    ints[pos].toByte()
}
