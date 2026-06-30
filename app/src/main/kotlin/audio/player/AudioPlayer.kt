package audio.player

import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine

class AudioPlayer {
    fun play(buffer: ShortArray, sampleRate: Int) {
        val format = AudioFormat(sampleRate.toFloat(), 16, 1, true, false)
        val info = DataLine.Info(SourceDataLine::class.java, format)
        val line = AudioSystem.getLine(info) as SourceDataLine
        line.open(format)
        line.start()

        val byteBuffer = ByteArray(buffer.size * 2)
        var index = 0
        for (sample in buffer) {
            byteBuffer[index++] = (sample.toInt() and 0xff).toByte()
            byteBuffer[index++] = ((sample.toInt() shr 8) and 0xff).toByte()
        }

        line.write(byteBuffer, 0, byteBuffer.size)
        line.drain()
        line.stop()
        line.close()
    }
}
