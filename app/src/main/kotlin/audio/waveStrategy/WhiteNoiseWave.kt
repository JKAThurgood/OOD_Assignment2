package audio.waveStrategy

import audio.core.AudioSource
import kotlin.random.Random

class WhiteNoiseWave : AudioSource {
    override fun generateSample(time: Double, frequency: Double): Double {
        return Random.nextDouble(-1.0, 1.0)
    }
}
