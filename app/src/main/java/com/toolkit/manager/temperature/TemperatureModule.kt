package com.wstxda.toolkit.manager.temperature

import android.content.Context
import com.wstxda.toolkit.base.SingletonHolder

object TemperatureModule {

    private val holder = SingletonHolder(::TemperatureManager)

    fun getInstance(context: Context) = holder.getInstance(context)
}