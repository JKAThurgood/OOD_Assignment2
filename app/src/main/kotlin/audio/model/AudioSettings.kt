package audio.model

data class AudioSettings(
        val channels: List<ChannelSettings>,
        val sampleRate: Int,
        val beatsPerMeasure: Int,
        val tempo: Int
)
