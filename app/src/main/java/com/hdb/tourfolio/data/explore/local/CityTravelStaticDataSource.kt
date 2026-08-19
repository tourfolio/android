package com.hdb.tourfolio.data.explore.local

import com.hdb.tourfolio.domain.explore.model.CityTravelDetail
import com.hdb.tourfolio.domain.explore.model.CityTravelSpot
import javax.inject.Inject
import javax.inject.Singleton

/*
 * 도시별 추천여행 상세 API가 아직 없어 임시로 고정 데이터를 제공한다.
 * 실제 API가 나오면 이 데이터소스만 교체하면 된다.
 */
@Singleton
class CityTravelStaticDataSource
    @Inject
    constructor() {
        private val travelDetailsById: Map<Long, CityTravelDetail> =
            mapOf(
                BUSAN_SUMMER_TRAVEL_ID to busanSummerTravelDetail(),
            )

        fun getDetail(travelId: Long): CityTravelDetail? = travelDetailsById[travelId]

        private fun busanSummerTravelDetail(): CityTravelDetail =
            CityTravelDetail(
                id = BUSAN_SUMMER_TRAVEL_ID,
                categoryTitle = "도시별 추천여행",
                title = "부산으로 떠나는 여름여행",
                placeCount = 6,
                cityImageKey = "bg_busan_demo",
                spots =
                    listOf(
                        CityTravelSpot(id = 1L, title = "흰여울길", imageKey = "bg_huinnyeoul_demo"),
                        CityTravelSpot(id = 2L, title = "광안대교", imageKey = "bg_busan_demo"),
                        CityTravelSpot(id = 3L, title = "해운대", imageKey = "bg_busan_demo"),
                        CityTravelSpot(id = 4L, title = "황령산 전망대", imageKey = "bg_busan_demo"),
                        CityTravelSpot(id = 5L, title = "감천 문화마을", imageKey = "bg_busan_demo"),
                        CityTravelSpot(id = 6L, title = "해동 용궁사", imageKey = "bg_busan_demo"),
                    ),
            )

        private companion object {
            const val BUSAN_SUMMER_TRAVEL_ID = 2L
        }
    }
