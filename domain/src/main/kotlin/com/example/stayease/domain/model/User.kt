package com.example.stayease.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String? = null,
    val totalBookings: Int = 0,
    val memberSince: Long = System.currentTimeMillis()
)
