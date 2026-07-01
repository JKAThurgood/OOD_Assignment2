package audio.waveStrategy

import audio.core.AudioSource
import kotlin.math.PI
import kotlin.math.sin

class SquareWave : AudioSource {
    override fun generateSample(time: Double, frequency: Double): Double {
        return if (sin(2.0 * PI * frequency * time) >= 0.0) 1.0 else -1.0
    }
}
