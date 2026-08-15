package com.hdb.tourfolio.core.network

import com.hdb.tourfolio.core.auth.UserSessionManager
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import javax.inject.Inject

class AuthInterceptor
    @Inject
    constructor(
        private val userSessionManager: UserSessionManager,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()

            val requiresAuth =
                request
                    .tag(Invocation::class.java)
                    ?.method()
                    ?.isAnnotationPresent(Authenticated::class.java) == true

            if (!requiresAuth) return chain.proceed(request)

            val token = userSessionManager.currentUser.value?.token ?: return chain.proceed(request)

            val authorizedRequest =
                request
                    .newBuilder()
                    .addHeader("Authorization", "TOKEN_$token")
                    .build()

            return chain.proceed(authorizedRequest)
        }
    }
