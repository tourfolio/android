package com.hdb.tourfolio.feature.mission.presentation

import com.hdb.tourfolio.domain.mission.model.MissionCategory

/*
 * 카테고리 필터 - null 은 "전체"를 의미
 */
val missionCategoryFilters: List<MissionCategory?> =
    listOf(
        null,
        MissionCategory.VISIT,
        MissionCategory.COLLECTION,
        MissionCategory.TRADE,
        MissionCategory.ATTENDANCE,
    )

val MissionCategory?.label: String
    get() =
        when (this) {
            null -> "전체"
            MissionCategory.VISIT -> "방문"
            MissionCategory.COLLECTION -> "수집"
            MissionCategory.TRADE -> "투자"
            MissionCategory.ATTENDANCE -> "출석"
            MissionCategory.ETC -> "기타"
        }
