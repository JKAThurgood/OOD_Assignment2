package audio.parser

import audio.effect.ADSEffect
import audio.effect.ClipEffect
import audio.effect.TanhEffect
import audio.effect.VolumeEffect
import audio.model.AudioSettings
import audio.model.ChannelSettings
import audio.model.Measure
import audio.model.Note
import audio.model.Pitch
import audio.source.AudioSource
import audio.source.WaveStrategy
import audio.wave.SawWave
import audio.wave.SinWave
import audio.wave.SquareWave
import audio.wave.WhiteNoiseWave
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

        val channels = lines.drop(1).mapIndexed { index, line -> parseChannel(line, index + 2) }

        if (channels.isEmpty()) {
            throw IllegalArgumentException("Input file must contain at least one channel")
        }

        return AudioSettings(channels, sampleRate, beatsPerMeasure, tempo)
    }

    private fun parseChannel(line: String, lineNumber: Int): ChannelSettings {
        val segments = line.split('|').map { it.trim() }
        if (segments.isEmpty()) {
            throw IllegalArgumentException("Channel line $lineNumber is malformed")
        }

        val settingsTokens = segments[0].split(' ').filter { it.isNotBlank() }
        if (settingsTokens.isEmpty()) {
            throw IllegalArgumentException("Channel line $lineNumber has no waveform")
        }

        val waveform = parseWaveform(settingsTokens[0], lineNumber)
        val effects = settingsTokens.drop(1).map { parseEffect(it, lineNumber) }
        val source =
            effects.fold(waveform as AudioSource) { accumulated, effect -> effect(accumulated) }

        val measures =
            if (segments.size == 1) {
                emptyList()
            } else {
                segments.drop(1).mapIndexed { measureIndex, measureText ->
                    parseMeasure(measureText, lineNumber, measureIndex + 1)
                }
            }

        return ChannelSettings(source, measures)
    }

    private fun parseWaveform(token: String, lineNumber: Int): WaveStrategy {
        return when (token.lowercase()) {
            "sine" -> SinWave()
            "square" -> SquareWave()
            "saw" -> SawWave()
            "noise" -> WhiteNoiseWave()
            else -> throw IllegalArgumentException("Unknown waveform '$token' on line $lineNumber")
        }
    }

    private fun parseEffect(token: String, lineNumber: Int): (AudioSource) -> AudioSource {
        val parts = token.split('$')
        return when (parts[0].lowercase()) {
            "volume" -> {
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

        val notes =
            tokens.chunked(2).mapIndexed { index, pair ->
                val noteToken = pair[0]
                val durationToken = pair[1]
                val note = parseNote(noteToken, lineNumber, measureNumber, index + 1)
                val duration =
                    durationToken.toDoubleOrNull()
                        ?: throw IllegalArgumentException(
                            "Duration '$durationToken' is not a number in measure $measureNumber on line $lineNumber"
                        )
                note.copy(duration = duration)
            }

        return Measure(notes)
    }

    private fun parseNote(
        token: String,
        lineNumber: Int,
        measureNumber: Int,
        pairIndex: Int
    ): Note {
        val regex = Regex("^([A-G](?:#|b)?)(\\d+)")
        val match = regex.find(token) ?: throw IllegalArgumentException(
            "Invalid note '$token' in measure $measureNumber on line $lineNumber"
        )

        val pitchToken = match.groupValues[1]
        val octave = match.groupValues[2].toInt()
        val pitch =
            when (pitchToken) {
                "A" -> Pitch.A
                "A#" -> Pitch.ASharp
                "Ab" -> Pitch.AFlat
                "B" -> Pitch.B
                "Bb" -> Pitch.BFlat
                "C" -> Pitch.C
                "C#" -> Pitch.CSharp
                "D" -> Pitch.D
                "D#" -> Pitch.DSharp
                "Db" -> Pitch.DFlat
                "E" -> Pitch.E
                "Eb" -> Pitch.EFlat
                "F" -> Pitch.F
                "F#" -> Pitch.FSharp
                "G" -> Pitch.G
                "G#" -> Pitch.GSharp
                "Gb" -> Pitch.GFlat
                else ->
                    throw IllegalArgumentException(
                        "Unknown pitch '$pitchToken' in measure $measureNumber on line $lineNumber"
                    )
            }

        return Note(pitch, octave, 0.0)
    }
}
