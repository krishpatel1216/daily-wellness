package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

enum class SoundscapeType(val id: String, val title: String) {
    HZ_432("432", "432Hz Om & Deep Zen Drone"),
    HZ_528("528", "528Hz Solfeggio (Transformation)"),
    BINAURAL("binaural", "Alpha Binaural Wave (Focus)"),
    PINK("pink", "Pink Noise (Gentle Waterfall)")
}

class SoundscapeEngine {

    private val sampleRate = 44100
    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    @Volatile
    var isPlaying: Boolean = false
        private set

    @Volatile
    var volume: Float = 0.25f

    @Volatile
    var currentType: SoundscapeType = SoundscapeType.HZ_432

    fun start(type: SoundscapeType = currentType) {
        stop()
        currentType = type
        isPlaying = true

        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(sampleRate / 4)

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack = track
        track.play()

        playbackJob = scope.launch {
            val chunkFrames = 2048
            val buffer = ShortArray(chunkFrames * 2) // Stereo (L, R)
            var phaseL = 0.0
            var phaseR = 0.0
            var subPhase = 0.0

            // Pink noise filter state
            var b0 = 0.0
            var b1 = 0.0
            var b2 = 0.0

            while (isActive && isPlaying) {
                val vol = volume.coerceIn(0f, 1f)

                when (currentType) {
                    SoundscapeType.HZ_432 -> {
                        val freqL = 432.0
                        val freqSub = 216.0
                        val stepL = (2.0 * Math.PI * freqL) / sampleRate
                        val stepSub = (2.0 * Math.PI * freqSub) / sampleRate

                        for (i in 0 until chunkFrames) {
                            val sample = (sin(phaseL) * 0.7 + sin(subPhase) * 0.3) * vol
                            val sampleShort = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                            buffer[i * 2] = sampleShort
                            buffer[i * 2 + 1] = sampleShort
                            phaseL += stepL
                            subPhase += stepSub
                        }
                    }

                    SoundscapeType.HZ_528 -> {
                        val freq = 528.0
                        val step = (2.0 * Math.PI * freq) / sampleRate
                        for (i in 0 until chunkFrames) {
                            val sample = sin(phaseL) * vol
                            val sampleShort = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                            buffer[i * 2] = sampleShort
                            buffer[i * 2 + 1] = sampleShort
                            phaseL += step
                        }
                    }

                    SoundscapeType.BINAURAL -> {
                        // Left: 200 Hz, Right: 210 Hz -> 10 Hz Alpha beat
                        val freqL = 200.0
                        val freqR = 210.0
                        val stepL = (2.0 * Math.PI * freqL) / sampleRate
                        val stepR = (2.0 * Math.PI * freqR) / sampleRate

                        for (i in 0 until chunkFrames) {
                            val sampleL = sin(phaseL) * vol
                            val sampleR = sin(phaseR) * vol
                            buffer[i * 2] = (sampleL * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                            buffer[i * 2 + 1] = (sampleR * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                            phaseL += stepL
                            phaseR += stepR
                        }
                    }

                    SoundscapeType.PINK -> {
                        // Paul Kellet's filtered pink noise approximation
                        for (i in 0 until chunkFrames) {
                            val white = (Random.nextDouble() * 2.0 - 1.0)
                            b0 = 0.99886 * b0 + white * 0.0555179
                            b1 = 0.99332 * b1 + white * 0.0750759
                            b2 = 0.96900 * b2 + white * 0.1538520
                            val pink = (b0 + b1 + b2 + white * 0.5362) * 0.12 * vol
                            val sampleShort = (pink * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                            buffer[i * 2] = sampleShort
                            buffer[i * 2 + 1] = sampleShort
                        }
                    }
                }

                // Prevent phase overflow
                if (phaseL > 100000.0) phaseL %= (2.0 * Math.PI)
                if (phaseR > 100000.0) phaseR %= (2.0 * Math.PI)
                if (subPhase > 100000.0) subPhase %= (2.0 * Math.PI)

                try {
                    track.write(buffer, 0, buffer.size)
                } catch (e: Exception) {
                    break
                }
            }
        }
    }

    fun stop() {
        isPlaying = false
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }

    fun playCompletionChime() {
        scope.launch {
            try {
                val chimeTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(sampleRate * 4)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                chimeTrack.play()
                val durationSec = 2.5
                val totalSamples = (sampleRate * durationSec).toInt()
                val chimeBuffer = ShortArray(totalSamples)
                val freq = 528.0
                val step = (2.0 * Math.PI * freq) / sampleRate

                for (i in 0 until totalSamples) {
                    val progress = i.toDouble() / totalSamples
                    val decay = Math.exp(-progress * 4.0) // smooth exponential decay
                    val sample = sin(i * step) * decay * 0.8
                    chimeBuffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                chimeTrack.write(chimeBuffer, 0, totalSamples)
                kotlinx.coroutines.delay(2600)
                chimeTrack.release()
            } catch (_: Exception) {}
        }
    }
}
