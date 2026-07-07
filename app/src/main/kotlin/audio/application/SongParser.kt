package audio.application

import audio.effect.ADSEffect
import audio.effect.ClipEffect
import audio.effect.TanhEffect
import audio.effect.VolumeEffect
import audio.musicModel.*
import audio.core.AudioSource
import audio.waveStrategy.*
import java.io.File

class SongParser {
    fun parse(filename: String): AudioSettings {
        val file = File(filename)
        if (!file.exists()) {
            throw IllegalArgumentException("Input file not found: $filename")
        }

        val lines = file.readLines().map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.isEmpty()) {
            throw IllegalArgumentException("Input file is empty")
        }

        val headerParts = lines[0].split(' ').filter { it.isNotBlank() }
        if (headerParts.size != 3) {
            throw IllegalArgumentException(
                "Header must contain sampleRate, beatsPerMeasure, and tempo"
            )
        }

        val (sampleRate, beatsPerMeasure, tempo) =
            headerParts.map {
                it.toIntOrNull()
                    ?: throw IllegalArgumentException(
                        "Header values must be integers: '$it'"
                    )
            }
        // index + 2 because we dropped the header and base 0 addition
        val channels = lines.drop(1).mapIndexed { index, line -> parseChannel(line, index + 2) }

        if (channels.isEmpty()) {
            throw IllegalArgumentException("Input file must contain at least one channel")
        }

        return AudioSettings(channels, sampleRate, beatsPerMeasure, tempo)
    }

    private fun parseChannel(line: String, lineNumber: Int): ChannelSettings {
        val segments = line.split('|').map { it.trim() }
        if (segments.size < 2) {
            throw IllegalArgumentException("Channel line $lineNumber is malformed")
        }

        val settingsTokens = segments[0].split(' ').filter { it.isNotBlank() }
        if (settingsTokens.isEmpty()) {
            throw IllegalArgumentException("Channel line $lineNumber has no waveform")
        }

        val waveform = parseWaveform(settingsTokens[0], lineNumber)
        val effects = settingsTokens.drop(1).map { parseEffect(it, lineNumber) }
        val baseSource: AudioSource = waveform

        var currentSource = baseSource

        for (effect in effects) {
            currentSource = effect(currentSource)
        }

        val source = currentSource
        // Done with settings
        val measureTexts = segments.drop(1)

        val measures = measureTexts.mapIndexed { index, text ->
            parseMeasure(text, lineNumber, index + 1)
        }

        return ChannelSettings(source, measures)
    }

    private fun parseWaveform(token: String, lineNumber: Int): AudioSource {
        return when (token.lowercase()) {
            "sin" -> SinWave()
            "square" -> SquareWave()
            "saw" -> SawWave()
            "whitenoise" -> WhiteNoiseWave()
            else -> throw IllegalArgumentException("Unknown waveform '$token' on line $lineNumber")
        }
    }

    private fun parseEffect(token: String, lineNumber: Int): (AudioSource) -> AudioSource {
        val parts = token.split('$')
        return when (parts[0].lowercase()) {
            "vol" -> {
                if (parts.size != 2)
                    throw IllegalArgumentException(
                        "Volume effect must include one argument on line $lineNumber"
                    )
                val level =
                    parts[1].toDoubleOrNull()
                        ?: throw IllegalArgumentException(
                            "Volume level must be a number on line $lineNumber"
                        )
                ({ source: AudioSource -> VolumeEffect(level, source) })
            }

            "ads" -> {
                if (parts.size != 4)
                    throw IllegalArgumentException(
                        "ADS effect must include attackEnd, decayEnd, sustain on line $lineNumber"
                    )
                val attackEnd =
                    parts[1].toDoubleOrNull()
                        ?: throw IllegalArgumentException(
                            "ADS attackEnd must be a number on line $lineNumber"
                        )
                val decayEnd =
                    parts[2].toDoubleOrNull()
                        ?: throw IllegalArgumentException(
                            "ADS decayEnd must be a number on line $lineNumber"
                        )
                val sustain =
                    parts[3].toDoubleOrNull()
                        ?: throw IllegalArgumentException(
                            "ADS sustain must be a number on line $lineNumber"
                        )
                return { source: AudioSource -> ADSEffect(attackEnd, decayEnd, sustain, source) }
            }

            "tanh" -> {
                if (parts.size != 2)
                    throw IllegalArgumentException(
                        "Tanh effect must include one argument on line $lineNumber"
                    )
                val drive =
                    parts[1].toDoubleOrNull()
                        ?: throw IllegalArgumentException(
                            "Tanh drive must be a number on line $lineNumber"
                        )
                return { source: AudioSource -> TanhEffect(drive, source) }
            }

            "clip" -> {
                if (parts.size != 2)
                    throw IllegalArgumentException(
                        "Clip effect must include one argument on line $lineNumber"
                    )
                val threshold =
                    parts[1].toDoubleOrNull()
                        ?: throw IllegalArgumentException(
                            "Clip threshold must be a number on line $lineNumber"
                        )
                return { source: AudioSource -> ClipEffect(threshold, source) }
            }

            else ->
                throw IllegalArgumentException(
                    "Unknown effect '${parts[0]}' on line $lineNumber"
                )
        }
    }

    private fun parseMeasure(text: String, lineNumber: Int, measureNumber: Int): Measure {
        val tokens = text.split(' ').filter { it.isNotBlank() }
        if (tokens.size % 2 != 0) {
            throw IllegalArgumentException(
                "Measure $measureNumber on line $lineNumber must contain note/duration pairs"
            )
        }

        val notes = tokens.chunked(2).mapIndexed { index, pair ->
            parseNote(
                noteToken = pair[0],
                durationToken = pair[1],
                lineNumber = lineNumber,
                measureNumber = measureNumber,
                pairIndex = index + 1
            )
        }

        return Measure(notes)
    }

    private fun parseNote(
        noteToken: String,
        durationToken: String,
        lineNumber: Int,
        measureNumber: Int,
        pairIndex: Int
    ): MusicalEvent {
        val duration = durationToken.toDoubleOrNull()
            ?: throw IllegalArgumentException(
                "Duration '$durationToken' is not a number in measure $measureNumber on line $lineNumber"
            )

        // Handle rests
        if (noteToken == "-") {
            return Rest(
                duration
            )
        }

        val regex = Regex("^([A-G](?:#|b)?)(\\d+)$")
        val match = regex.find(noteToken)
            ?: throw IllegalArgumentException(
                "Invalid note '$noteToken' in measure $measureNumber on line $lineNumber"
            )

        val pitchToken = match.groupValues[1]
        val octave = match.groupValues[2].toInt()

        val pitch = Pitch.fromString(pitchToken)

        return Note(pitch, octave, duration)
    }
}
