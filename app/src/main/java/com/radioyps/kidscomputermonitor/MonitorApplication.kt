package com.radioyps.kidscomputermonitor



import android.app.Application
import android.os.Build
import android.util.Log
import androidx.multidex.MultiDexApplication
import androidx.work.*

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStream

import java.util.concurrent.TimeUnit

/**
 * Override application to setup background work via WorkManager
 */
private const val TAG = "MonitorApplication"
class MonitorApplication : MultiDexApplication() {

    /**
     * onCreate is called before the first screen is shown to the user.
     *
     * Use it to setup any background tasks, running expensive setup operations in a background
     * thread to avoid delaying app start.
     */
    companion object{
        var ngrokApiKey = ""
    }

    val applicationScope = CoroutineScope(Dispatchers.Default)
    private fun delayedInit() = applicationScope.launch {
        getApiKey()
    }
    override fun onCreate() {
        super.onCreate()
        delayedInit()
    }
    private fun getApiKey(){
        var res = ""
        val input: InputStream = this.getResources().openRawResource(R.raw.ngrok_key)

        val reader = BufferedReader(input.reader())

        try {
            res = reader.readText()

            Log.v(TAG, "getApiKey(): ${res}")

        } finally {
            reader.close()

        }
         ngrokApiKey = res
    }


}