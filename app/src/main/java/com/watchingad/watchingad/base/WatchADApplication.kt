package com.watchingad.watchingad.base

import android.app.Application
import android.content.Context

class WatchADApplication : Application() {

    init{
        instance = this
    }

    companion object {
        lateinit var instance: WatchADApplication
        fun ApplicationContext() : Context {
            return instance.applicationContext
        }
    }

}