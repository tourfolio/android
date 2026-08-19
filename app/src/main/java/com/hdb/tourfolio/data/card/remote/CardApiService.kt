package com.hdb.tourfolio.data.card.remote

import com.hdb.tourfolio.data.card.remote.dto.CardAcquireDto
import com.hdb.tourfolio.data.card.remote.dto.CardCollectionDto
import com.hdb.tourfolio.data.card.remote.dto.CardDetailDto
import com.hdb.tourfolio.data.card.remote.dto.CardLocationDto
import com.hdb.tourfolio.data.common.network.Authenticated
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CardApiService {
    /*
     * 수집 메인 조회
     */
    @Authenticated
    @GET("api/v1/collection")
    suspend fun getCollection(
        @Query("region") region: String? = null,
        @Query("theme") theme: String? = null,
        @Query("rarity") rarity: String? = null,
    ): CardCollectionDto

    /*
     * 카드 상세 조회
     */
    @Authenticated
    @GET("api/v1/collection/cards/{cardId}")
    suspend fun getCardDetail(
        @Path("cardId") cardId: Long,
    ): CardDetailDto

    /*
     * 카드가 속한 관광지 좌표 조회
     */
    @Authenticated
    @GET("api/v1/collection/cards/{cardId}/location")
    suspend fun getCardLocation(
        @Path("cardId") cardId: Long,
    ): CardLocationDto

    /*
     * 카드 획득
     * 앱에서 거리값이 200m 이하임을 계산한 후 호출
     * 200m 이하임을 확인한 뒤 호출
     */
    @Authenticated
    @POST("api/v1/collection/cards/{cardId}/acquire")
    suspend fun acquireCard(
        @Path("cardId") cardId: Long,
    ): CardAcquireDto
}
