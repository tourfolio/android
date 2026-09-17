package com.hdb.tourfolio.data.explore

import com.hdb.tourfolio.data.common.network.ReadTimeoutInterceptor
import com.hdb.tourfolio.data.explore.remote.ExploreApiService
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException

class ExploreDetailRequestTest {
    @Test
    fun timeoutThenSuccessRetriesDetailWithLongerReadTimeout() =
        runBlocking {
            val timeouts = mutableListOf<Int>()
            val api =
                api { timeout ->
                    timeouts.add(timeout)
                    if (timeouts.size == 1) throw SocketTimeoutException("timeout")
                    200 to detailJson
                }

            assertEquals(7L, ExploreRepositoryImpl(api).getSpotDetail(7).spotId)
            assertEquals(listOf(30_000, 30_000), timeouts)
        }

    @Test
    fun repeatedTimeoutStopsAfterOneRetry() =
        runBlocking {
            var calls = 0
            val repository =
                ExploreRepositoryImpl(
                    api {
                        calls++
                        throw SocketTimeoutException("timeout")
                    },
                )

            val failure = runCatching { repository.getSpotDetail(7) }.exceptionOrNull()
            assertTrue(failure is SocketTimeoutException)
            assertEquals(2, calls)
        }

    @Test
    fun temporaryServerErrorsAreRetried() =
        runBlocking {
            for (code in listOf(502, 503, 504)) {
                var calls = 0
                val repository =
                    ExploreRepositoryImpl(
                        api {
                            calls++
                            if (calls == 1) code to "{}" else 200 to detailJson
                        },
                    )

                assertEquals(7L, repository.getSpotDetail(7).spotId)
                assertEquals(2, calls)
            }
        }

    @Test
    fun permanentHttpErrorsAreNotRetried() =
        runBlocking {
            for (code in listOf(401, 403, 404, 429, 500)) {
                var calls = 0
                val repository =
                    ExploreRepositoryImpl(
                        api {
                            calls++
                            code to "{}"
                        },
                    )

                val failure = runCatching { repository.getSpotDetail(7) }.exceptionOrNull()
                assertEquals(code, (failure as HttpException).code())
                assertEquals(1, calls)
            }
        }

    @Test
    fun otherEndpointsKeepDefaultTimeout() =
        runBlocking {
            var readTimeout = 0
            val api =
                api {
                    readTimeout = it
                    200 to "[]"
                }

            assertTrue(api.getCollections().isEmpty())
            assertEquals(10_000, readTimeout)
        }

    private fun api(respond: (Int) -> Pair<Int, String>): ExploreApiService {
        val client =
            OkHttpClient.Builder()
                .addInterceptor(ReadTimeoutInterceptor())
                .addInterceptor { chain ->
                    val (code, body) = respond(chain.readTimeoutMillis())
                    Response.Builder()
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_1)
                        .code(code)
                        .message("test")
                        .body(body.toResponseBody())
                        .build()
                }.build()
        return Retrofit.Builder()
            .baseUrl("https://example.invalid/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExploreApiService::class.java)
    }

    private val detailJson =
        """
        {"spotId":7,"name":"관광지","imageUrl":"","hasImage":false,
         "address":"서울","tags":[],"description":"소개","attractionPoints":[],"nearbySpots":[]}
        """.trimIndent()
}
