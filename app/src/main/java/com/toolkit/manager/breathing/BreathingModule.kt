package com.wstxda.toolkit.manager.breathing

import android.content.Context
import com.wstxda.toolkit.base.SingletonHolder

object BreathingModule {

    private val holder = SingletonHolder(::BreathingManager)

    fun getInstance(context: Context) = holder.getInstance(context)
}