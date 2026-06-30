package audio.effect

import audio.source.AudioSource

class VolumeEffect(private val level: Double, source: AudioSource) : EffectDecorator(source) {
    override fun generateSample(time: Double, frequency: Double): Double {
        return source.generateSample(time, frequency) * level
    }
}
