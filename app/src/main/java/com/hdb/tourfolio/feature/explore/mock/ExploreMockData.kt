package com.hdb.tourfolio.feature.explore.mock

import androidx.annotation.DrawableRes
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.explore.model.RegionType
import com.hdb.tourfolio.feature.explore.model.TagType
import com.hdb.tourfolio.feature.explore.model.ThemeType

/**
 * 관광지 목록 화면에서 사용하는 간략한 데이터입니다.
 */
data class TourSpotListItemUiModel(
    val id: Long,
    val title: String,
    val address: String,
    val themeType: ThemeType,
    val regionType: RegionType,
    val tags: List<TagType>,
    @DrawableRes val imageRes: Int,
)

/**
 * 관광지 상세 화면의 주변 관광지 카드에서 사용하는 데이터입니다.
 */
data class NearbyTourSpotUiModel(
    val id: Long,
    val title: String,
    @DrawableRes val imageRes: Int,
)

/**
 * 관광지 상세 화면에서 사용하는 데이터입니다.
 */
data class TourSpotDetailUiModel(
    val id: Long,
    val title: String,
    val description: String,
    val address: String,
    val themeType: ThemeType,
    val regionType: RegionType,
    val tags: List<TagType>,
    @DrawableRes val imageRes: Int,
    val homepageUrl: String,
    val phoneNumber: String,
    val operatingHours: String,
    val operatingNoticeTitle: String?,
    val operatingNoticeContent: String?,
    val closedDays: String,
    val nearbySpots: List<NearbyTourSpotUiModel>,
)

object TourSpotMockData {
    /**
     * 모든 상세 화면에서 동일하게 사용하는 임시 주변 관광지 목록입니다.
     *
     * 실제 API 연결 시 각 관광지별 주변 관광지 응답으로 교체합니다.
     */
    private val sharedNearbySpots =
        listOf(
            NearbyTourSpotUiModel(
                id = 101L,
                title = "광화문",
                imageRes = R.drawable.bg_gyeongju_demo,
            ),
            NearbyTourSpotUiModel(
                id = 102L,
                title = "근정전",
                imageRes = R.drawable.bg_cheomseongdae_demo,
            ),
            NearbyTourSpotUiModel(
                id = 103L,
                title = "북촌한옥마을",
                imageRes = R.drawable.bg_seoul_demo,
            ),
            NearbyTourSpotUiModel(
                id = 104L,
                title = "청계천",
                imageRes = R.drawable.bg_search_top,
            ),
        )

    /**
     * 관광지 상세 목 데이터 10개입니다.
     */
    val detailItems: List<TourSpotDetailUiModel> =
        listOf(
            TourSpotDetailUiModel(
                id = 1L,
                title = "경복궁",
                description = "조선의 시간을 품은 공간,\n500년의 역사가 살아 숨 쉬는 곳",
                address = "서울특별시 종로구 사직로 161",
                themeType = ThemeType.HISTORY,
                regionType = RegionType.SEOUL,
                tags =
                    listOf(
                        TagType.HISTORY,
                        TagType.PALACE,
                        TagType.CULTURAL_HERITAGE,
                        TagType.POPULAR_PLACE,
                        TagType.WALK,
                    ),
                imageRes = R.drawable.bg_gyeongju_demo,
                homepageUrl = "https://royal.khs.go.kr/gbg",
                phoneNumber = "02-3700-3900",
                operatingHours = "09:00 ~ 18:00 (입장 마감 17:00)",
                operatingNoticeTitle = "마감시간 변동",
                operatingNoticeContent =
                    "계절에 따라 마감 시간이 17:00~18:30으로 변동됩니다.",
                closedDays = "매주 화요일 정기휴무",
                nearbySpots = sharedNearbySpots,
            ),
            TourSpotDetailUiModel(
                id = 2L,
                title = "광안리",
                description = "광안대교의 야경과 함께하는\n부산의 대표적인 해변 명소",
                address = "부산광역시 수영구 광안해변로 219",
                themeType = ThemeType.CULTURE,
                regionType = RegionType.BUSAN,
                tags =
                    listOf(
                        TagType.SEA,
                        TagType.BEACH,
                        TagType.NIGHT_VIEW,
                        TagType.HEALING,
                        TagType.COUPLE_TRAVEL,
                        TagType.PHOTO_SPOT,
                    ),
                imageRes = R.drawable.bg_busan_demo,
                homepageUrl = "https://www.visitbusan.net",
                phoneNumber = "051-610-4848",
                operatingHours = "상시 개방",
                operatingNoticeTitle = null,
                operatingNoticeContent = null,
                closedDays = "연중무휴",
                nearbySpots = sharedNearbySpots,
            ),
            TourSpotDetailUiModel(
                id = 3L,
                title = "성산일출봉",
                description = "제주의 푸른 바다와 일출을 만나는\n유네스코 세계자연유산",
                address = "제주특별자치도 서귀포시 성산읍 일출로 284-12",
                themeType = ThemeType.NATURE,
                regionType = RegionType.JEJU,
                tags =
                    listOf(
                        TagType.NATURE,
                        TagType.SEA,
                        TagType.SUNRISE,
                        TagType.HIKING,
                        TagType.FAMILY_TRAVEL,
                        TagType.PHOTO_SPOT,
                    ),
                imageRes = R.drawable.bg_seongsan_demo,
                homepageUrl = "https://www.jeju.go.kr",
                phoneNumber = "064-783-0959",
                operatingHours = "07:30 ~ 19:00",
                operatingNoticeTitle = "입장 마감 안내",
                operatingNoticeContent =
                    "기상 상황에 따라 탐방로 이용이 제한될 수 있습니다.",
                closedDays = "매월 첫 번째 월요일",
                nearbySpots = sharedNearbySpots,
            ),
            TourSpotDetailUiModel(
                id = 4L,
                title = "한라산",
                description = "계절마다 다른 풍경을 보여주는\n제주의 대표적인 자연 명소",
                address = "제주특별자치도 제주시 1100로 2070-61",
                themeType = ThemeType.NATURE,
                regionType = RegionType.JEJU,
                tags =
                    listOf(
                        TagType.NATURE,
                        TagType.MOUNTAIN,
                        TagType.HIKING,
                        TagType.HEALING,
                        TagType.FAMILY_TRAVEL,
                        TagType.PHOTO_SPOT,
                    ),
                imageRes = R.drawable.bg_seongsan_demo,
                homepageUrl = "https://www.jeju.go.kr/hallasan",
                phoneNumber = "064-713-9950",
                operatingHours = "탐방로별 입산 시간 상이",
                operatingNoticeTitle = "탐방 예약 안내",
                operatingNoticeContent =
                    "성판악 및 관음사 탐방로는 사전 예약이 필요할 수 있습니다.",
                closedDays = "기상 악화 시 입산 통제",
                nearbySpots = sharedNearbySpots,
            ),
            TourSpotDetailUiModel(
                id = 5L,
                title = "남산타워",
                description = "서울 도심의 풍경을 한눈에 담는\n대표적인 야경 명소",
                address = "서울특별시 용산구 남산공원길 105",
                themeType = ThemeType.CULTURE,
                regionType = RegionType.SEOUL,
                tags =
                    listOf(
                        TagType.NIGHT_VIEW,
                        TagType.CITY_TRAVEL,
                        TagType.COUPLE_TRAVEL,
                        TagType.POPULAR_PLACE,
                        TagType.PHOTO_SPOT,
                        TagType.WALK,
                    ),
                imageRes = R.drawable.bg_namsan_demo,
                homepageUrl = "https://www.seoultower.co.kr",
                phoneNumber = "02-3455-9277",
                operatingHours = "10:30 ~ 22:30",
                operatingNoticeTitle = null,
                operatingNoticeContent = null,
                closedDays = "연중무휴",
                nearbySpots = sharedNearbySpots,
            ),
            TourSpotDetailUiModel(
                id = 6L,
                title = "흰여울길",
                description = "바다를 따라 이어지는 골목과\n감성적인 풍경을 만나는 곳",
                address = "부산광역시 영도구 영선동4가 605-3",
                themeType = ThemeType.CULTURE,
                regionType = RegionType.BUSAN,
                tags =
                    listOf(
                        TagType.SEA,
                        TagType.VILLAGE,
                        TagType.WALK,
                        TagType.HEALING,
                        TagType.PHOTO_SPOT,
                        TagType.COUPLE_TRAVEL,
                    ),
                imageRes = R.drawable.bg_huinnyeoul_demo,
                homepageUrl = "https://www.yeongdo.go.kr",
                phoneNumber = "051-419-4067",
                operatingHours = "상시 개방",
                operatingNoticeTitle = "주민 거주 지역 안내",
                operatingNoticeContent =
                    "실제 주민이 생활하는 지역이므로 소음에 주의해 주세요.",
                closedDays = "연중무휴",
                nearbySpots = sharedNearbySpots,
            ),
            TourSpotDetailUiModel(
                id = 7L,
                title = "첨성대",
                description = "신라 시대의 과학과 역사를 간직한\n동양에서 가장 오래된 천문대",
                address = "경상북도 경주시 첨성로 140-25",
                themeType = ThemeType.HISTORY,
                regionType = RegionType.GYEONGBUK,
                tags =
                    listOf(
                        TagType.HISTORY,
                        TagType.CULTURAL_HERITAGE,
                        TagType.WALK,
                        TagType.POPULAR_PLACE,
                        TagType.PHOTO_SPOT,
                        TagType.FAMILY_TRAVEL,
                    ),
                imageRes = R.drawable.bg_cheomseongdae_demo,
                homepageUrl = "https://www.gyeongju.go.kr",
                phoneNumber = "054-779-8744",
                operatingHours = "09:00 ~ 22:00",
                operatingNoticeTitle = null,
                operatingNoticeContent = null,
                closedDays = "연중무휴",
                nearbySpots = sharedNearbySpots,
            ),
            TourSpotDetailUiModel(
                id = 8L,
                title = "해운대",
                description = "도심과 바다가 어우러진\n부산의 대표적인 해수욕장",
                address = "부산광역시 해운대구 해운대해변로 264",
                themeType = ThemeType.NATURE,
                regionType = RegionType.BUSAN,
                tags =
                    listOf(
                        TagType.SEA,
                        TagType.BEACH,
                        TagType.HEALING,
                        TagType.FAMILY_TRAVEL,
                        TagType.POPULAR_PLACE,
                        TagType.PHOTO_SPOT,
                    ),
                imageRes = R.drawable.bg_busan_demo,
                homepageUrl = "https://www.haeundae.go.kr",
                phoneNumber = "051-749-5700",
                operatingHours = "상시 개방",
                operatingNoticeTitle = "해수욕장 운영 기간",
                operatingNoticeContent =
                    "수영 가능 기간과 안전요원 운영 시간은 계절에 따라 달라집니다.",
                closedDays = "연중무휴",
                nearbySpots = sharedNearbySpots,
            ),
            TourSpotDetailUiModel(
                id = 9L,
                title = "감천 문화마을",
                description = "알록달록한 골목과 예술 작품을 따라\n부산의 이야기를 만나는 마을",
                address = "부산광역시 사하구 감내2로 203",
                themeType = ThemeType.CULTURE,
                regionType = RegionType.BUSAN,
                tags =
                    listOf(
                        TagType.CULTURAL_HERITAGE,
                        TagType.VILLAGE,
                        TagType.WALK,
                        TagType.PHOTO_SPOT,
                        TagType.FAMILY_TRAVEL,
                        TagType.CITY_TRAVEL,
                    ),
                imageRes = R.drawable.bg_busan_demo,
                homepageUrl = "https://www.gamcheon.or.kr",
                phoneNumber = "051-204-1444",
                operatingHours = "09:00 ~ 18:00",
                operatingNoticeTitle = "관람 예절 안내",
                operatingNoticeContent =
                    "주민 거주 지역이므로 주택 내부 촬영과 큰 소음을 피해 주세요.",
                closedDays = "연중무휴",
                nearbySpots = sharedNearbySpots,
            ),
            TourSpotDetailUiModel(
                id = 10L,
                title = "해동 용궁사",
                description = "바다와 맞닿은 절벽 위에서\n아름다운 일출을 감상하는 사찰",
                address = "부산광역시 기장군 기장읍 용궁길 86",
                themeType = ThemeType.HISTORY,
                regionType = RegionType.BUSAN,
                tags =
                    listOf(
                        TagType.HISTORY,
                        TagType.TEMPLE,
                        TagType.SEA,
                        TagType.SUNRISE,
                        TagType.PHOTO_SPOT,
                        TagType.FAMILY_TRAVEL,
                    ),
                imageRes = R.drawable.bg_busan_demo,
                homepageUrl = "https://www.yongkungsa.or.kr",
                phoneNumber = "051-722-7744",
                operatingHours = "04:30 ~ 19:20",
                operatingNoticeTitle = null,
                operatingNoticeContent = null,
                closedDays = "연중무휴",
                nearbySpots = sharedNearbySpots,
            ),
        )

    /**
     * 상세 데이터의 공통 필드로 목록 데이터를 생성합니다.
     */
    val listItems: List<TourSpotListItemUiModel> =
        detailItems.map { detail ->
            TourSpotListItemUiModel(
                id = detail.id,
                title = detail.title,
                address = detail.address,
                themeType = detail.themeType,
                regionType = detail.regionType,
                tags = detail.tags,
                imageRes = detail.imageRes,
            )
        }

    fun findDetailById(id: Long): TourSpotDetailUiModel? =
        detailItems.find { detail ->
            detail.id == id
        }
}