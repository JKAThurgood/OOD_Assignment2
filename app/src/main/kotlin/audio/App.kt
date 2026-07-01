package audio

import audio.application.*

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: <program> <input-file>")
        return
    }

    try {
        val parser = SongParser()
        val settings = parser.parse(args[0])
        val synthesizer = Synthesizer()
        val buffer = synthesizer.render(settings)
        val player = AudioPlayer()
        player.play(buffer, settings.sampleRate)
    } catch (ex: Exception) {
        println("Error: ${ex.message}")
    }
}
