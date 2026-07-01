package audio.waveStrategy

import audio.core.AudioSource
import kotlin.math.PI
import kotlin.math.sin

class SinWave : AudioSource {
    override fun generateSample(time: Double, frequency: Double): Double {
        return sin(2.0 * PI * frequency * time)
    }
}
