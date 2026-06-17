package com.example.tekakata.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class SoundManager(context: Context) {
    private val prefs = PreferencesManager(context)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val sampleRate = 44100

    fun playFound() = playSafe {
        playSequence(floatArrayOf(523.25f, 659.25f), intArrayOf(120, 150))
    }

    fun playComplete() = playSafe {
        playSequence(floatArrayOf(523.25f, 659.25f, 783.99f, 1046.50f), intArrayOf(150, 150, 150, 350))
    }

    fun playHint() = playSafe {
        playSequence(floatArrayOf(440f), intArrayOf(80))
    }

    fun playClick() = playSafe {
        playSequence(floatArrayOf(880f), intArrayOf(30))
    }

    fun playReset() = playSafe {
        playSequence(floatArrayOf(300f, 250f, 200f), intArrayOf(70, 70, 100))
    }

    private fun playSafe(action: () -> Unit) {
        if (!prefs.isSoundEnabled()) return
        scope.launch {
            try {
                action()
            } catch (_: Exception) {
                // game tetap jalan meskipun audio error
            }
        }
    }

    private fun playSequence(frequencies: FloatArray, durationsMs: IntArray) {
        val gapMs = 12
        val attackSamples = sampleRate * 5 / 1000
        val releaseSamples = sampleRate * 8 / 1000

        var totalSamples = 0
        for (i in frequencies.indices) {
            totalSamples += sampleRate * durationsMs[i] / 1000
            if (i < frequencies.size - 1) {
                totalSamples += sampleRate * gapMs / 1000
            }
        }

        val buffer = ShortArray(totalSamples)
        var offset = 0

        for (i in frequencies.indices) {
            val freq = frequencies[i]
            val durMs = durationsMs[i]
            val numSamples = sampleRate * durMs / 1000

            for (j in 0 until numSamples) {
                if (offset >= totalSamples) break
                val t = j.toFloat() / sampleRate

                val envelope = when {
                    j < attackSamples -> j.toFloat() / attackSamples
                    j >= numSamples - releaseSamples -> (numSamples - 1 - j).toFloat() / releaseSamples
                    else -> 1.0f
                }

                val sample = sin(2.0 * PI * freq * t)
                val value = (Short.MAX_VALUE * 0.5f * envelope * sample).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                buffer[offset] = value.toShort()
                offset++
            }

            if (i < frequencies.size - 1) {
                offset += sampleRate * gapMs / 1000
            }
        }

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
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
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(buffer, 0, buffer.size)
        track.play()

        val totalMs = totalSamples * 1000 / sampleRate
        Thread.sleep(totalMs.toLong() + 100)
        track.release()
    }
}
