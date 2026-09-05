package com.hdb.tourfolio.feature.card.presentation.model

import androidx.annotation.DrawableRes
import com.hdb.tourfolio.domain.card.model.CardRarity
import com.hdb.tourfolio.domain.common.model.RegionType
import com.hdb.tourfolio.domain.common.model.ThemeType

val CardRarity.displayName: String
    get() =
        when (this) {
            CardRarity.LEGEND -> "Legend"
            CardRarity.EPIC -> "Epic"
            CardRarity.RARE -> "Rare"
            CardRarity.NORMAL -> "Normal"
        }

data class CardListItemUiModel(
    val id: Long,
    val title: String,
    val regionType: RegionType,
    val themeType: ThemeType,
    val rarity: CardRarity,
    val acquiredDate: String?,
    val isAcquired: Boolean,
    val imageUrl: String? = null,
    @DrawableRes val imageRes: Int? = null,
    @DrawableRes val backImageRes: Int? = null,
)

data class CardDetailUiModel(
    val id: Long,
    val title: String,
    val themeType: ThemeType,
    val rarity: CardRarity,
    val acquiredDate: String?,
    val isAcquired: Boolean,
    /*
     * 실제 API 이미지
     */
    val imageUrl: String? = null,
    /*
     * Preview / MockData 이미지
     */
    @DrawableRes
    val imageRes: Int? = null,
    @DrawableRes
    val backImageRes: Int? = null,
    /*
     * 상세 API 추가 정보
     */
    val address: String = "",
    val glowColorCode: String = "",
    val cardNumber: String = "",
    val phrase: String = "",
    val acquisitionPath: String? = null,
    val message: String = "",
)
