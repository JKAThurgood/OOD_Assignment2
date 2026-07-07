package audio.application

import audio.musicModel.AudioSettings
import audio.musicModel.ChannelSettings
import audio.musicModel.Note
import audio.musicModel.Rest
import kotlin.math.roundToInt

class Synthesizer {

    fun render(settings: AudioSettings): ShortArray {
        require(settings.sampleRate > 0) {
            "Sample rate must be greater than zero"
        }

        require(settings.tempo > 0) {
            "Tempo must be greater than zero"
        }

        require(settings.channels.isNotEmpty()) {
            "At least one channel is required"
        }

        val channelSamples = settings.channels.map { channel ->
            renderChannel(channel, settings)
        }

        return mixChannels(channelSamples)
    }

    private fun renderChannel(
        channel: ChannelSettings,
        settings: AudioSettings
    ): DoubleArray {

        val secondsPerBeat = 60.0 / settings.tempo
        val samplesPerBeat = settings.sampleRate * secondsPerBeat

        val samples = mutableListOf<Double>()

        var currentSample = 0.0

        channel.measures.forEach { measure ->
            measure.events.forEach { event ->

                val nextSample =
                    currentSample + event.duration * samplesPerBeat

                val startIndex =
                    currentSample.roundToInt()

                val endIndex =
                    nextSample.roundToInt()

                val eventSamples =
                    endIndex - startIndex

                when (event) {
                    is Note -> {
                        for (i in 0 until eventSamples) {
                            val time =
                                i.toDouble() / settings.sampleRate

                            samples.add(
                                channel.source.generateSample(
                                    time,
                                    event.toFrequency()
                                )
                            )
                        }
                    }

                    is Rest -> {
                        repeat(eventSamples) {
                            samples.add(0.0)
                        }
                    }
                }

                currentSample = nextSample
            }
        }

        return samples.toDoubleArray()
    }

    private fun mixChannels(
        channels: List<DoubleArray>
    ): ShortArray {

        val maxLength =
            channels.maxOfOrNull { it.size } ?: return ShortArray(0)

        val mixed = ShortArray(maxLength)

        for (i in 0 until maxLength) {
            var sum = 0.0

            channels.forEach { channel ->
                if (i < channel.size) {
                    sum += channel[i]
                }
            }

            val normalized =
                (sum / channels.size)
                    .coerceIn(-1.0, 1.0)

            mixed[i] =
                (normalized * Short.MAX_VALUE)
                    .toInt()
                    .toShort()
        }

        return mixed
    }
}