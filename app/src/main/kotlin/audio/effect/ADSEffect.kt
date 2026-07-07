package audio.effect

import audio.core.AudioSource

class ADSEffect(
    private val attackEnd: Double,
    private val decayEnd: Double,
    private val sustain: Double,
    source: AudioSource
) : EffectDecorator(source) {
    override fun generateSample(time: Double, frequency: Double): Double {
        val raw = source.generateSample(time, frequency)
        val envelope =
            when {
                attackEnd in time..0.0 -> 1.0

                time < attackEnd ->
                    time / attackEnd

                decayEnd <= 0.0 ->
                    sustain

                time <= attackEnd + decayEnd -> {
                    val decayTime = time - attackEnd
                    val decayFraction = decayTime / decayEnd
                    1.0 - (1.0 - sustain) * decayFraction
                }

                else -> sustain
            }
        return raw * envelope
    }
}
