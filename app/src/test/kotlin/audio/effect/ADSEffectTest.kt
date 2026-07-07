package audio.effect

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ADSEffectTest {

    @Test
    fun `attack ramps from zero to full volume`() {
        val effect = ADSEffect(
            attackEnd = 1.0,
            decayEnd = 1.0,
            sustain = 0.5,
            source = ConstantSource(1.0)
        )

        assertEquals(
            0.0,
            effect.generateSample(0.0, 440.0),
            1e-10
        )

        assertEquals(
            0.5,
            effect.generateSample(0.5, 440.0),
            1e-10
        )

        assertEquals(
            1.0,
            effect.generateSample(1.0, 440.0),
            1e-10
        )
    }

    @Test
    fun `decay moves from full volume to sustain`() {
        val effect = ADSEffect(
            attackEnd = 1.0,
            decayEnd = 1.0,
            sustain = 0.5,
            source = ConstantSource(1.0)
        )

        assertEquals(
            0.75,
            effect.generateSample(1.5, 440.0),
            1e-10
        )

        assertEquals(
            0.5,
            effect.generateSample(2.0, 440.0),
            1e-10
        )
    }

    @Test
    fun `after decay envelope remains at sustain`() {
        val effect = ADSEffect(
            attackEnd = 1.0,
            decayEnd = 1.0,
            sustain = 0.3,
            source = ConstantSource(1.0)
        )

        assertEquals(
            0.3,
            effect.generateSample(5.0, 440.0),
            1e-10
        )
    }
}