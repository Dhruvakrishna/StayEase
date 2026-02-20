package com.example.stayease.data.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.stayease.domain.model.AuthTokens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenStore @Inject constructor(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPrefs = EncryptedSharedPreferences.create(
        context,
        "secure_auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _tokens = MutableStateFlow<AuthTokens?>(null)
    val tokens: Flow<AuthTokens?> = _tokens.asStateFlow()

    init {
        loadTokens()
    }

    private fun loadTokens() {
        val access = sharedPrefs.getString("access_token", null)
        val refresh = sharedPrefs.getString("refresh_token", null)
        val expires = sharedPrefs.getLong("expires_at", -1L)

        if (access != null && refresh != null && expires != -1L) {
            _tokens.value = AuthTokens(access, refresh, expires)
        }
    }

    fun save(t: AuthTokens) {
        sharedPrefs.edit().apply {
            putString("access_token", t.accessToken)
            putString("refresh_token", t.refreshToken)
            putLong("expires_at", t.expiresAtEpochMs)
            apply()
        }
        _tokens.value = t
    }

    fun clear() {
        sharedPrefs.edit().clear().apply()
        _tokens.value = null
    }
}
