package com.wstxda.toolkit.manager.level

import android.content.Context
import com.wstxda.toolkit.base.SingletonHolder

object LevelModule {

    private val holder = SingletonHolder(::LevelManager)

    fun getInstance(context: Context) = holder.getInstance(context)
}