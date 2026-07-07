package audio.musicModel

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class PitchTest {

    @Test
    fun `parses natural notes`() {
        assertEquals(Pitch.A, Pitch.fromString("A"))
        assertEquals(Pitch.C, Pitch.fromString("C"))
        assertEquals(Pitch.G, Pitch.fromString("G"))
    }

    @Test
    fun `parses sharp notes`() {
        assertEquals(Pitch.CSharp, Pitch.fromString("C#"))
        assertEquals(Pitch.DSharp, Pitch.fromString("D#"))
        assertEquals(Pitch.GSharp, Pitch.fromString("G#"))
    }

    @Test
    fun `parses flat notes`() {
        assertEquals(Pitch.AFlat, Pitch.fromString("Ab"))
        assertEquals(Pitch.BFlat, Pitch.fromString("Bb"))
        assertEquals(Pitch.DFlat, Pitch.fromString("Db"))
    }

    @Test
    fun `parses all supported pitches`() {
        val pitches = mapOf(
            "A" to Pitch.A,
            "A#" to Pitch.ASharp,
            "Ab" to Pitch.AFlat,
            "B" to Pitch.B,
            "Bb" to Pitch.BFlat,
            "C" to Pitch.C,
            "C#" to Pitch.CSharp,
            "Cb" to Pitch.CFlat,
            "D" to Pitch.D,
            "D#" to Pitch.DSharp,
            "Db" to Pitch.DFlat,
            "E" to Pitch.E,
            "Eb" to Pitch.EFlat,
            "F" to Pitch.F,
            "F#" to Pitch.FSharp,
            "Fb" to Pitch.FFlat,
            "G" to Pitch.G,
            "G#" to Pitch.GSharp,
            "Gb" to Pitch.GFlat
        )

        pitches.forEach { (text, expected) ->
            assertEquals(expected, Pitch.fromString(text))
        }
    }

    @Test
    fun `throws for invalid pitch`() {
        assertThrows(IllegalArgumentException::class.java) {
            Pitch.fromString("H")
        }
    }

    @Test
    fun `throws for empty pitch`() {
        assertThrows(IllegalArgumentException::class.java) {
            Pitch.fromString("")
        }
    }

    @Test
    fun `pitch contains correct semitone offsets`() {
        assertEquals(0, Pitch.C.semitoneOffset)
        assertEquals(1, Pitch.CSharp.semitoneOffset)
        assertEquals(1, Pitch.DFlat.semitoneOffset)
        assertEquals(9, Pitch.A.semitoneOffset)
    }

    @Test
    fun `enharmonic pitches have same offset`() {
        assertEquals(
            Pitch.CSharp.semitoneOffset,
            Pitch.DFlat.semitoneOffset
        )

        assertEquals(
            Pitch.ASharp.semitoneOffset,
            Pitch.BFlat.semitoneOffset
        )
    }

    @Test
    fun `parses pitch names`() {
        assertEquals(Pitch.C, Pitch.fromString("C"))
        assertEquals(Pitch.CSharp, Pitch.fromString("C#"))
        assertEquals(Pitch.DFlat, Pitch.fromString("Db"))
    }
}