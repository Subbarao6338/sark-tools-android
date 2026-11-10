package com.wstxda.toolkit.manager.battery

import android.content.Context
import com.wstxda.toolkit.base.SingletonHolder

object BatteryModule {

    private val holder = SingletonHolder(::BatteryManager)

    fun getInstance(context: Context) = holder.getInstance(context)
}