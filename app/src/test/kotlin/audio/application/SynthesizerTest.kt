package audio.application

import audio.core.AudioSource
import audio.musicModel.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SynthesizerTest {

    private val synthesizer = Synthesizer()

    private class ConstantSource(
        private val value: Double
    ) : AudioSource {
        override fun generateSample(
            time: Double,
            frequency: Double
        ): Double {
            return value
        }
    }

    private fun settings(
        source: AudioSource,
        event: MusicalEvent,
        sampleRate: Int = 100,
        tempo: Int = 60
    ): AudioSettings {
        return AudioSettings(
            channels = listOf(
                ChannelSettings(
                    source = source,
                    measures = listOf(
                        Measure(listOf(event))
                    )
                )
            ),
            sampleRate = sampleRate,
            beatsPerMeasure = 4,
            tempo = tempo
        )
    }


    @Test
    fun `renders note into samples`() {
        val result = synthesizer.render(
            settings(
                ConstantSource(1.0),
                Note(Pitch.C, 4, 1.0)
            )
        )

        // 60 BPM = 1 second per beat
        // 100 samples/sec = 100 samples
        assertEquals(100, result.size)

        assertEquals(
            Short.MAX_VALUE,
            result[0]
        )
    }


    @Test
    fun `renders rest as silence`() {
        val result = synthesizer.render(
            settings(
                ConstantSource(1.0),
                Rest(1.0)
            )
        )

        assertEquals(100, result.size)

        assertTrue(
            result.all { it == 0.toShort() }
        )
    }


    @Test
    fun `mixes multiple channels`() {
        val settings = AudioSettings(
            channels = listOf(
                ChannelSettings(
                    ConstantSource(1.0),
                    listOf(
                        Measure(
                            listOf(
                                Note(Pitch.C, 4, 1.0)
                            )
                        )
                    )
                ),
                ChannelSettings(
                    ConstantSource(1.0),
                    listOf(
                        Measure(
                            listOf(
                                Note(Pitch.C, 4, 1.0)
                            )
                        )
                    )
                )
            ),
            sampleRate = 100,
            beatsPerMeasure = 4,
            tempo = 60
        )

        val result = synthesizer.render(settings)

        assertEquals(
            Short.MAX_VALUE,
            result[0]
        )
    }


    @Test
    fun `averages channels during mixing`() {
        val settings = AudioSettings(
            channels = listOf(
                ChannelSettings(
                    ConstantSource(1.0),
                    listOf(
                        Measure(
                            listOf(
                                Note(Pitch.C, 4, 1.0)
                            )
                        )
                    )
                ),
                ChannelSettings(
                    ConstantSource(0.0),
                    listOf(
                        Measure(
                            listOf(
                                Note(Pitch.C, 4, 1.0)
                            )
                        )
                    )
                )
            ),
            sampleRate = 100,
            beatsPerMeasure = 4,
            tempo = 60
        )

        val result = synthesizer.render(settings)

        assertEquals(
            16383,
            result[0].toInt()
        )
    }

    @Test
    fun `clips samples above maximum range`() {
        val result = synthesizer.render(
            settings(
                ConstantSource(2.0),
                Note(Pitch.C, 4, 1.0)
            )
        )

        assertEquals(
            Short.MAX_VALUE,
            result[0]
        )
    }


    @Test
    fun `clips samples below minimum range`() {
        val result = synthesizer.render(
            settings(
                ConstantSource(-2.0),
                Note(Pitch.C, 4, 1.0)
            )
        )

        assertEquals(
            (-Short.MAX_VALUE).toShort(),
            result[0]
        )
    }

    @Test
    fun `throws when sample rate is invalid`() {
        val settings = settings(
            ConstantSource(1.0),
            Note(Pitch.C, 4, 1.0),
            sampleRate = 0
        )

        assertThrows<IllegalArgumentException> {
            synthesizer.render(settings)
        }
    }


    @Test
    fun `throws when tempo is invalid`() {
        val settings = settings(
            ConstantSource(1.0),
            Note(Pitch.C, 4, 1.0),
            tempo = 0
        )

        assertThrows<IllegalArgumentException> {
            synthesizer.render(settings)
        }
    }


    @Test
    fun `throws when no channels exist`() {
        val settings = AudioSettings(
            channels = emptyList(),
            sampleRate = 100,
            beatsPerMeasure = 4,
            tempo = 60
        )

        assertThrows<IllegalArgumentException> {
            synthesizer.render(settings)
        }
    }
}
