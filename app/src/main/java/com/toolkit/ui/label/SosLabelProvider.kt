package com.wstxda.toolkit.ui.label

import android.content.Context
import com.wstxda.toolkit.R

class SosLabelProvider(private val context: Context) {

    fun getLabel(): CharSequence {
        return context.getString(R.string.sos_tile)
    }

    fun getSubtitle(active: Boolean, available: Boolean): CharSequence {
        return when {
            !available -> context.getString(R.string.tile_unavailable)
            active -> context.getString(R.string.tile_on)
            else -> context.getString(R.string.tile_off)
        }
    }
}