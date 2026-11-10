package com.wstxda.toolkit.ui.label

import android.content.Context
import com.wstxda.toolkit.R
import kotlin.math.roundToInt

class CompassLabelProvider(private val context: Context) {

    fun getLabel(isActive: Boolean, degrees: Float): CharSequence {
        return if (isActive) {
            val direction = when (degrees) {
                in 0.0..22.5, in 337.5..360.0 -> context.getString(R.string.N)
                in 22.5..67.5 -> context.getString(R.string.NE)
                in 67.5..112.5 -> context.getString(R.string.E)
                in 112.5..157.5 -> context.getString(R.string.SE)
                in 157.5..202.5 -> context.getString(R.string.S)
                in 202.5..247.5 -> context.getString(R.string.SW)
                in 247.5..292.5 -> context.getString(R.string.W)
                in 292.5..337.5 -> context.getString(R.string.NW)
                else -> ""
            }
            val degreesRounded = degrees.roundToInt() % 360
            context.getString(R.string.compass_tile_degrees, degreesRounded, direction)
        } else {
            context.getString(R.string.compass_tile)
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