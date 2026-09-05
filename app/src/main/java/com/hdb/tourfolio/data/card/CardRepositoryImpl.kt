package com.hdb.tourfolio.data.card

import android.content.Context
import com.hdb.tourfolio.data.auth.local.SessionLocalDataSource
import com.hdb.tourfolio.domain.auth.model.NotAuthenticatedException
import com.hdb.tourfolio.domain.card.model.Card
import com.hdb.tourfolio.domain.card.model.CardAcquisition
import com.hdb.tourfolio.domain.card.model.CardCollection
import com.hdb.tourfolio.domain.card.model.CardDetail
import com.hdb.tourfolio.domain.card.model.CardLocation
import com.hdb.tourfolio.domain.card.model.CardRarity
import com.hdb.tourfolio.domain.card.repository.CardRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val sessionLocalDataSource: SessionLocalDataSource,
) : CardRepository {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override suspend fun getCollection(region: String?, theme: String?, rarity: String?): CardCollection {
        val userId = sessionLocalDataSource.getUserId()
        val acquiredDates = localCards.associate { it.id to acquiredDate(userId, it.id) }
        val ownedIds = acquiredDates.filterValues { it != null }.keys
        val cards = localCards
            .filter { region == null || it.region == region || it.regionTypeName == region }
            .filter { theme == null || it.theme == theme || it.themeTypeName == theme }
            .filter { rarity == null || it.rarity.name == rarity }
            .map { it.toCard(acquiredAt = acquiredDates[it.id]) }
        return CardCollection(
            collectionRate = ownedIds.size.toDouble() / localCards.size * 100.0,
            ownedCount = ownedIds.size,
            totalCount = localCards.size,
            filteredCount = cards.size,
            cards = cards,
        )
    }

    override suspend fun getCardDetail(cardId: Long): CardDetail {
        val card = localCards.findCard(cardId)
        val acquiredAt = acquiredDate(sessionLocalDataSource.getUserId(), cardId)
        return CardDetail(
            cardId = card.id,
            name = card.name,
            address = card.address,
            rarity = card.rarity,
            theme = card.theme,
            imageUrl = "",
            glowColorCode = if (card.rarity == CardRarity.EPIC) "#8B5CF6" else "#A3A3A3",
            cardNumber = card.id.toString().padStart(3, '0'),
            phrase = card.phrase,
            isOwned = acquiredAt != null,
            acquiredAt = acquiredAt,
            acquisitionPath = acquiredAt?.let { "현장 방문" },
            message = "관광지에서 200m 이내로 가까이 가면 카드를 획득할 수 있어요.",
        )
    }

    override suspend fun getCardLocation(cardId: Long): CardLocation {
        val card = localCards.findCard(cardId)
        return CardLocation(card.id, card.name, card.latitude, card.longitude)
    }

    override suspend fun acquireCard(cardId: Long): CardAcquisition {
        val card = localCards.findCard(cardId)
        val userId = sessionLocalDataSource.getUserId() ?: throw NotAuthenticatedException()
        val acquiredAt = acquiredDate(userId, cardId) ?: LocalDate.now().toString()
        preferences.edit().putString(acquiredDateKey(userId, cardId), acquiredAt).apply()
        return CardAcquisition(card.id, card.name, card.rarity, acquiredAt)
    }

    private fun acquiredDate(userId: Long?, cardId: Long): String? =
        userId?.let { preferences.getString(acquiredDateKey(it, cardId), null) }
}

private data class LocalCard(
    val id: Long,
    val name: String,
    val address: String,
    val rarity: CardRarity,
    val theme: String,
    val region: String,
    val latitude: Double,
    val longitude: Double,
    val phrase: String,
) {
    fun toCard(acquiredAt: String?) = Card(id, name, "", rarity, theme, region, acquiredAt != null, acquiredAt)

    val regionTypeName: String
        get() = when (region) {
            "서울" -> "SEOUL"
            "부산" -> "BUSAN"
            "경북" -> "GYEONGBUK"
            "제주" -> "JEJU"
            else -> ""
        }

    val themeTypeName: String
        get() = when (theme) {
            "역사" -> "HISTORY"
            "자연" -> "NATURE"
            "문화" -> "CULTURE"
            else -> ""
        }
}

private val localCards = listOf(
    LocalCard(1, "경복궁", "서울특별시 종로구 사직로 161", CardRarity.RARE, "역사", "서울", 37.579617, 126.977041, "조선의 시간을 품은 궁궐"),
    LocalCard(2, "동궁과 월지", "경상북도 경주시 원화로 102", CardRarity.RARE, "역사", "경북", 35.834681, 129.226596, "달빛 아래 되살아나는 신라"),
    LocalCard(3, "감천문화마을", "부산광역시 사하구 감내2로 203", CardRarity.RARE, "문화", "부산", 35.097486, 129.010592, "산복도로에 피어난 색채"),
    LocalCard(4, "성산일출봉", "제주특별자치도 서귀포시 성산읍 일출로 284-12", CardRarity.EPIC, "자연", "제주", 33.458056, 126.942500, "제주의 바다에서 맞는 첫 햇살"),
    LocalCard(5, "광안리해수욕장", "부산광역시 수영구 광안해변로 219", CardRarity.RARE, "자연", "부산", 35.153169, 129.118666, "도시의 밤을 비추는 바다"),
    LocalCard(6, "남산서울타워", "서울특별시 용산구 남산공원길 105", CardRarity.RARE, "문화", "서울", 37.551169, 126.988227, "서울의 빛을 한눈에 담는 곳"),
)

private fun List<LocalCard>.findCard(cardId: Long): LocalCard =
    firstOrNull { it.id == cardId } ?: error("존재하지 않는 카드입니다: $cardId")

private fun acquiredDateKey(userId: Long, cardId: Long) = "user_${userId}_acquired_at_$cardId"

private const val PREFERENCES_NAME = "card_collection"
