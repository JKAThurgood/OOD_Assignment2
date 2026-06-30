package audio.effect

import audio.source.AudioSource

abstract class EffectDecorator(protected val source: AudioSource) : AudioSource
