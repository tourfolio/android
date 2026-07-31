package com.hdb.tourfolio.feature.explore.model

/**
 * 테마 유형
 */
enum class ThemeType(
    val displayName: String,
) {
    HISTORY("역사"),
    NATURE("자연"),
    CULTURE("문화"),
}

/**
 * 관광지 지역
 */
enum class RegionType(
    val displayName: String,
) {
    SEOUL("서울"),
    BUSAN("부산"),
    DAEGU("대구"),
    INCHEON("인천"),
    GWANGJU("광주"),
    DAEJEON("대전"),
    ULSAN("울산"),
    GYEONGGI("경기"),
    GANGWON("강원"),
    CHUNGBUK("충북"),
    CHUNGNAM("충남"),
    JEONBUK("전북"),
    JEONNAM("전남"),
    GYEONGBUK("경북"),
    GYEONGNAM("경남"),
    JEJU("제주"),
}

/**
 * 관광지 태그 (임시)
 */
enum class TagType(
    val displayName: String,
) {
    HISTORY("역사"),
    PALACE("궁궐"),
    CULTURAL_HERITAGE("문화유산"),
    TEMPLE("사찰"),

    NATURE("자연"),
    SEA("바다"),
    MOUNTAIN("산"),
    BEACH("해변"),
    SUNRISE("일출"),
    NIGHT_VIEW("야경"),

    HEALING("힐링"),
    WALK("산책"),
    HIKING("등산"),
    PHOTO_SPOT("사진명소"),

    FAMILY_TRAVEL("가족여행"),
    COUPLE_TRAVEL("커플여행"),
    SOLO_TRAVEL("혼자여행"),

    POPULAR_PLACE("인기명소"),
    CITY_TRAVEL("도시여행"),
    VILLAGE("마을"),
}
