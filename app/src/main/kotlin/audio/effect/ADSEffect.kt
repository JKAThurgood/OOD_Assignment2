package audio.effect

import audio.core.AudioSource

class ADSEffect(
    private val attackEnd: Double,
    private val decayEnd: Double,
    private val sustain: Double,
    source: AudioSource
) : EffectDecorator(source) {
    override fun generateSample(
        time: Double,
        frequency: Double
    ): Double {

        val raw = source.generateSample(time, frequency)

        val envelope =
            when {
                attackEnd > 0.0 && time <= attackEnd ->
                    time / attackEnd

                time <= decayEnd -> {
                    val decayLength =
                        (decayEnd - attackEnd).coerceAtLeast(0.0)

                    if (decayLength == 0.0) {
                        sustain
                    } else {
                        val fraction =
                            (time - attackEnd) / decayLength

                        1.0 - (1.0 - sustain) * fraction
                    }
                }

                else ->
                    sustain
            }

        return raw * envelope
    }
}
