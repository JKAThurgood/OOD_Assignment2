package audio.waveStrategy

import audio.core.WaveStrategy
import kotlin.math.PI
import kotlin.math.sin

class SinWave : WaveStrategy {
    override fun generateSample(time: Double, frequency: Double): Double {
        return sin(2.0 * PI * frequency * time)
    }
}
