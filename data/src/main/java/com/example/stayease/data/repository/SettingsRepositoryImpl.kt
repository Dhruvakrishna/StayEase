package com.example.stayease.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.stayease.domain.repository.AppTheme
import com.example.stayease.domain.repository.SettingsRepository
import com.example.stayease.domain.repository.UserSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private val THEME_KEY = stringPreferencesKey("theme")
    private val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")
    private val ONBOARDING_KEY = booleanPreferencesKey("onboarding_completed")

    override fun observeSettings(): Flow<UserSettings> = context.dataStore.data.map { preferences ->
        UserSettings(
            theme = AppTheme.valueOf(preferences[THEME_KEY] ?: AppTheme.SYSTEM.name),
            notificationsEnabled = preferences[NOTIFICATIONS_KEY] ?: true,
            hasCompletedOnboarding = preferences[ONBOARDING_KEY] ?: false
        )
    }

    override suspend fun setTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_KEY] = enabled
        }
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_KEY] = completed
        }
    }
}
