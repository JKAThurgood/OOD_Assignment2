package audio.effect

import audio.core.AudioSource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EffectDecoratorTest {

    @Test
    fun `decorator delegates sample generation to wrapped source`() {
        val source = CountingSource()
        val effect = VolumeEffect(
            level = 1.0,
            source = source
        )

        val result = effect.generateSample(
            time = 0.5,
            frequency = 440.0
        )

        assertEquals(1.0, result, 1e-10)

        assertEquals(1, source.callCount)
        assertEquals(0.5, source.lastTime, 1e-10)
        assertEquals(440.0, source.lastFrequency, 1e-10)
    }

    @Test
    fun `multiple effects share same wrapped source chain`() {
        val source = CountingSource()

        val effectChain = VolumeEffect(
            level = 0.5,
            source = ClipEffect(
                threshold = 1.0,
                source = source
            )
        )

        val result = effectChain.generateSample(
            time = 1.0,
            frequency = 220.0
        )

        assertEquals(0.5, result, 1e-10)
        assertEquals(1, source.callCount)
    }

    private class CountingSource : AudioSource {
        var callCount = 0
        var lastTime = 0.0
        var lastFrequency = 0.0

        override fun generateSample(
            time: Double,
            frequency: Double
        ): Double {
            callCount++
            lastTime = time
            lastFrequency = frequency
            return 1.0
        }
    }
}