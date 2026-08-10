@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.feature.card.components.CardDetailBottomSheet
import com.hdb.tourfolio.feature.card.components.CardFilterBar
import com.hdb.tourfolio.feature.card.components.CardFilterState
import com.hdb.tourfolio.feature.card.components.CardHeader
import com.hdb.tourfolio.feature.card.components.OwnedCardStatus
import com.hdb.tourfolio.feature.card.components.TourCard
import com.hdb.tourfolio.feature.card.mock.CardListItemUiModel
import com.hdb.tourfolio.feature.card.mock.CardMockData
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.TourfolioTheme

@Composable
fun CardScreen(
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onCardClick: (Long) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var filterState by remember {
        mutableStateOf(
            CardFilterState(),
        )
    }

    var selectedCardId by remember {
        mutableStateOf<Long?>(null)
    }

    val selectedCardDetail =
        remember(selectedCardId) {
            selectedCardId?.let { cardId ->
                CardMockData.findDetailById(
                    id = cardId,
                )
            }
        }

    val filteredCards =
        remember(filterState) {
            filterCards(
                items = CardMockData.listItems,
                filterState = filterState,
            )
        }

    val totalCardCount =
        remember {
            CardMockData.listItems.size
        }

    val acquiredCardCount =
        remember {
            CardMockData.listItems.count { item ->
                item.isAcquired
            }
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Natural100),
    ) {
        Column(
            modifier =
                Modifier.padding(
                    start = 22.dp,
                    top = 20.dp,
                    end = 22.dp,
                ),
        ) {
            CardHeader(
                onProfileClick = onProfileClick,
                onNotificationClick = onNotificationClick,
            )

            Spacer(
                modifier = Modifier.height(28.dp),
            )

            OwnedCardStatus(
                ownedCardCount = acquiredCardCount,
                totalCardCount = totalCardCount,
            )

            Spacer(
                modifier = Modifier.height(34.dp),
            )

            CardFilterBar(
                filterState = filterState,
                onAllClick = {
                    filterState =
                        CardFilterState()
                },
                onRegionSelected = { region ->
                    filterState =
                        filterState.copy(
                            region = region,
                        )
                },
                onThemeSelected = { theme ->
                    filterState =
                        filterState.copy(
                            theme = theme,
                        )
                },
                onRaritySelected = { rarity ->
                    filterState =
                        filterState.copy(
                            rarity = rarity,
                        )
                },
            )

            Spacer(
                modifier = Modifier.height(18.dp),
            )

            Text(
                text = "총 ${filteredCards.size}장",
                style =
                    LocalAppTypography.current.bodySmall.medium.copy(
                        color = Natural60,
                    ),
            )

            Spacer(
                modifier = Modifier.height(10.dp),
            )
        }

        LazyVerticalGrid(
            columns =
                GridCells.Fixed(
                    count = 3,
                ),
            modifier =
                Modifier
                    .fillMaxSize(),
            contentPadding =
                PaddingValues(
                    start = 22.dp,
                    end = 22.dp,
                    bottom = 24.dp,
                ),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp),
            verticalArrangement =
                Arrangement.spacedBy(8.dp),
        ) {
            items(
                items = filteredCards,
                key = { item ->
                    item.id
                },
            ) { item ->
                TourCard(
                    item = item,
                    onClick = { cardId ->
                        selectedCardId = cardId

                        /*
                         * 외부 추가 동작 대비해 기존 callback 유지
                         */
                        onCardClick(cardId)
                    },
                )
            }
        }
    }

    selectedCardDetail?.let { card ->
        CardDetailBottomSheet(
            card = card,
            onDismissRequest = {
                selectedCardId = null
            },
            onExpandImageClick = {
                /*
                 * 획득한 카드 이미지 확대 기능
                 */
            },
            onAcquireClick = {
                /*
                 * 미획득 카드 방문 인증 / 카드 획득 기능
                 */
            },
        )
    }
}

private fun filterCards(
    items: List<CardListItemUiModel>,
    filterState: CardFilterState,
): List<CardListItemUiModel> =
    items.filter { item ->
        val matchesRegion =
            filterState.region == null ||
                item.regionType == filterState.region

        val matchesTheme =
            filterState.theme == null ||
                item.themeType == filterState.theme

        val matchesRarity =
            filterState.rarity == null ||
                item.rarity == filterState.rarity

        matchesRegion &&
            matchesTheme &&
            matchesRarity
    }

@Preview(
    name = "Card Main Screen",
    showBackground = true,
    widthDp = 393,
    heightDp = 852,
)
@Composable
private fun CardScreenPreview() {
    TourfolioTheme {
        CardScreen()
    }
}
