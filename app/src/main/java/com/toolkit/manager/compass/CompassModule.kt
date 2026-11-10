package com.wstxda.toolkit.manager.compass

import android.content.Context
import com.wstxda.toolkit.base.SingletonHolder

object CompassModule {

    private val holder = SingletonHolder(::CompassManager)

    fun getInstance(context: Context) = holder.getInstance(context)
}