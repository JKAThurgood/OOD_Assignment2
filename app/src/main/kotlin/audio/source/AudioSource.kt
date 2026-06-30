package audio.source

interface AudioSource {
    fun generateSample(time: Double, frequency: Double): Double
}

interface WaveStrategy : AudioSource
