package audio.effect

import audio.core.AudioSource
import kotlin.math.tanh

class TanhEffect(private val drive: Double, source: AudioSource) : EffectDecorator(source) {
    override fun generateSample(time: Double, frequency: Double): Double {
        return tanh(drive * source.generateSample(time, frequency))
    }
}
