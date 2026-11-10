package com.wstxda.toolkit.ui.icon

import android.content.Context
import android.graphics.drawable.Icon
import com.wstxda.toolkit.R
import com.wstxda.toolkit.manager.soundmode.SoundMode

class SoundModeIconProvider(private val context: Context) {

    fun getIcon(currentMode: SoundMode, hasPermission: Boolean): Icon {
        if (!hasPermission) {
            return Icon.createWithResource(context, R.drawable.ic_sound)
        }

        val iconRes = when (currentMode) {
            SoundMode.NORMAL -> R.drawable.ic_sound_normal
            SoundMode.VIBRATE -> R.drawable.ic_sound_vibrate
            SoundMode.SILENT -> R.drawable.ic_sound_silent
        }

        return Icon.createWithResource(context, iconRes)
    }
}