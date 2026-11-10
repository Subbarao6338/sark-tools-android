package com.wstxda.toolkit.ui.icon

import android.content.Context
import android.graphics.drawable.Icon
import com.wstxda.toolkit.R
import com.wstxda.toolkit.manager.caffeine.CaffeineState

class CaffeineIconProvider(private val context: Context) {

    fun getIcon(state: CaffeineState): Icon {
        val iconRes = when (state) {
            CaffeineState.Off -> R.drawable.ic_caffeine
            CaffeineState.FiveMinutes -> R.drawable.ic_caffeine_5
            CaffeineState.TenMinutes -> R.drawable.ic_caffeine_10
            CaffeineState.ThirtyMinutes -> R.drawable.ic_caffeine_30
            CaffeineState.OneHour -> R.drawable.ic_caffeine_60
            CaffeineState.Infinite -> R.drawable.ic_caffeine_infinite
        }
        return Icon.createWithResource(context, iconRes)
    }
}