package com.wstxda.toolkit.manager.luxmeter

import android.content.Context
import com.wstxda.toolkit.base.SingletonHolder

object LuxMeterModule {

    private val holder = SingletonHolder(::LuxMeterManager)

    fun getInstance(context: Context) = holder.getInstance(context)
}