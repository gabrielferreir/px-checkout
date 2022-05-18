package com.mercadopago.android.px.internal.audio

import android.content.Context
import android.media.MediaPlayer
import com.mercadopago.android.px.R

internal class AudioPlayer(context: Context) {

    val context: Context = context.applicationContext

    fun play(sound: Sound) {
        MediaPlayer.create(context, sound.id).start()
    }

    enum class Sound(val id: Int = 0) {
        SUCCESS(R.raw.congrats_success),
        FAILURE(R.raw.congrats_failure),
        NONE
    }
}
