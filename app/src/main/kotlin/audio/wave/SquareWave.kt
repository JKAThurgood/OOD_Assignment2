package audio.wave

import audio.source.WaveStrategy
import kotlin.math.PI
import kotlin.math.sin

class SquareWave : WaveStrategy {
    override fun generateSample(time: Double, frequency: Double): Double {
        return if (sin(2.0 * PI * frequency * time) >= 0.0) 1.0 else -1.0
    }
}
