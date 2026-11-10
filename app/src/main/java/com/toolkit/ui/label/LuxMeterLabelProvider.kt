package com.wstxda.toolkit.ui.label

import android.content.Context
import com.wstxda.toolkit.R

class LuxMeterLabelProvider(private val context: Context) {

    fun getLabel(isActive: Boolean, lux: Int): CharSequence {
        return if (isActive) {
            context.getString(R.string.lux_meter_tile_lux, lux)
        } else {
            context.getString(R.string.lux_meter_tile)
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