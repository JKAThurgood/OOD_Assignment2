package audio.musicModel

import kotlin.math.pow

data class Note(
    val pitch: Pitch,
    val octave: Int,
    override val duration: Double
) : MusicalEvent {

    fun toFrequency(): Double {
        val noteNumber = octave * 12 + pitch.semitoneOffset
        val a4Number = 4 * 12 + 9

        val semitoneDifference = noteNumber - a4Number

        return 440.0 * 2.0.pow(semitoneDifference / 12.0)
    }
}