package audio.musicModel

enum class Pitch(
    val semitoneOffset: Int
) {
    C(0),
    CSharp(1),
    CFlat(-1),

    D(2),
    DSharp(3),
    DFlat(1),

    E(4),
    EFlat(3),

    F(5),
    FSharp(6),
    FFlat(4),

    G(7),
    GSharp(8),
    GFlat(6),

    A(9),
    ASharp(10),
    AFlat(8),

    B(11),
    BFlat(10);

    companion object {
        fun fromString(value: String): Pitch {
            return when (value) {
                "A" -> A
                "A#" -> ASharp
                "Ab" -> AFlat
                "B" -> B
                "Bb" -> BFlat
                "C" -> C
                "C#" -> CSharp
                "Cb" -> CFlat
                "D" -> D
                "D#" -> DSharp
                "Db" -> DFlat
                "E" -> E
                "Eb" -> EFlat
                "F" -> F
                "F#" -> FSharp
                "Fb" -> FFlat
                "G" -> G
                "G#" -> GSharp
                "Gb" -> GFlat
                else -> throw IllegalArgumentException(
                    "Unknown pitch '$value'"
                )
            }
        }
    }
}