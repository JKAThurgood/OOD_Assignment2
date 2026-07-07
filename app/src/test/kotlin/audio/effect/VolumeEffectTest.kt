package audio.effect

import audio.core.AudioSource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VolumeEffectTest {

    private val source = ConstantSource(1.0)

    @Test
    fun `multiplies source sample by volume level`() {
        val effect = VolumeEffect(
            level = 0.5,
            source = source
        )

        assertEquals(
            0.5,
            effect.generateSample(0.0, 440.0),
            1e-10
        )
    }

    @Test
    fun `handles negative samples`() {
        val effect = VolumeEffect(
            level = 2.0,
            source = ConstantSource(-0.25)
        )

        assertEquals(
            -0.5,
            effect.generateSample(0.0, 440.0),
            1e-10
        )
    }
}