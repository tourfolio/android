package com.hdb.tourfolio.domain.card.model

data class Card(
    val cardId: Long,
    val spotName: String,
    val imageUrl: String,
    val rarity: CardRarity,
    val theme: String,
    val region: String,
    val isOwned: Boolean,
)

data class CardCollection(
    val collectionRate: Double,
    val ownedCount: Int,
    val totalCount: Int,
    val cards: List<Card>,
)

data class CardDetail(
    val cardId: Long,
    val name: String,
    val address: String,
    val rarity: CardRarity,
    val theme: String,
    val imageUrl: String,
    val glowColorCode: String?,
    val cardNumber: String,
    val phrase: String,
    val isOwned: Boolean,
    val acquiredAt: String?,
    val acquisitionPath: String?,
    val message: String?,
)

data class CardLocation(
    val cardId: Long,
    val spotName: String,
    val latitude: Double,
    val longitude: Double,
)

data class CardAcquisition(
    val cardId: Long,
    val cardName: String,
    val rarity: CardRarity,
    val acquiredAt: String,
)

enum class CardRarity {
    LEGEND,
    EPIC,
    RARE,
    NORMAL,
}
