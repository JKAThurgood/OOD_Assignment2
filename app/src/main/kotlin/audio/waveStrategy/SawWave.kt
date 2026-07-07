package audio.waveStrategy

import audio.core.AudioSource

class SawWave : AudioSource {
    override fun generateSample(time: Double, frequency: Double): Double {
        if (frequency <= 0.0) {
            return 0.0
        }

        val period = 1.0 / frequency
        val phase = (time % period) / period
        return 2.0 * phase - 1.0
    }
}