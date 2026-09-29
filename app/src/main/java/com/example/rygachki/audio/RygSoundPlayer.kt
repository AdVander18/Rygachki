package com.example.rygachki.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.rygachki.R

class RygSoundPlayer(context: Context) {

    private val appContext = context.applicationContext

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(MAX_STREAMS)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private var soundId: Int = 0

    init {
        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status != 0) soundId = 0
        }
        runCatching {
            appContext.resources.openRawResourceFd(R.raw.ryg)?.use { descriptor ->
                soundId = soundPool.load(descriptor, 1)
            }
        }
    }

    fun play() {
        playSound(RATE)
    }

    fun playBack() {
        playSound(BACK_RATE)
    }

    private fun playSound(rate: Float) {
        if (soundId == 0) return
        soundPool.play(soundId, VOLUME, VOLUME, 1, 0, rate)
    }

    fun release() {
        soundId = 0
        soundPool.release()
    }

    private companion object {
        const val MAX_STREAMS = 4
        const val VOLUME = 1f
        const val RATE = 1f
        const val BACK_RATE = 0.7f
    }
}

@Composable
fun rememberRygSoundPlayer(): RygSoundPlayer {
    val context = LocalContext.current
    val player = remember { RygSoundPlayer(context) }
    DisposableEffect(player) {
        onDispose { player.release() }
    }
    return player
}
