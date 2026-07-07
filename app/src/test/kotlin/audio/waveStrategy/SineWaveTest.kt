package audio.waveStrategy

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.PI

class SinWaveTest {

    private val sinWave = SinWave()

    @Test
    fun `returns zero at time zero`() {
        val sample = sinWave.generateSample(
            time = 0.0,
            frequency = 440.0
        )

        assertEquals(0.0, sample, 1e-10)
    }

    @Test
    fun `returns positive one at quarter period`() {
        val frequency = 100.0
        val quarterPeriod = 1.0 / (4 * frequency)

        val sample = sinWave.generateSample(
            time = quarterPeriod,
            frequency = frequency
        )

        assertEquals(1.0, sample, 1e-10)
    }

    @Test
    fun `returns zero at half period`() {
        val frequency = 100.0
        val halfPeriod = 1.0 / (2 * frequency)

        val sample = sinWave.generateSample(
            time = halfPeriod,
            frequency = frequency
        )

        assertEquals(0.0, sample, 1e-10)
    }

    @Test
    fun `returns negative one at three quarter period`() {
        val frequency = 100.0
        val threeQuarterPeriod = 3.0 / (4 * frequency)

        val sample = sinWave.generateSample(
            time = threeQuarterPeriod,
            frequency = frequency
        )

        assertEquals(-1.0, sample, 1e-10)
    }

    @Test
    fun `output stays within valid waveform range`() {
        val samples = (0..100).map {
            sinWave.generateSample(
                time = it / 1000.0,
                frequency = 440.0
            )
        }

        samples.forEach { sample ->
            assert(sample in -1.0..1.0)
        }
    }
}