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

    @Test
    fun `attack phase uses linear ramp`() {
        val effect = ADSEffect(
            attackEnd = 1.0,
            decayEnd = 1.0,
            sustain = 0.5,
            source = ConstantSource(1.0)
        )

        val sample = effect.generateSample(
            time = 0.5,
            frequency = 440.0
        )

        assertEquals(0.5, sample, 1e-10)
    }

    @Test
    fun `attack phase with zero attack end returns full amplitude`() {
        val effect = ADSEffect(
            attackEnd = 0.0,
            decayEnd = 1.0,
            sustain = 0.5,
            source = ConstantSource(1.0)
        )

        val sample = effect.generateSample(
            time = 0.0,
            frequency = 440.0
        )

        assertEquals(1.0, sample, 1e-10)
    }

    @Test
    fun `sustain phase returns sustain level`() {
        val effect = ADSEffect(
            attackEnd = 1.0,
            decayEnd = 1.0,
            sustain = 0.25,
            source = ConstantSource(1.0)
        )

        val sample = effect.generateSample(
            time = 5.0,
            frequency = 440.0
        )

        assertEquals(0.25, sample, 1e-10)
    }

    @Test
    fun `attack reaches full volume at attack end`() {
        val effect =
            ADSEffect(
                0.1,
                0.2,
                0.5,
                ConstantSource(1.0)
            )

        assertEquals(
            1.0,
            effect.generateSample(0.1, 440.0),
            1e-10
        )
    }

    @Test
    fun `decay interpolates between attack and sustain`() {
        val effect =
            ADSEffect(
                0.1,
                0.2,
                0.5,
                ConstantSource(1.0)
            )

        assertEquals(
            0.75,
            effect.generateSample(0.15, 440.0),
            1e-10
        )
    }

    @Test
    fun `decay reaches sustain at decay end`() {
        val effect =
            ADSEffect(
                0.1,
                0.2,
                0.5,
                ConstantSource(1.0)
            )

        assertEquals(
            0.5,
            effect.generateSample(0.2, 440.0),
            1e-10
        )
    }

    @Test
    fun `after decay end remains at sustain`() {
        val effect =
            ADSEffect(
                0.1,
                0.2,
                0.5,
                ConstantSource(1.0)
            )

        assertEquals(
            0.5,
            effect.generateSample(0.25, 440.0),
            1e-10
        )
    }
}