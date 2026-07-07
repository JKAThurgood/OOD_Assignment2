package audio.application

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AudioPlayerTest {

    private val player = AudioPlayer()

    @Test
    fun `converts samples to little endian bytes`() {
        val samples = shortArrayOf(
            0,
            1,
            -1,
            32767,
            -32768
        )

        val bytes = player.toByteArray(samples)

        assertArrayEquals(
            byteArrayOf(
                0, 0,
                1, 0,
                -1, -1,
                -1, 127,
                0, -128
            ),
            bytes
        )
    }

    @Test
    fun `creates two bytes per sample`() {
        val samples = shortArrayOf(1, 2, 3)

        val bytes = player.toByteArray(samples)

        assertEquals(6, bytes.size)
    }

    @Test
    fun `handles empty buffer`() {
        val bytes = player.toByteArray(shortArrayOf())

        assertEquals(0, bytes.size)
    }
}