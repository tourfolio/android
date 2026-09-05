package com.hdb.tourfolio.data.auth.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.hdb.tourfolio.data.auth.remote.dto.AuthResponseDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionLocalDataSource
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
        private val gson: Gson,
    ) {
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        val currentSession: StateFlow<AuthResponseDto?> =
            dataStore.data
                .map { prefs -> prefs[SESSION_KEY]?.let { gson.fromJson(it, AuthResponseDto::class.java) } }
                .stateIn(scope, SharingStarted.Eagerly, null)

        suspend fun getUserId(): Long? =
            dataStore.data.first()[SESSION_KEY]
                ?.let { gson.fromJson(it, AuthResponseDto::class.java).id }
                ?.takeIf { it > 0 }

        suspend fun setSession(session: AuthResponseDto) {
            dataStore.edit { it[SESSION_KEY] = gson.toJson(session) }
        }

        suspend fun clear() {
            dataStore.edit { it.remove(SESSION_KEY) }
        }

        private companion object {
            val SESSION_KEY = stringPreferencesKey("auth_user_json")
        }
    }
