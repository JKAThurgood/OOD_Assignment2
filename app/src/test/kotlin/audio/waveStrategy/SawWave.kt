package audio.waveStrategy

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SawWaveTest {

    private val sawWave = SawWave()

    @Test
    fun `starts at negative one at beginning of cycle`() {
        val sample = sawWave.generateSample(
            time = 0.0,
            frequency = 440.0
        )

        assertEquals(-1.0, sample, 1e-10)
    }

    @Test
    fun `reaches zero at halfway through period`() {
        val frequency = 440.0
        val period = 1.0 / frequency

        val sample = sawWave.generateSample(
            time = period / 2,
            frequency = frequency
        )

        assertEquals(0.0, sample, 1e-10)
    }

    @Test
    fun `approaches positive one before cycle resets`() {
        val frequency = 440.0
        val period = 1.0 / frequency

        val sample = sawWave.generateSample(
            time = period * 0.999,
            frequency = frequency
        )

        assertEquals(0.998, sample, 1e-3)
    }

    @Test
    fun `resets after one full period`() {
        val frequency = 440.0
        val period = 1.0 / frequency

        val sampleAtStart = sawWave.generateSample(
            time = 0.0,
            frequency = frequency
        )

        val sampleAfterPeriod = sawWave.generateSample(
            time = period,
            frequency = frequency
        )

        assertEquals(sampleAtStart, sampleAfterPeriod, 1e-10)
    }

    @Test
    fun `works at different frequencies`() {
        val frequency = 100.0
        val period = 1.0 / frequency

        val sample = sawWave.generateSample(
            time = period / 4,
            frequency = frequency
        )

        // phase = 0.25 => 2(0.25)-1 = -0.5
        assertEquals(-0.5, sample, 1e-10)
    }

    @Test
    fun `zero frequency should not produce invalid values`() {
        val sample = sawWave.generateSample(
            time = 0.0,
            frequency = 0.0
        )

        assertEquals(0.0, sample, 1e-10)
    }
}