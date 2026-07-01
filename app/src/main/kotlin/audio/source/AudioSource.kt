package audio.source

interface AudioSource {
    fun generateSample(time: Double, frequency: Double): Double
}

// This creates a new interface (WaveStrategy) that inherits from AudioSource which is a chainable wrapping system.
interface WaveStrategy : AudioSource
