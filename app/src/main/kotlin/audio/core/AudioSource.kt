package audio.core

interface AudioSource {
    fun generateSample(time: Double, frequency: Double): Double
}
