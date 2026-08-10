@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.card.mock

import androidx.annotation.DrawableRes
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.explore.model.RegionType
import com.hdb.tourfolio.feature.explore.model.ThemeType

enum class CardRarity(
    val displayName: String,
) {
    LEGEND("Legend"),
    EPIC("Epic"),
    RARE("Rare"),
    NORMAL("Normal"),
}

data class CardListItemUiModel(
    val id: Long,
    val title: String,
    val regionType: RegionType,
    val themeType: ThemeType,
    val rarity: CardRarity,
    val acquiredDate: String?,
    val isAcquired: Boolean,
    @DrawableRes val imageRes: Int,
)

data class CardDetailUiModel(
    val id: Long,
    val title: String,
    val regionType: RegionType,
    val themeType: ThemeType,
    val rarity: CardRarity,
    val acquiredDate: String?,
    val isAcquired: Boolean,
    @DrawableRes val imageRes: Int,
)

object CardMockData {
    val detailItems: List<CardDetailUiModel> =
        listOf(
            CardDetailUiModel(
                id = 1L,
                title = "경복궁",
                regionType = RegionType.SEOUL,
                themeType = ThemeType.HISTORY,
                rarity = CardRarity.LEGEND,
                acquiredDate = "2026.06.22",
                isAcquired = true,
                imageRes = R.drawable.bg_gyeongju_demo,
            ),
            CardDetailUiModel(
                id = 2L,
                title = "광안리",
                regionType = RegionType.BUSAN,
                themeType = ThemeType.CULTURE,
                rarity = CardRarity.RARE,
                acquiredDate = null,
                isAcquired = false,
                imageRes = R.drawable.bg_busan_demo,
            ),
            CardDetailUiModel(
                id = 3L,
                title = "성산일출봉",
                regionType = RegionType.JEJU,
                themeType = ThemeType.NATURE,
                rarity = CardRarity.LEGEND,
                acquiredDate = "2026.07.03",
                isAcquired = true,
                imageRes = R.drawable.bg_seongsan_demo,
            ),
            CardDetailUiModel(
                id = 4L,
                title = "한라산",
                regionType = RegionType.JEJU,
                themeType = ThemeType.NATURE,
                rarity = CardRarity.EPIC,
                acquiredDate = null,
                isAcquired = false,
                imageRes = R.drawable.bg_seongsan_demo,
            ),
            CardDetailUiModel(
                id = 5L,
                title = "남산타워",
                regionType = RegionType.SEOUL,
                themeType = ThemeType.CULTURE,
                rarity = CardRarity.RARE,
                acquiredDate = "2026.05.18",
                isAcquired = true,
                imageRes = R.drawable.bg_namsan_demo,
            ),
            CardDetailUiModel(
                id = 6L,
                title = "흰여울길",
                regionType = RegionType.BUSAN,
                themeType = ThemeType.CULTURE,
                rarity = CardRarity.NORMAL,
                acquiredDate = "2026.06.30",
                isAcquired = true,
                imageRes = R.drawable.bg_huinnyeoul_demo,
            ),
            CardDetailUiModel(
                id = 7L,
                title = "첨성대",
                regionType = RegionType.GYEONGBUK,
                themeType = ThemeType.HISTORY,
                rarity = CardRarity.EPIC,
                acquiredDate = null,
                isAcquired = false,
                imageRes = R.drawable.bg_cheomseongdae_demo,
            ),
            CardDetailUiModel(
                id = 8L,
                title = "해운대",
                regionType = RegionType.BUSAN,
                themeType = ThemeType.NATURE,
                rarity = CardRarity.RARE,
                acquiredDate = "2026.07.17",
                isAcquired = true,
                imageRes = R.drawable.bg_busan_demo,
            ),
            CardDetailUiModel(
                id = 9L,
                title = "감천 문화마을",
                regionType = RegionType.BUSAN,
                themeType = ThemeType.CULTURE,
                rarity = CardRarity.NORMAL,
                acquiredDate = null,
                isAcquired = false,
                imageRes = R.drawable.bg_busan_demo,
            ),
            CardDetailUiModel(
                id = 10L,
                title = "해동 용궁사",
                regionType = RegionType.BUSAN,
                themeType = ThemeType.HISTORY,
                rarity = CardRarity.EPIC,
                acquiredDate = "2026.08.01",
                isAcquired = true,
                imageRes = R.drawable.bg_busan_demo,
            ),
        )

    val listItems: List<CardListItemUiModel> =
        detailItems.map { detail ->
            CardListItemUiModel(
                id = detail.id,
                title = detail.title,
                regionType = detail.regionType,
                themeType = detail.themeType,
                rarity = detail.rarity,
                acquiredDate = detail.acquiredDate,
                isAcquired = detail.isAcquired,
                imageRes = detail.imageRes,
            )
        }

    fun findDetailById(id: Long): CardDetailUiModel? =
        detailItems.find { item ->
            item.id == id
        }
}
