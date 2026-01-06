package com.realtorsuccessiq.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "rsiq_prefs")

object PrefKeys {
    val TEXTS_COUNT_AS_CONVERSATIONS = booleanPreferencesKey("texts_count_as_conversations")
    val ANALYTICS_ENABLED = booleanPreferencesKey("analytics_enabled") // opt-out => default true
    val USER_LOGO_TEXT = stringPreferencesKey("user_logo_text")
}

class AppPrefs(private val context: Context) {
    val textsCountAsConversations: Flow<Boolean> =
        context.dataStore.data.map { it[PrefKeys.TEXTS_COUNT_AS_CONVERSATIONS] ?: false }

    val analyticsEnabled: Flow<Boolean> =
        context.dataStore.data.map { it[PrefKeys.ANALYTICS_ENABLED] ?: true }

    val userLogoText: Flow<String> =
        context.dataStore.data.map { it[PrefKeys.USER_LOGO_TEXT] ?: "" }

    suspend fun setTextsCountAsConversations(enabled: Boolean) {
        context.dataStore.edit { it[PrefKeys.TEXTS_COUNT_AS_CONVERSATIONS] = enabled }
    }

    suspend fun setAnalyticsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PrefKeys.ANALYTICS_ENABLED] = enabled }
    }

    suspend fun setUserLogoText(text: String) {
        context.dataStore.edit { it[PrefKeys.USER_LOGO_TEXT] = text }
    }
}


