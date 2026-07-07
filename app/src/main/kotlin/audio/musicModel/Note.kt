package audio.musicModel

import kotlin.math.pow

data class Note(val pitch: Pitch, val octave: Int, val duration: Double) {
    fun toFrequency(): Double {
        if (pitch == Pitch.Rest) {
            return 0.0
        }

        val semitoneOffset =
            when (pitch) {
                Pitch.C -> 0
                Pitch.CSharp -> 1
                Pitch.CFlat -> -1
                Pitch.D -> 2
                Pitch.DSharp -> 3
                Pitch.DFlat -> 1
                Pitch.E -> 4
                Pitch.EFlat -> 3
                Pitch.F -> 5
                Pitch.FSharp -> 6
                Pitch.FFlat -> 4
                Pitch.G -> 7
                Pitch.GSharp -> 8
                Pitch.GFlat -> 6
                Pitch.A -> 9
                Pitch.ASharp -> 10
                Pitch.AFlat -> 8
                Pitch.B -> 11
                Pitch.BFlat -> 10
            }
        val noteNumber = octave * 12 + semitoneOffset
        val a4Number = 4 * 12 + 9
        val semitoneDifference = noteNumber - a4Number
        return 440.0 * 2.0.pow(semitoneDifference / 12.0)
    }
}
