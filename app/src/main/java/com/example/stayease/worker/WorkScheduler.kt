package com.example.stayease.worker
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object WorkScheduler {
  private const val UNIQUE = "stayease_background"
  fun schedule(context: Context) {
    val req = PeriodicWorkRequestBuilder<BackgroundWorker>(6, TimeUnit.HOURS).build()
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(UNIQUE, ExistingPeriodicWorkPolicy.UPDATE, req)
  }
}
