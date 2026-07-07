package audio.effect

import audio.core.AudioSource

class ConstantSource(
    private val value: Double
) : AudioSource {

    override fun generateSample(time: Double, frequency: Double): Double {
        return value
    }
}