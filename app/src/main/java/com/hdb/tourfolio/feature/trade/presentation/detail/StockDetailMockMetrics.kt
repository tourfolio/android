package com.hdb.tourfolio.feature.trade.presentation.detail

import kotlin.random.Random

/*
 * 공모가/거래량/관광 데이터 지표 API가 아직 없어 종목 ID로 시드를 고정한 임시 값을 제공한다.
 * 실제 API가 나오면 이 파일의 값/함수만 교체하면 된다.
 * (전일 종가는 api/stocks의 실제 prevPrice를 쓰므로 더 이상 여기서 임시로 만들지 않는다.)
 */
const val MOCK_OFFERING_PRICE = 20_000L

fun mockOfferingChangeRate(currentPrice: Long): Double =
    if (MOCK_OFFERING_PRICE != 0L) {
        (currentPrice - MOCK_OFFERING_PRICE) * 100.0 / MOCK_OFFERING_PRICE
    } else {
        0.0
    }

fun mockTodayVolume(stockId: Long): Int = Random(stockId * 131 + 7).nextInt(500, 5000)

data class MockTourDataIndicators(
    val demandIntensity: Int,
    val visitorForecast: Int,
    val resourceDemand: Int,
)

fun mockTourDataIndicators(stockId: Long): MockTourDataIndicators =
    MockTourDataIndicators(
        demandIntensity = Random(stockId * 19 + 3).nextInt(3, 11),
        visitorForecast = Random(stockId * 23 + 9).nextInt(3, 11),
        resourceDemand = Random(stockId * 29 + 17).nextInt(3, 11),
    )
