package com.example.stayease.core.result

sealed class AppError {
    abstract val message: String
    
    data class Network(override val message: String) : AppError()
    data class Unauthorized(override val message: String) : AppError()
    data class NotFound(override val message: String) : AppError()
    data class Validation(override val message: String) : AppError()
    data class Server(override val message: String) : AppError()
    data class Unexpected(override val message: String) : AppError()
    
    // Feature: Enhanced Error Handling
    // Localized message logic would go here or be handled by a UI mapper.
    fun toUserMessage(): String = when(this) {
        is Network -> "Please check your internet connection and try again."
        is Unauthorized -> "Your session has expired. Please log in again."
        is NotFound -> "We couldn't find what you were looking for."
        is Server -> "Something went wrong on our end. We're working on it!"
        else -> message
    }
}
