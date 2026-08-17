package com.hdb.tourfolio.feature.explore.model

/*
 * 필터 전체 선택은 API 입장에서 "필터 없음"과 동일하게 처리
 *
 * 예:
 * 지역 전체가 선택된 경우
 * -> regions = emptySet()
 * -> API 요청 시 regions = null
 */
internal fun normalizeExploreFilters(
    tags: Set<TagType>,
    themes: Set<ThemeType>,
    regions: Set<RegionType>,
): NormalizedExploreFilters =
    NormalizedExploreFilters(
        tags =
            if (tags.size == TagType.entries.size) {
                emptySet()
            } else {
                tags
            },
        themes =
            if (themes.size == ThemeType.entries.size) {
                emptySet()
            } else {
                themes
            },
        regions =
            if (regions.size == RegionType.entries.size) {
                emptySet()
            } else {
                regions
            },
    )
