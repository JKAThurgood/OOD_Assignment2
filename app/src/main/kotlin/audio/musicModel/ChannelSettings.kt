package audio.musicModel

import audio.core.AudioSource

data class ChannelSettings(val source: AudioSource, val measures: List<Measure>)
