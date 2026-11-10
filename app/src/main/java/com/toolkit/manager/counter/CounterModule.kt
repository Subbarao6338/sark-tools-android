package com.wstxda.toolkit.manager.counter

import android.content.Context
import com.wstxda.toolkit.base.SingletonHolder

object CounterModule {

    private val holder = SingletonHolder(::CounterManager)

    fun getInstance(context: Context) = holder.getInstance(context)
}