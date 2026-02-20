package com.example.stayease.core.result

/**
 * Executes a block of code and wraps the result in an [AppResult].
 * Platform-specific implementations handle relevant exceptions (e.g., Retrofit on Android).
 */
expect suspend fun <T> safeCall(block: suspend () -> T): AppResult<T>
