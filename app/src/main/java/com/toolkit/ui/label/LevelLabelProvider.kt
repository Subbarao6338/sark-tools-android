package com.wstxda.toolkit.ui.label

import android.content.Context
import com.wstxda.toolkit.R

class LevelLabelProvider(private val context: Context) {

    fun getLabel(isActive: Boolean, degrees: Int): CharSequence {
        return if (isActive) {
            if (degrees == 0) {
                context.getString(R.string.level_tile_zero)
            } else {
                context.getString(R.string.level_tile_degrees, degrees)
            }
        } else {
            context.getString(R.string.level_tile)
        }
    }

    fun getSubtitle(isActive: Boolean): CharSequence {
        return if (isActive) {
            context.getString(R.string.tile_on)
        } else {
            context.getString(R.string.tile_off)
        }
    }
}