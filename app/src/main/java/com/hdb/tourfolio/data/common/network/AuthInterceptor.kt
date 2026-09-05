package com.hdb.tourfolio.data.common.network

import com.hdb.tourfolio.data.auth.local.SessionLocalDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import javax.inject.Inject

class AuthInterceptor
    @Inject
    constructor(
        private val sessionLocalDataSource: SessionLocalDataSource,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()

            val requiresAuth =
                request
                    .tag(Invocation::class.java)
                    ?.method()
                    ?.isAnnotationPresent(Authenticated::class.java) == true

            if (!requiresAuth) return chain.proceed(request)

            val token = runBlocking { sessionLocalDataSource.getSession()?.token } ?: return chain.proceed(request)

            val authorizedRequest =
                request
                    .newBuilder()
                    .addHeader("Authorization", "TOKEN_$token")
                    .build()

            return chain.proceed(authorizedRequest)
        }
    }
