package com.example.stayease.core.telemetry
interface Telemetry {
  fun setUserId(id: String?)
  fun logEvent(name: String, params: Map<String, Any?> = emptyMap())
  fun recordNonFatal(t: Throwable, extras: Map<String, String> = emptyMap())
}
