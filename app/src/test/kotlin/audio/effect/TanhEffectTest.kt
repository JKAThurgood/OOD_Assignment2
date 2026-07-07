package audio.effect

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.tanh

class TanhEffectTest {

    @Test
    fun `applies tanh distortion`() {
        val drive = 2.0

        val effect = TanhEffect(
            drive = drive,
            source = ConstantSource(0.5)
        )

        assertEquals(
            tanh(1.0),
            effect.generateSample(0.0, 440.0),
            1e-10
        )
    }

    @Test
    fun `zero input stays zero`() {
        val effect = TanhEffect(
            drive = 10.0,
            source = ConstantSource(0.0)
        )

        assertEquals(
            0.0,
            effect.generateSample(0.0, 440.0),
            1e-10
        )
    }
}