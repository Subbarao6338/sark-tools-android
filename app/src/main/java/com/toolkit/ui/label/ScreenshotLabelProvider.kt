package com.wstxda.toolkit.ui.label

import android.content.Context
import com.wstxda.toolkit.R

class ScreenshotLabelProvider(private val context: Context) {

    fun getLabel(): CharSequence {
        return context.getString(R.string.screenshot_tile)
    }

    fun getSubtitle(isPermissionGranted: Boolean): CharSequence {
        return if (isPermissionGranted) {
            context.getString(R.string.tile_on)
        } else {
            context.getString(R.string.tile_setup)
        }
    }
}