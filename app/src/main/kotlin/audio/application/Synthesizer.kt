package audio.application

import audio.musicModel.AudioSettings

class Synthesizer {
    fun render(settings: AudioSettings): ShortArray {
        val secondsPerBeat = 60.0 / settings.tempo
        val samplesPerBeat = settings.sampleRate * secondsPerBeat
        val channelSamples =
            settings.channels.map { channel ->
                val samples = mutableListOf<Double>()
                channel.measures.forEach { measure ->
                    measure.notes.forEach { note ->
                        val noteSamples = (note.duration * samplesPerBeat).toInt()
                        for (i in 0 until noteSamples) {
                            val time = i.toDouble() / settings.sampleRate
                            samples += channel.source.generateSample(time, note.toFrequency())
                        }
                    }
                }
                samples.toDoubleArray()
            }

        val maxLength = channelSamples.maxOfOrNull { it.size } ?: 0
        val mixed = ShortArray(maxLength)

        for (i in 0 until maxLength) {
            var sum = 0.0
            settings.channels.indices.forEach { channelIndex ->
                val samples = channelSamples[channelIndex]
                if (i < samples.size) sum += samples[i]
            }
            val normalized = (sum / settings.channels.size).coerceIn(-1.0, 1.0)
            mixed[i] = (normalized * Short.MAX_VALUE).toInt().toShort()
        }

        return mixed
    }
}
