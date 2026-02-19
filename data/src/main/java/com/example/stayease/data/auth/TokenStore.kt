package com.example.stayease.data.auth
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.stayease.domain.model.AuthTokens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth_tokens")

class TokenStore(private val context: Context) {
  private val accessKey = stringPreferencesKey("access")
  private val refreshKey = stringPreferencesKey("refresh")
  private val expKey = longPreferencesKey("exp")

  val tokens: Flow<AuthTokens?> = context.dataStore.data.map { prefs ->
    val a = prefs[accessKey]; val r = prefs[refreshKey]; val e = prefs[expKey]
    if (a != null && r != null && e != null) AuthTokens(a, r, e) else null
  }

  suspend fun save(t: AuthTokens) {
    context.dataStore.edit { p ->
      p[accessKey] = t.accessToken
      p[refreshKey] = t.refreshToken
      p[expKey] = t.expiresAtEpochMs
    }
  }
  suspend fun clear() { context.dataStore.edit { it.clear() } }
}
