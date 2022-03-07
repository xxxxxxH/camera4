package com.sweetcam.app

import android.content.Context
import androidx.multidex.MultiDexApplication


class App : MultiDexApplication() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Ktx.initialize(this)
    }

    override fun onCreate() {
        super.onCreate()
        Ktx.getInstance().initStartUp()
    }

}