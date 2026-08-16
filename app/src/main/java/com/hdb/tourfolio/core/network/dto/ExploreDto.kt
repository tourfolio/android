package com.hdb.tourfolio.core.network.dto

/*
 * 탐색 첫 진입 시 카드
 */
data class ExploreMainCardDto(
    val spotId: Long,
    val name: String,
    val subTitle: String,
    val description: String,
    val location: String,
    val address: String,
    val imageUrl: String,
    val theme: String,
    val tags: List<String>,
    val totalCount: Int,
    val currentIndex: Int,
)

/*
 * 탐색 일반 관광지 카드
 */
data class ExploreCardDto(
    val id: Long,
    val name: String,
    val areaCode: String,
    val areaName: String,
    val themeTag: String,
    val tier: Int,
    val imageUrl: String,
    val description: String,
    val mapX: String,
    val mapY: String,
    val address: String,
    val tags: List<String>,
)