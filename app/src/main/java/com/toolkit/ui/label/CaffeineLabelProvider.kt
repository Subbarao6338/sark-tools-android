package com.wstxda.toolkit.ui.label

import android.content.Context
import com.wstxda.toolkit.R
import com.wstxda.toolkit.manager.caffeine.CaffeineState
import java.util.concurrent.TimeUnit

class CaffeineLabelProvider(private val context: Context) {

    fun getLabel(state: CaffeineState, hasPermission: Boolean): CharSequence {
        if (!hasPermission || state == CaffeineState.Off) {
            return context.getString(R.string.caffeine_tile)
        }

        return getFormattedTime(state)
    }

    fun getSubtitle(state: CaffeineState, hasPermission: Boolean): CharSequence {
        if (!hasPermission) {
            return context.getString(R.string.tile_setup)
        }

        if (state == CaffeineState.Off) {
            return context.getString(R.string.tile_off)
        }

        return context.getString(R.string.tile_on)
    }

    private fun getFormattedTime(state: CaffeineState): String {
        return when (state) {
            CaffeineState.Infinite -> context.getString(R.string.caffeine_tile_infinite)
            CaffeineState.Off -> ""
            else -> {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(state.timeout.toLong())
                context.getString(R.string.caffeine_tile_minutes, minutes)
            }
        }
    }
}