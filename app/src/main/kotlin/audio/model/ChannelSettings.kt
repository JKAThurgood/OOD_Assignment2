package audio.model

import audio.source.AudioSource

data class ChannelSettings(val source: AudioSource, val measures: List<Measure>)
