package com.hdb.tourfolio.core.network.dto

data class CardItemDto(
    val cardId: Long,
    val spotName: String,
    val imageUrl: String,
    val rarity: String,
    val theme: String,
    val region: String,
    val isOwned: Boolean,
)

data class CardCollectionDto(
    val collectionRate: Double,
    val ownedCount: Int,
    val totalCount: Int,
    val cards: List<CardItemDto>,
)

data class CardDetailDto(
    val cardId: Long,
    val name: String,
    val address: String,
    val rarity: String,
    val theme: String,
    val imageUrl: String,
    val glowColorCode: String,
    val cardNumber: String,
    val phrase: String,
    val isOwned: Boolean,
    val acquiredAt: String?,
    val acquisitionPath: String?,
    val message: String?,
)

data class CardLocationDto(
    val cardId: Long,
    val spotName: String,
    val latitude: Double,
    val longitude: Double,
)

data class CardAcquireDto(
    val cardId: Long,
    val cardName: String,
    val rarity: String,
    val acquiredAt: String,
)