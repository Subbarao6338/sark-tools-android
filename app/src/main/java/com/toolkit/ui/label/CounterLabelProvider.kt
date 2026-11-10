package com.wstxda.toolkit.ui.label

import android.content.Context
import com.wstxda.toolkit.R

class CounterLabelProvider(private val context: Context) {

    fun getAddLabel(isActive: Boolean, count: Int): CharSequence {
        return if (isActive) count.toString() else context.getString(R.string.counter_tile_add)
    }

    fun getRemoveLabel(isActive: Boolean, count: Int): CharSequence {
        return if (isActive) count.toString() else context.getString(R.string.counter_tile_remove)
    }

    fun getResetLabel(): CharSequence {
        return context.getString(R.string.counter_tile_reset)
    }

    fun getAddSubtitle(isActive: Boolean): CharSequence {
        return if (isActive) {
            context.getString(R.string.counter_tile_add)
        } else {
            context.getString(R.string.counter_tile)
        }
    }

    fun getRemoveSubtitle(isActive: Boolean): CharSequence {
        return if (isActive) {
            context.getString(R.string.counter_tile_remove)
        } else {
            context.getString(R.string.counter_tile)
        }
    }

    fun getResetSubtitle(): CharSequence {
        return context.getString(R.string.counter_tile)
    }
}