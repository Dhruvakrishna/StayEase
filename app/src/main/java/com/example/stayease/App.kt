package com.example.stayease
import android.app.Application
import com.example.stayease.worker.WorkScheduler
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
  override fun onCreate() {
    super.onCreate()
    WorkScheduler.schedule(this)
  }
}
