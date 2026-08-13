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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.feature.card.components.CardDetailBottomSheet
import com.hdb.tourfolio.feature.card.components.CardFilterBar
import com.hdb.tourfolio.feature.card.components.CardFilterState
import com.hdb.tourfolio.feature.card.components.CardHeader
import com.hdb.tourfolio.feature.card.components.LocationDialogType
import com.hdb.tourfolio.feature.card.components.LocationPermissionDialog
import com.hdb.tourfolio.feature.card.components.LocationPermissionRequiredDialog
import com.hdb.tourfolio.feature.card.components.LocationVerificationAnimationScreen
import com.hdb.tourfolio.feature.card.components.LocationVerificationAnimationState
import com.hdb.tourfolio.feature.card.components.OwnedCardStatus
import com.hdb.tourfolio.feature.card.components.TourCard
import com.hdb.tourfolio.feature.card.components.isPreciseLocationGranted
import com.hdb.tourfolio.feature.card.components.shouldShowLocationPermissionFlow
import com.hdb.tourfolio.feature.card.mock.CardListItemUiModel
import com.hdb.tourfolio.feature.card.mock.CardMockData
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.TourfolioTheme
import kotlinx.coroutines.delay

private const val TEMP_LOCATION_CHECK_DELAY_MS = 3000L

@Composable
fun CardScreen(
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onCardClick: (Long) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context =
        LocalContext.current

    var filterState by remember {
        mutableStateOf(
            CardFilterState(),
        )
    }

    var selectedCardId by remember {
        mutableStateOf<Long?>(null)
    }

    var locationDialogType by remember {
        mutableStateOf<LocationDialogType?>(null)
    }

    var showLocationVerificationScreen by remember {
        mutableStateOf(false)
    }

    /*
     * 위치 검증 화면 현재 상태
     *
     * CHECKING:
     * 위치 확인 중
     *
     * TOO_FAR:
     * 200m보다 먼 상태
     */
    var locationVerificationState by remember {
        mutableStateOf(
            LocationVerificationAnimationState.CHECKING,
        )
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

    /*
    * 임시 위치 검증 처리
    * 실제 GPS / 서버 연동 전 UI 확인을 위해
    * CHECKING 상태가 시작되면 3초 뒤
    * 무조건 TOO_FAR 상태로 전환합니다.
    */
    LaunchedEffect(
        showLocationVerificationScreen,
        locationVerificationState,
    ) {
        if (
            showLocationVerificationScreen &&
            locationVerificationState ==
            LocationVerificationAnimationState.CHECKING
        ) {
            delay(TEMP_LOCATION_CHECK_DELAY_MS)

            locationVerificationState =
                LocationVerificationAnimationState.TOO_FAR
        }
    }

    /*
     * 위치 검증 화면
     */
    if (showLocationVerificationScreen) {
        LocationVerificationAnimationScreen(
            state = locationVerificationState,

            /*
             * CardDetailUiModel의 실제 필드는 title입니다.
             */
            spotName =
                selectedCardDetail?.title
                    ?: "관광지",

            /*
             * CHECKING 화면
             * "취소하기"
             *
             * 위치 검증 화면만 닫습니다.
             * selectedCardId는 유지하므로
             * 기존 카드 상세 바텀시트가 다시 나타납니다.
             */
            onCancelClick = {
                showLocationVerificationScreen = false
            },

            /*
             * TOO_FAR 화면
             * "위치 확인"
             *
             * 다시 위치 확인 중 상태로 전환합니다.
             */
            onCheckAgainClick = {
                locationVerificationState =
                    LocationVerificationAnimationState.CHECKING
            },

            /*
             * TOO_FAR 화면
             * "돌아가기"
             *
             * 위치 검증 화면과 카드 상세 바텀시트를
             * 모두 종료하여 수집 메인 화면으로 돌아갑니다.
             */
            onBackClick = {
                showLocationVerificationScreen = false
                selectedCardId = null
            },

            modifier = modifier,
        )

        /*
         * 아래 CardScreen UI가 같이 그려지지 않도록 종료합니다.
         */
        return
    }

    /*
     * 기존 수집 메인 화면
     */
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

    /*
     * 카드 상세 BottomSheet
     */
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
                when {
                    isPreciseLocationGranted(
                        context = context,
                    ) -> {
                        locationVerificationState =
                            LocationVerificationAnimationState.CHECKING

                        showLocationVerificationScreen = true
                    }

                    shouldShowLocationPermissionFlow(
                        context = context,
                    ) -> {
                        locationDialogType =
                            LocationDialogType.PERMISSION_REQUEST
                    }

                    else -> {
                        locationDialogType =
                            LocationDialogType.PERMISSION_REQUIRED
                    }
                }
            },
        )
    }

    /*
     * 위치 권한 Dialog
     */
    when (locationDialogType) {
        LocationDialogType.PERMISSION_REQUEST -> {
            LocationPermissionDialog(
                onDismissRequest = {
                    locationDialogType = null
                },
                onPreciseLocationGranted = {
                    locationDialogType = null

                    locationVerificationState =
                        LocationVerificationAnimationState.CHECKING

                    showLocationVerificationScreen = true
                },
                onPreciseLocationDenied = {
                    locationDialogType = null
                },
            )
        }

        LocationDialogType.PERMISSION_REQUIRED -> {
            LocationPermissionRequiredDialog(
                onDismissRequest = {
                    locationDialogType = null
                },
            )
        }

        null -> Unit
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
