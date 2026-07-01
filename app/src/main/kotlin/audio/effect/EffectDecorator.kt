package audio.effect

import audio.core.AudioSource

abstract class EffectDecorator(protected val source: AudioSource) : AudioSource
