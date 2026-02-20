package com.example.stayease.core.telemetry

class NoopTelemetry : Telemetry {
    override fun setUserId(id: String?) = Unit
    override fun logEvent(name: String, params: Map<String, Any?>) = Unit
    override fun recordNonFatal(t: Throwable, extras: Map<String, String>) = Unit
}
