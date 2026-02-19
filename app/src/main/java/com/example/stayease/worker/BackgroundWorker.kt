package com.example.stayease.worker
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.stayease.core.result.AppResult
import com.example.stayease.domain.model.GeoPoint
import com.example.stayease.domain.usecase.RefreshStaysUseCase
import com.example.stayease.domain.usecase.SyncBookingsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class BackgroundWorker @AssistedInject constructor(
  @Assisted appContext: Context,
  @Assisted params: WorkerParameters,
  private val refreshStays: RefreshStaysUseCase,
  private val syncBookings: SyncBookingsUseCase
) : CoroutineWorker(appContext, params) {
  override suspend fun doWork(): Result {
    val pivot = GeoPoint(41.8781, -87.6298)
    val r1 = refreshStays(pivot, 2500, 40)
    val r2 = syncBookings()
    return if (r1 is AppResult.Ok && r2 is AppResult.Ok) Result.success() else Result.retry()
  }
}
