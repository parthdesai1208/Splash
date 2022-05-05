package com.example.otpviewer

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel

class InitViewModel(application:Application) : AndroidViewModel(application) {
    companion object {
        const val WORK_DURATION = 5000L  //assume that it takes 5 seconds for doing that operation
    }
    private val initTime = SystemClock.uptimeMillis()
    fun isDataReady() = SystemClock.uptimeMillis() - initTime > WORK_DURATION

}