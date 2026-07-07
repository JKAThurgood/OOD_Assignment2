package audio.application

import audio.effect.*
import audio.musicModel.*
import audio.waveStrategy.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

class SongParserTest {

    private val parser = SongParser()

    private fun createTempSong(contents: String): File {
        val file = File.createTempFile("song_test", ".txt")
        file.writeText(contents)
        file.deleteOnExit()
        return file
    }

    @Test
    fun `parses basic song header`() {
        val file = createTempSong(
            """
            44100 4 120
            sin | C4 1 D4 1
            """.trimIndent()
        )

        val settings = parser.parse(file.path)

        assertEquals(44100, settings.sampleRate)
        assertEquals(4, settings.beatsPerMeasure)
        assertEquals(120, settings.tempo)
        assertEquals(1, settings.channels.size)
    }

    @Test
    fun `parses notes correctly`() {
        val file = createTempSong(
            """
            44100 4 120
            sin | C4 1 G4 2
            """.trimIndent()
        )

        val settings = parser.parse(file.path)

        val notes = settings.channels[0]
            .measures[0]
            .events

        assertEquals(2, notes.size)

        val c = notes[0] as Note
        val g = notes[1] as Note

        assertEquals(Pitch.C, c.pitch)
        assertEquals(4, c.octave)
        assertEquals(1.0, c.duration)

        assertEquals(Pitch.G, g.pitch)
        assertEquals(4, g.octave)
        assertEquals(2.0, g.duration)
    }

    @Test
    fun `parses rests correctly`() {
        val file = createTempSong(
            """
            44100 4 120
            sin | - 2
            """.trimIndent()
        )

        val settings = parser.parse(file.path)

        val event = settings.channels[0]
            .measures[0]
            .events[0]

        assertTrue(event is Rest)
        assertEquals(2.0, event.duration)
    }

    @Test
    fun `parses waveform types`() {
        val waveforms = mapOf(
            "sin" to SinWave::class,
            "square" to SquareWave::class,
            "saw" to SawWave::class,
            "whitenoise" to WhiteNoiseWave::class
        )

        waveforms.forEach { (name, expectedClass) ->
            val file = createTempSong(
                """
                44100 4 120
                $name | C4 1
                """.trimIndent()
            )

            val settings = parser.parse(file.path)

            assertEquals(
                expectedClass,
                settings.channels[0].source::class
            )
        }
    }

    @Test
    fun `throws when file does not exist`() {
        assertThrows<IllegalArgumentException> {
            parser.parse("does_not_exist.txt")
        }
    }

    @Test
    fun `throws when header is invalid`() {
        val file = createTempSong(
            """
            44100 120
            sin | C4 1
            """.trimIndent()
        )

        assertThrows<IllegalArgumentException> {
            parser.parse(file.path)
        }
    }

    @Test
    fun `throws when measure has uneven note duration pairs`() {
        val file = createTempSong(
            """
            44100 4 120
            sin | C4 1 D4
            """.trimIndent()
        )

        assertThrows<IllegalArgumentException> {
            parser.parse(file.path)
        }
    }

    @Test
    fun `parses effects`() {
        val file = createTempSong(
            """
            44100 4 120
            sin vol$0.5 tanh$2 clip$0.8 | C4 1
            """.trimIndent()
        )

        val settings = parser.parse(file.path)

        assertTrue(
            settings.channels[0].source is ClipEffect
        )
    }
}