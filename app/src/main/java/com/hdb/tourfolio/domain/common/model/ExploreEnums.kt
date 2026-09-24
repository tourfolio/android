package com.hdb.tourfolio.domain.common.model

enum class ThemeType(
    val displayName: String,
) {
    HISTORY("역사"),
    NATURE("자연"),
    CULTURE("문화"),
    ;

    companion object {
        fun fromDisplayName(displayName: String): ThemeType? = entries.firstOrNull { it.displayName == displayName }
    }
}

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
    ;

    companion object {
        fun fromDisplayName(displayName: String): RegionType? = entries.firstOrNull { it.displayName == displayName }
    }
}

enum class TagType(
    val displayName: String,
) {
    PALACE("궁궐"),
    PERFORMANCE("공연"),
    SHOPPING("쇼핑"),
    NIGHT_VIEW("야경"),
    TRADITIONAL_MARKET("전통시장"),
    BEACH("해수욕장"),
    OBSERVATORY("전망대"),
    CAVE("동굴"),
    WALKING_TRAIL("산책로"),
    PHOTO_SPOT("포토스팟"),
    TEMPLE("사찰"),
    ISLAND("섬"),
    SCENIC_VIEW("절경"),
    THEME_PARK("테마파크"),
    LANDMARK("랜드마크"),
    DOLMEN("고인돌"),
    MOUNTAIN("산"),
    SUNRISE("일출명소"),
    ;

    companion object {
        fun fromDisplayName(displayName: String): TagType? = entries.firstOrNull { it.displayName == displayName }
    }
}
