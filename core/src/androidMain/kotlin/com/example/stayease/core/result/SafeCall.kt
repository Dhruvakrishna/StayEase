package com.example.stayease.core.result

import java.io.IOException
import retrofit2.HttpException

actual suspend fun <T> safeCall(block: suspend () -> T): AppResult<T> {
  return try {
    AppResult.Ok(block())
  } catch (e: HttpException) {
    when (e.code()) {
      401 -> AppResult.Err(AppError.Unauthorized("Unauthorized"))
      404 -> AppResult.Err(AppError.NotFound("Not found"))
      in 500..599 -> AppResult.Err(AppError.Server("Server error"))
      else -> AppResult.Err(AppError.Network("HTTP ${e.code()}"))
    }
  } catch (_: IOException) {
    AppResult.Err(AppError.Network("Network error"))
  } catch (t: Throwable) {
    AppResult.Err(AppError.Unexpected(t.message ?: "Unexpected error"))
  }
}
