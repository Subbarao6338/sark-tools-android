package com.wstxda.toolkit.manager.networktraffic

import android.content.Context
import com.wstxda.toolkit.base.SingletonHolder

object NetworkTrafficModule {

    private val holder = SingletonHolder(::NetworkTrafficManager)

    fun getInstance(context: Context) = holder.getInstance(context)
}