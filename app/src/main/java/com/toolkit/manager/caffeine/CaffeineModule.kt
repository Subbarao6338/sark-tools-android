package com.wstxda.toolkit.manager.caffeine

import android.content.Context
import com.wstxda.toolkit.base.SingletonHolder

object CaffeineModule {

    private val holder = SingletonHolder(::CaffeineManager)

    fun getInstance(context: Context) = holder.getInstance(context)
}