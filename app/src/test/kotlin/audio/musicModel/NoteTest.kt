package audio.musicModel

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.pow

class NoteTest {

    @Test
    fun `A4 returns 440 Hz`() {
        val note = Note(
            pitch = Pitch.A,
            octave = 4,
            duration = 1.0
        )

        assertEquals(440.0, note.toFrequency(), 1e-10)
    }

    @Test
    fun `C4 returns approximately middle C frequency`() {
        val note = Note(
            pitch = Pitch.C,
            octave = 4,
            duration = 1.0
        )

        assertEquals(261.63, note.toFrequency(), 0.01)
    }

    @Test
    fun `octave increase doubles frequency`() {
        val c4 = Note(Pitch.C, 4, 1.0)
        val c5 = Note(Pitch.C, 5, 1.0)

        assertEquals(
            c4.toFrequency() * 2,
            c5.toFrequency(),
            1e-10
        )
    }

    @Test
    fun `sharp and flat enharmonic notes have same frequency`() {
        val cSharp = Note(Pitch.CSharp, 4, 1.0)
        val dFlat = Note(Pitch.DFlat, 4, 1.0)

        assertEquals(
            cSharp.toFrequency(),
            dFlat.toFrequency(),
            1e-10
        )
    }

    @Test
    fun `duration is stored correctly`() {
        val note = Note(
            pitch = Pitch.G,
            octave = 5,
            duration = 2.5
        )

        assertEquals(2.5, note.duration)
    }
}