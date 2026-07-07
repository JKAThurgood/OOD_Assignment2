package audio.waveStrategy

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SquareWaveTest {

    private val squareWave = SquareWave()

    @Test
    fun `returns positive one at time zero`() {
        val sample = squareWave.generateSample(
            time = 0.0,
            frequency = 440.0
        )

        assertEquals(1.0, sample)
    }

    @Test
    fun `returns positive one during first half of period`() {
        val frequency = 100.0
        val quarterPeriod = 1.0 / (4 * frequency)

        val sample = squareWave.generateSample(
            time = quarterPeriod,
            frequency = frequency
        )

        assertEquals(1.0, sample)
    }

    @Test
    fun `returns negative one during second half of period`() {
        val frequency = 100.0
        val threeQuarterPeriod = 3.0 / (4 * frequency)

        val sample = squareWave.generateSample(
            time = threeQuarterPeriod,
            frequency = frequency
        )

        assertEquals(-1.0, sample)
    }

    @Test
    fun `output only contains positive or negative one`() {
        val samples = (0..100).map {
            squareWave.generateSample(
                time = it / 1000.0,
                frequency = 440.0
            )
        }

        samples.forEach { sample ->
            assert(sample == 1.0 || sample == -1.0)
        }
    }
}