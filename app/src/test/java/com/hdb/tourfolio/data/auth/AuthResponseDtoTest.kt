package com.hdb.tourfolio.data.auth

import com.google.gson.Gson
import com.hdb.tourfolio.data.auth.remote.dto.AuthResponseDto
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthResponseDtoTest {
    private val gson = Gson()

    @Test
    fun loginUserIdSurvivesSessionSerialization() {
        val response = gson.fromJson(
            """{"userId":42,"email":"test@example.com","nickname":"test","token":"token","createdAt":"2026-09-05"}""",
            AuthResponseDto::class.java,
        )

        assertEquals(42L, response.id)
        val restored = gson.fromJson(gson.toJson(response), AuthResponseDto::class.java)
        assertEquals(42L, restored.id)
    }

    @Test
    fun legacySessionIdCanStillBeRead() {
        val restored = gson.fromJson("""{"id":17}""", AuthResponseDto::class.java)

        assertEquals(17L, restored.id)
    }
}
