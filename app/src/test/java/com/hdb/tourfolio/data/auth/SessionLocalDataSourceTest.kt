package com.hdb.tourfolio.data.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.google.gson.Gson
import com.hdb.tourfolio.data.auth.local.SessionLocalDataSource
import com.hdb.tourfolio.data.auth.remote.dto.AuthResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SessionLocalDataSourceTest {
    @Test
    fun sessionReadsReflectLoginAccountSwitchAndLogoutImmediately() =
        runBlocking {
            val session = SessionLocalDataSource(MemoryDataStore(), Gson())
            assertNull(session.getSession())

            session.setSession(AuthResponseDto(1, "one@example.com", "one", "first-token", "2026-09-05"))
            assertEquals("first-token", session.getSession()?.token)
            assertEquals(1L, session.getUserId())

            session.setSession(AuthResponseDto(2, "two@example.com", "two", "second-token", "2026-09-05"))
            assertEquals("second-token", session.getSession()?.token)
            assertEquals(2L, session.getUserId())

            session.clear()
            assertNull(session.getSession())
            assertNull(session.getUserId())
        }
}

private class MemoryDataStore : DataStore<Preferences> {
    override val data = MutableStateFlow(emptyPreferences())

    override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences {
        val updated = transform(data.value)
        data.value = updated
        return updated
    }
}
