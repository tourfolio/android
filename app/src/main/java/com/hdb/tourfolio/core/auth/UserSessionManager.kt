package com.hdb.tourfolio.core.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.hdb.tourfolio.core.network.dto.AuthResponseDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSessionManager
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
        private val gson: Gson,
    ) {
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        val currentUser: StateFlow<AuthResponseDto?> =
            dataStore.data
                .map { prefs -> prefs[USER_KEY]?.let { gson.fromJson(it, AuthResponseDto::class.java) } }
                .stateIn(scope, SharingStarted.Eagerly, null)

        suspend fun setUser(user: AuthResponseDto) {
            dataStore.edit { it[USER_KEY] = gson.toJson(user) }
        }

        suspend fun clear() {
            dataStore.edit { it.remove(USER_KEY) }
        }

        private companion object {
            val USER_KEY = stringPreferencesKey("auth_user_json")
        }
    }
