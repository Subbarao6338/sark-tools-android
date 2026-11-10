package com.wstxda.toolkit.ui.icon

import android.content.Context
import android.graphics.drawable.Icon
import com.wstxda.toolkit.R

class CounterIconProvider(private val context: Context) {

    fun getAddIcon(): Icon {
        return Icon.createWithResource(context, R.drawable.ic_counter_add)
    }

    fun getRemoveIcon(): Icon {
        return Icon.createWithResource(context, R.drawable.ic_counter_remove)
    }

    fun getResetIcon(): Icon {
        return Icon.createWithResource(context, R.drawable.ic_counter_reset)
    }
}