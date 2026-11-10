package com.wstxda.toolkit.ui.label

import android.content.Context
import com.wstxda.toolkit.R

class MediaOutputLabelProvider(private val context: Context) {

    fun getLabel(): CharSequence {
        return context.getString(R.string.media_output_tile)
    }

    fun getSubtitle(): CharSequence {
        return context.getString(R.string.tile_open)
    }
}