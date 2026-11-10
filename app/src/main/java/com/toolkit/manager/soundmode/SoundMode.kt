package com.wstxda.toolkit.manager.soundmode

import android.media.AudioManager

enum class SoundMode(val ringerMode: Int) {
    NORMAL(AudioManager.RINGER_MODE_NORMAL), VIBRATE(AudioManager.RINGER_MODE_VIBRATE), SILENT(
        AudioManager.RINGER_MODE_SILENT
    ),
}