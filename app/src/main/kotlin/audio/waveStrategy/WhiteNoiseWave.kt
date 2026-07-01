package audio.waveStrategy

import audio.core.WaveStrategy
import kotlin.random.Random

class WhiteNoiseWave : WaveStrategy {
    override fun generateSample(time: Double, frequency: Double): Double {
        return Random.nextDouble(-1.0, 1.0)
    }
}
