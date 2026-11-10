package com.wstxda.toolkit.ui.label

import android.content.Context
import com.wstxda.toolkit.R
import com.wstxda.toolkit.manager.soundmode.SoundMode

class SoundModeLabelProvider(private val context: Context) {

    fun getLabel(currentMode: SoundMode, hasPermission: Boolean): CharSequence {
        return if (hasPermission) {
            when (currentMode) {
                SoundMode.NORMAL -> context.getString(R.string.sound_tile_normal)
                SoundMode.VIBRATE -> context.getString(R.string.sound_tile_vibrate)
                SoundMode.SILENT -> context.getString(R.string.sound_tile_silent)
            }
        } else {
            context.getString(R.string.sound_mode_tile)
        }
    }

    fun getSubtitle(hasPermission: Boolean): CharSequence {
        return if (hasPermission) {
            context.getString(R.string.tile_switch)
        } else {
            context.getString(R.string.tile_setup)
        }
    }
}