package com.wstxda.toolkit.manager.sos

import android.content.Context
import com.wstxda.toolkit.base.SingletonHolder

object SosModule {

    private val holder = SingletonHolder(::SosManager)

    fun getInstance(context: Context) = holder.getInstance(context)
}