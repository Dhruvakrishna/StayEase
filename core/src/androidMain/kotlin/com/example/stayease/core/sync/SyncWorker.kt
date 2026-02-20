package com.example.stayease.core.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.stayease.core.domain.repository.TripRepository
import javax.inject.Inject

class SyncWorker @Inject constructor(
    context: Context,
    workerParams: WorkerParameters,
    private val repository: TripRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            repository.syncPendingChanges()
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}
