package com.wstxda.toolkit.manager.memory

import android.content.Context
import com.wstxda.toolkit.base.SingletonHolder

object MemoryModule {

    private val holder = SingletonHolder(::MemoryManager)

    fun getInstance(context: Context) = holder.getInstance(context)
}