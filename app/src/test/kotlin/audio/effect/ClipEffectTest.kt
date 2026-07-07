package audio.effect

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ClipEffectTest {

    @Test
    fun `clips samples above threshold`() {
        val effect = ClipEffect(
            threshold = 0.5,
            source = ConstantSource(1.0)
        )

        assertEquals(
            0.5,
            effect.generateSample(0.0, 440.0),
            1e-10
        )
    }

    @Test
    fun `clips samples below negative threshold`() {
        val effect = ClipEffect(
            threshold = 0.5,
            source = ConstantSource(-1.0)
        )

        assertEquals(
            -0.5,
            effect.generateSample(0.0, 440.0),
            1e-10
        )
    }

    @Test
    fun `does not change samples inside threshold`() {
        val effect = ClipEffect(
            threshold = 0.5,
            source = ConstantSource(0.25)
        )

        assertEquals(
            0.25,
            effect.generateSample(0.0, 440.0),
            1e-10
        )
    }
}