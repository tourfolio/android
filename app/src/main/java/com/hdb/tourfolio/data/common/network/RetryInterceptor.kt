package com.hdb.tourfolio.data.common.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.ConnectException
import java.net.UnknownHostException

/*
 * 앱을 막 실행한 직후에는 기기 네트워크/DNS가 아직 준비되지 않아
 * 첫 요청이 UnknownHostException("Unable to resolve host")으로 한 번 실패하는 경우가 있다.
 * 이런 실패는 요청이 서버에 도달하기 전(DNS 조회/연결 단계)에 일어나므로,
 * POST 등 비멱등 요청이어도 안전하게 재시도할 수 있다.
 * (SocketTimeoutException 등 연결 이후 실패는 서버가 이미 요청을 받았을 수 있어 재시도하지 않는다.)
 */
class RetryInterceptor(
    private val maxRetries: Int = 2,
    private val retryDelayMillis: Long = 500L,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var lastException: IOException? = null

        for (attempt in 0..maxRetries) {
            try {
                return chain.proceed(request)
            } catch (e: IOException) {
                if (!e.isRetryable() || attempt == maxRetries) {
                    throw e
                }
                lastException = e
                Thread.sleep(retryDelayMillis)
            }
        }

        throw lastException ?: IOException("네트워크 요청에 실패했습니다.")
    }

    private fun IOException.isRetryable(): Boolean = this is UnknownHostException || this is ConnectException
}
