package com.wstxda.toolkit.ui.label

import android.content.Context
import com.wstxda.toolkit.R

class LockLabelProvider(private val context: Context) {

    fun getLabel(): CharSequence {
        return context.getString(R.string.lock_tile)
    }

    fun getSubtitle(isPermissionGranted: Boolean): CharSequence {
        return if (isPermissionGranted) {
            context.getString(R.string.tile_on)
        } else {
            context.getString(R.string.tile_setup)
        }
    }
}