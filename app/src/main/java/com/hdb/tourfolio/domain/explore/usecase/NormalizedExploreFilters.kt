package com.hdb.tourfolio.domain.explore.usecase

import com.hdb.tourfolio.domain.common.model.RegionType
import com.hdb.tourfolio.domain.common.model.TagType
import com.hdb.tourfolio.domain.common.model.ThemeType

/*
 * 필터 전체 선택은 API 입장에서 "필터 없음"과 동일하게 처리한다.
 */
internal data class NormalizedExploreFilters(
    val tags: Set<TagType>,
    val themes: Set<ThemeType>,
    val regions: Set<RegionType>,
)

internal fun normalizeExploreFilters(
    tags: Set<TagType>,
    themes: Set<ThemeType>,
    regions: Set<RegionType>,
): NormalizedExploreFilters =
    NormalizedExploreFilters(
        tags = if (tags.size == TagType.entries.size) emptySet() else tags,
        themes = if (themes.size == ThemeType.entries.size) emptySet() else themes,
        regions = if (regions.size == RegionType.entries.size) emptySet() else regions,
    )
