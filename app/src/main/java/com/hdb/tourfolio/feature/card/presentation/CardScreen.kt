@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.card.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hdb.tourfolio.feature.card.presentation.components.CardAcquisitionSuccessScreen
import com.hdb.tourfolio.feature.card.presentation.components.CardDetailBottomSheet
import com.hdb.tourfolio.feature.card.presentation.components.CardFilterBar
import com.hdb.tourfolio.feature.card.presentation.components.CardFilterState
import com.hdb.tourfolio.feature.card.presentation.components.ExpandedImageScreen
import com.hdb.tourfolio.feature.card.presentation.components.LocationDialogType
import com.hdb.tourfolio.feature.card.presentation.components.LocationPermissionDialog
import com.hdb.tourfolio.feature.card.presentation.components.LocationPermissionRequiredDialog
import com.hdb.tourfolio.feature.card.presentation.components.LocationVerificationAnimationScreen
import com.hdb.tourfolio.feature.card.presentation.components.LocationVerificationAnimationState
import com.hdb.tourfolio.feature.card.presentation.components.OwnedCardStatus
import com.hdb.tourfolio.feature.card.presentation.components.TourCard
import com.hdb.tourfolio.feature.card.presentation.components.getCurrentPreciseLocation
import com.hdb.tourfolio.feature.card.presentation.components.isPreciseLocationGranted
import com.hdb.tourfolio.feature.card.presentation.components.shouldShowLocationPermissionFlow
import com.hdb.tourfolio.ui.components.CommonHeader
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.TourfolioTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun CardScreen(
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onCardClick: (Long) -> Unit = {},
    onExpandedImageVisibilityChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: CardViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val state by viewModel.state.collectAsStateWithLifecycle()
    val collectionUiState = state.collection
    val detailUiState = state.detail
    val acquireUiState = state.acquire

    var filterState by remember { mutableStateOf(CardFilterState()) }

    /*
     * 현재 선택된 카드
     */
    var selectedCardId by remember { mutableStateOf<Long?>(null) }

    /*
     * 획득 카드 전체 이미지 보기
     */
    var showExpandedCardImage by remember { mutableStateOf(false) }

    /*
     * 확대 이미지 화면 표시 여부 AppNavHost에 전달
     */
    LaunchedEffect(showExpandedCardImage) {
        onExpandedImageVisibilityChange(showExpandedCardImage)
    }

    DisposableEffect(Unit) {
        onDispose {
            onExpandedImageVisibilityChange(false)
        }
    }

    /*
     * 위치 권한 Dialog
     */
    var locationDialogType by remember { mutableStateOf<LocationDialogType?>(null) }
    var showLocationVerificationScreen by remember { mutableStateOf(false) }
    var currentLocationJob by remember { mutableStateOf<Job?>(null) }
    var locationErrorMessage by remember { mutableStateOf<String?>(null) }

    fun refetchCollection() {
        viewModel.processIntent(
            CardIntent.FetchCollection(
                region = filterState.region?.displayName,
                theme = filterState.theme?.displayName,
                rarity = filterState.rarity?.name,
            ),
        )
    }

    /*
     * 실제 위치 확인 시작
     */
    fun startLocationVerification() {
        val cardId = selectedCardId ?: return

        /*
         * 이전 위치 조회가 남아 있다면 취소
         */
        currentLocationJob?.cancel()

        /*
         * 이전 획득 상태 초기화
         */
        viewModel.processIntent(CardIntent.ClearAcquireState)

        locationErrorMessage = null

        /*
         * 즉시 CHECKING 화면 표시
         */
        showLocationVerificationScreen = true

        currentLocationJob =
            coroutineScope.launch {
                val currentLocation = getCurrentPreciseLocation(context = context)

                if (currentLocation == null) {
                    locationErrorMessage = "현재 위치를 확인할 수 없습니다.\nGPS가 켜져 있는지 확인해주세요."
                    return@launch
                }

                /*
                 * 실제 카드 획득 검증 시작
                 */
                viewModel.processIntent(
                    CardIntent.VerifyLocationAndAcquire(
                        cardId = cardId,
                        userLatitude = currentLocation.latitude,
                        userLongitude = currentLocation.longitude,
                    ),
                )
            }
    }

    /*
     * 카드 이미지 전체 보기
     */
    if (showExpandedCardImage) {
        val cardDetail = (detailUiState as? CardDetailUiState.Success)?.detail

        if (cardDetail != null) {
            ExpandedImageScreen(
                card = cardDetail,
                onCloseClick = { showExpandedCardImage = false },
                modifier = modifier,
            )
            return
        }
    }

    /*
     * 카드 획득 성공 화면
     */
    if (acquireUiState is CardAcquireUiState.Success) {
        CardAcquisitionSuccessScreen(
            cardName = acquireUiState.cardName,
            rarity = acquireUiState.rarity,
            acquiredAt = acquireUiState.acquiredAt,
            cardId = acquireUiState.cardId,
            onCollectionClick = {
                selectedCardId = null
                showLocationVerificationScreen = false
                viewModel.processIntent(CardIntent.ClearCardDetail)
                viewModel.processIntent(CardIntent.ClearAcquireState)
                refetchCollection()
            },
            onCloseClick = {
                selectedCardId = null
                showLocationVerificationScreen = false
                viewModel.processIntent(CardIntent.ClearCardDetail)
                viewModel.processIntent(CardIntent.ClearAcquireState)
                refetchCollection()
            },
            modifier = modifier,
        )
        return
    }

    /*
     * 위치 검증 전체 화면
     */
    if (showLocationVerificationScreen) {
        /*
         * 현재 GPS 획득 자체가 실패한 경우
         */
        if (locationErrorMessage != null) {
            CardError(
                title = "위치를 확인하지 못했습니다.",
                message = locationErrorMessage.orEmpty(),
                onRetryClick = { startLocationVerification() },
                onDismissClick = {
                    currentLocationJob?.cancel()
                    locationErrorMessage = null
                    showLocationVerificationScreen = false
                    viewModel.processIntent(CardIntent.ClearAcquireState)
                },
            )
            return
        }

        /*
         * 관광지 좌표 조회 또는 카드 획득 API 실패
         */
        if (acquireUiState is CardAcquireUiState.Error) {
            CardError(
                title = "카드 획득을 진행하지 못했습니다.",
                message = acquireUiState.message,
                onRetryClick = { startLocationVerification() },
                onDismissClick = {
                    currentLocationJob?.cancel()
                    showLocationVerificationScreen = false
                    viewModel.processIntent(CardIntent.ClearAcquireState)
                },
            )
            return
        }

        val verificationState =
            when (acquireUiState) {
                is CardAcquireUiState.TooFar -> LocationVerificationAnimationState.TOO_FAR
                else -> LocationVerificationAnimationState.CHECKING
            }

        val spotName =
            when (acquireUiState) {
                is CardAcquireUiState.TooFar -> acquireUiState.spotName
                else -> (detailUiState as? CardDetailUiState.Success)?.detail?.title ?: "관광지"
            }

        LocationVerificationAnimationScreen(
            state = verificationState,
            spotName = spotName,
            onCancelClick = {
                currentLocationJob?.cancel()
                showLocationVerificationScreen = false
                locationErrorMessage = null
                viewModel.processIntent(CardIntent.ClearAcquireState)
            },
            onCheckAgainClick = { startLocationVerification() },
            onBackClick = {
                currentLocationJob?.cancel()
                showLocationVerificationScreen = false
                locationErrorMessage = null
                selectedCardId = null
                viewModel.processIntent(CardIntent.ClearAcquireState)
                viewModel.processIntent(CardIntent.ClearCardDetail)
            },
            modifier = modifier,
        )
        return
    }

    /*
     * 수집 메인 API
     */
    when (val collection = collectionUiState) {
        CardCollectionUiState.Loading -> {
            CardLoading(modifier = modifier)
        }

        is CardCollectionUiState.Error -> {
            CardError(
                title = "카드 목록을 불러오지 못했습니다.",
                message = collection.message,
                onRetryClick = { refetchCollection() },
                modifier = modifier,
            )
        }

        is CardCollectionUiState.Success -> {
            val summary = collection.summary
            val cards = collection.cards

            Column(
                modifier = modifier.fillMaxSize().background(Natural100),
            ) {
                Column(
                    modifier = Modifier.padding(start = 22.dp, top = 20.dp, end = 22.dp),
                ) {
                    CommonHeader(
                        onProfileClick = onProfileClick,
                        onNotificationClick = onNotificationClick,
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    OwnedCardStatus(
                        ownedCardCount = summary.ownedCount,
                        totalCardCount = summary.totalCount,
                    )

                    Spacer(modifier = Modifier.height(34.dp))

                    /*
                     * 서버 필터
                     */
                    CardFilterBar(
                        filterState = filterState,
                        onAllClick = {
                            filterState = CardFilterState()
                            viewModel.processIntent(CardIntent.FetchCollection())
                        },
                        onRegionSelected = { region ->
                            filterState = filterState.copy(region = region)
                            refetchCollection()
                        },
                        onThemeSelected = { theme ->
                            filterState = filterState.copy(theme = theme)
                            refetchCollection()
                        },
                        onRaritySelected = { rarity ->
                            filterState = filterState.copy(rarity = rarity)
                            refetchCollection()
                        },
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "총 ${collection.filteredCount}장",
                        style = LocalAppTypography.current.bodySmall.medium.copy(color = Natural60),
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(count = 3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 22.dp, end = 22.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(items = cards, key = { it.id }) { item ->
                        TourCard(
                            item = item,
                            onClick = { cardId ->
                                selectedCardId = cardId
                                viewModel.processIntent(CardIntent.FetchCardDetail(cardId))
                                onCardClick(cardId)
                            },
                        )
                    }
                }
            }
        }
    }

    /*
     * 카드 상세 API
     */
    when (val detail = detailUiState) {
        CardDetailUiState.Idle -> Unit

        CardDetailUiState.Loading -> {
            CardLoading(overlay = true)
        }

        is CardDetailUiState.Error -> {
            CardError(
                title = "카드 정보를 불러오지 못했습니다.",
                message = detail.message,
                onRetryClick = {
                    selectedCardId?.let { cardId ->
                        viewModel.processIntent(CardIntent.FetchCardDetail(cardId))
                    }
                },
                onDismissClick = {
                    selectedCardId = null
                    viewModel.processIntent(CardIntent.ClearCardDetail)
                },
                overlay = true,
            )
        }

        is CardDetailUiState.Success -> {
            CardDetailBottomSheet(
                card = detail.detail,
                onDismissRequest = {
                    selectedCardId = null
                    viewModel.processIntent(CardIntent.ClearCardDetail)
                    viewModel.processIntent(CardIntent.ClearAcquireState)
                },
                onExpandImageClick = { showExpandedCardImage = true },
                /*
                 * 카드 획득하기
                 */
                onAcquireClick = {
                    when {
                        isPreciseLocationGranted(context = context) -> {
                            startLocationVerification()
                        }

                        shouldShowLocationPermissionFlow(context = context) -> {
                            locationDialogType = LocationDialogType.PERMISSION_REQUEST
                        }

                        else -> {
                            locationDialogType = LocationDialogType.PERMISSION_REQUIRED
                        }
                    }
                },
            )
        }
    }

    /*
     * 위치 권한 Dialog
     */
    when (locationDialogType) {
        LocationDialogType.PERMISSION_REQUEST -> {
            LocationPermissionDialog(
                onDismissRequest = { locationDialogType = null },
                onPreciseLocationGranted = {
                    locationDialogType = null
                    startLocationVerification()
                },
                onPreciseLocationDenied = { locationDialogType = null },
            )
        }

        LocationDialogType.PERMISSION_REQUIRED -> {
            LocationPermissionRequiredDialog(
                onDismissRequest = { locationDialogType = null },
            )
        }

        null -> Unit
    }
}

/*
 * 공통 Loading
 */
@Composable
private fun CardLoading(
    modifier: Modifier = Modifier,
    overlay: Boolean = false,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    color = if (overlay) Color.Black.copy(alpha = 0.35f) else Natural100,
                ),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = Primary)
    }
}

/*
 * 공통 Error
 */
@Composable
private fun CardError(
    title: String,
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
    onDismissClick: (() -> Unit)? = null,
    overlay: Boolean = false,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    color = if (overlay) Color.Black.copy(alpha = 0.35f) else Natural100,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier =
                if (overlay) {
                    Modifier
                        .padding(horizontal = 34.dp)
                        .background(
                            color = Natural100,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        )
                        .padding(horizontal = 28.dp, vertical = 24.dp)
                } else {
                    Modifier.padding(horizontal = 28.dp)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                style = LocalAppTypography.current.bodyLarge.bold.copy(color = Natural10),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                style = LocalAppTypography.current.bodySmall.medium.copy(color = Natural60),
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = onRetryClick) {
                Text(
                    text = "다시 시도",
                    style = LocalAppTypography.current.bodySmall.bold.copy(color = Primary),
                )
            }

            if (onDismissClick != null) {
                TextButton(onClick = onDismissClick) {
                    Text(
                        text = "닫기",
                        style = LocalAppTypography.current.bodySmall.medium.copy(color = Natural60),
                    )
                }
            }
        }
    }
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
    }
}
