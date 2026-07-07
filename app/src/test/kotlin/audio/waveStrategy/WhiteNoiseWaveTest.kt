package audio.waveStrategy

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class WhiteNoiseWaveTest {

    private val whiteNoiseWave = WhiteNoiseWave()

    @Test
    fun `generates samples within valid range`() {
        repeat(1000) {
            val sample = whiteNoiseWave.generateSample(
                time = it.toDouble(),
                frequency = 440.0
            )

            assertTrue(sample >= -1.0)
            assertTrue(sample <= 1.0)
        }
    }

    @Test
    fun `does not produce constant output`() {
        val samples = (0 until 100)
            .map {
                whiteNoiseWave.generateSample(
                    time = it.toDouble(),
                    frequency = 440.0
                )
            }
            .toSet()

        assertTrue(samples.size > 1)
    }

    @Test
    fun `frequency does not affect generated noise`() {
        val samples1 = (0 until 100)
            .map {
                whiteNoiseWave.generateSample(
                    time = it.toDouble(),
                    frequency = 100.0
                )
            }

        val samples2 = (0 until 100)
            .map {
                whiteNoiseWave.generateSample(
                    time = it.toDouble(),
                    frequency = 1000.0
                )
            }

        assertTrue(samples1.all { it in -1.0..1.0 })
        assertTrue(samples2.all { it in -1.0..1.0 })
    }
}