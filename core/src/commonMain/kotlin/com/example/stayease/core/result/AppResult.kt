package com.example.stayease.core.result
sealed class AppResult<out T> {
  data class Ok<T>(val value: T) : AppResult<T>()
  data class Err(val error: AppError) : AppResult<Nothing>()
  inline fun <R> map(transform: (T) -> R): AppResult<R> = when (this) {
    is Ok -> Ok(transform(value))
    is Err -> this
  }
}
