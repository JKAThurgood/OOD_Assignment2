package audio.effect

import audio.source.AudioSource

class ClipEffect(private val threshold: Double, source: AudioSource) : EffectDecorator(source) {
    override fun generateSample(time: Double, frequency: Double): Double {
        val sample = source.generateSample(time, frequency)
        return when {
            sample > threshold -> threshold
            sample < -threshold -> -threshold
            else -> sample
        }
    }
}
