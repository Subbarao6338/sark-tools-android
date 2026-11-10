package com.wstxda.toolkit.base

import android.content.Context

class SingletonHolder<T>(private val creator: (Context) -> T) {

    @Volatile
    private var instance: T? = null

    fun getInstance(context: Context): T {
        return instance ?: synchronized(this) {
            instance ?: creator(context.applicationContext).also { instance = it }
        }
    }
}