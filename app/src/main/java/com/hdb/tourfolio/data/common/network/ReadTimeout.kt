package com.hdb.tourfolio.data.common.network

import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import java.util.concurrent.TimeUnit

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ReadTimeout(
    val seconds: Int,
)

class ReadTimeoutInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val timeout =
            chain.request()
                .tag(Invocation::class.java)
                ?.method()
                ?.getAnnotation(ReadTimeout::class.java)
        val configuredChain = timeout?.let { chain.withReadTimeout(it.seconds, TimeUnit.SECONDS) } ?: chain
        return configuredChain.proceed(chain.request())
    }
}
