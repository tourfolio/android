package com.hdb.tourfolio.feature.explore.model

enum class ThemeType(
    val displayName: String,
) {
    HISTORY("역사"),
    NATURE("자연"),
    CULTURE("문화"),
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
}

enum class TagType(
    val displayName: String,
) {
    HISTORY("역사"),
    PALACE("궁궐"),
    JOSEON_DYNASTY("조선왕조"),
    NATIONAL_TREASURE("국보"),

    CULTURE("문화"),
    OBSERVATORY("전망대"),
    NIGHT_VIEW("야경"),

    NATURE("자연"),
    VOLCANO("화산"),
    UNESCO_NATURAL_HERITAGE("유네스코 세계자연유산"),
    SUNRISE("일출"),
    MOUNTAIN("산"),
    TOP_100_MOUNTAIN("대한민국 100대 명산"),
    HIKING("등산"),

    BEACH("해수욕장"),
    SEA("바다"),
    SUMMER("여름"),

    TEMPLE("사찰"),
    HANOK("한옥"),
    TRADITIONAL_CULTURE("전통문화"),
    UNESCO_CULTURAL_HERITAGE("유네스코 세계문화유산"),

    GARDEN("정원"),
    FLOWER("꽃"),
    HEALING("힐링"),

    CABLE_CAR("케이블카"),
    HALLYEO_WATERWAY("한려수도"),
}
