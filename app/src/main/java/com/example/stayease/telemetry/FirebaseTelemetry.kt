package com.example.stayease.telemetry
import android.os.Bundle
import com.example.stayease.core.telemetry.Telemetry
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject

class FirebaseTelemetry @Inject constructor(
  private val analytics: FirebaseAnalytics,
  private val crashlytics: FirebaseCrashlytics
) : Telemetry {
  override fun setUserId(id: String?) {
    analytics.setUserId(id)
    crashlytics.setUserId(id ?: "unknown")
  }

  override fun logEvent(name: String, params: Map<String, Any?>) {
    val b = Bundle()
    params.forEach { (k, v) ->
      when (v) {
        is String -> b.putString(k, v)
        is Int -> b.putInt(k, v)
        is Long -> b.putLong(k, v)
        is Double -> b.putDouble(k, v)
        is Boolean -> b.putBoolean(k, v)
        else -> if (v != null) b.putString(k, v.toString())
      }
    }
    analytics.logEvent(name, b)
  }

  override fun recordNonFatal(t: Throwable, extras: Map<String, String>) {
    extras.forEach { (k, v) -> crashlytics.setCustomKey(k, v) }
    crashlytics.recordException(t)
  }
}
