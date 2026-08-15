@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.card.components

import android.R.id.message
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.card.mock.CardDetailUiModel
import com.hdb.tourfolio.feature.explore.model.ThemeType
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Natural90
import com.hdb.tourfolio.ui.theme.Natural99
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.Primary99

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailBottomSheet(
    card: CardDetailUiModel,
    onDismissRequest: () -> Unit,
    onExpandImageClick: () -> Unit = {},
    onAcquireClick: () -> Unit = {},
) {
    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Natural100,
        /*
         * 바텀시트가 나타날 때
         * CardScreen 뒤쪽을 어둡게 처리합니다.
         */
        scrimColor =
            Color.Black.copy(
                alpha = 0.8f,
            ),
        dragHandle = {
            Box(
                modifier =
                    Modifier
                        .padding(
                            top = 14.dp,
                            bottom = 22.dp,
                        )
                        .size(
                            width = 72.dp,
                            height = 6.dp,
                        )
                        .background(
                            color = Color(0xFFD7D7D7),
                            shape = RoundedCornerShape(50),
                        ),
            )
        },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
        ) {
            Text(
                text = "카드 상세",
                style =
                    LocalAppTypography.current.titleSmall.bold.copy(
                        color = Natural10,
                    ),
                modifier =
                    Modifier.padding(
                        horizontal = 22.dp,
                        vertical = 6.dp,
                    ),
            )

            Spacer(
                modifier = Modifier.height(18.dp),
            )

            HorizontalDivider(
                color = Natural90,
            )

            Spacer(
                modifier = Modifier.height(22.dp),
            )

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 22.dp,
                        ),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                /*
                 * 좌측 카드 이미지
                 */
                CardDetailImage(
                    card = card,
                    onExpandImageClick = onExpandImageClick,
                    modifier =
                        Modifier
                            .width(188.dp)
                            .height(263.dp),
                )

                /*
                 * 우측 상세 정보
                 */
                CardDetailInformation(
                    card = card,
                    modifier = Modifier.weight(1f),
                )
            }

            /*
             * 미획득 카드에서만 안내 문구와
             * 카드 획득하기 버튼을 표시합니다.
             */
            if (!card.isAcquired) {
                Spacer(
                    modifier = Modifier.height(24.dp),
                )

                AcquireGuide(
                    message =
                        card.message.ifBlank {
                            "관광지를 직접 방문하여 카드를 획득하세요"
                        },
                    modifier =
                        Modifier.padding(
                            horizontal = 22.dp,
                        ),
                )

                Spacer(
                    modifier = Modifier.height(32.dp),
                )

                HorizontalDivider(
                    color = Natural90,
                )

                Spacer(
                    modifier = Modifier.height(20.dp),
                )

                AcquireCardButton(
                    onClick = onAcquireClick,
                    modifier =
                        Modifier.padding(
                            horizontal = 22.dp,
                        ),
                )

                Spacer(
                    modifier = Modifier.height(18.dp),
                )
            } else {
                /*
                 * 획득 카드의 경우 별도 획득 버튼이 없으므로
                 * 하단 여백만 확보합니다.
                 */
                Spacer(
                    modifier = Modifier.height(28.dp),
                )
            }
        }
    }
}

@Composable
private fun CardDetailImage(
    card: CardDetailUiModel,
    onExpandImageClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .clip(
                    RoundedCornerShape(10.dp),
                ),
    ) {
        if (!card.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = card.imageUrl,
                contentDescription = card.title,
                modifier =
                    Modifier.matchParentSize(),
                contentScale =
                    ContentScale.Crop,
            )
        } else if (card.imageRes != null) {
            /*
             * Preview / 기존 MockData
             */
            Image(
                painter =
                    painterResource(
                        id = card.imageRes,
                    ),
                contentDescription =
                    card.title,
                modifier =
                    Modifier.matchParentSize(),
                contentScale =
                    ContentScale.Crop,
            )
        }

        /*
         * 미획득 카드
         */
        if (!card.isAcquired) {
            Box(
                modifier =
                    Modifier
                        .matchParentSize()
                        .background(
                            Color.Black.copy(
                                alpha = 0.92f,
                            ),
                        ),
            )

            Text(
                text = "Tourfolio",
                style =
                    LocalAppTypography.current.bodyLarge.heavy.copy(
                        color = Primary,
                    ),
                modifier =
                    Modifier.align(
                        Alignment.Center,
                    ),
            )
        }

        /*
         * 획득한 카드에서만
         * 사진 확대 버튼을 표시합니다.
         */
        if (card.isAcquired) {
            Box(
                modifier =
                    Modifier
                        .align(
                            Alignment.BottomEnd,
                        )
                        .padding(10.dp)
                        .size(48.dp)
                        .background(
                            color = Primary.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(50),
                        )
                        .clickable(
                            onClick = onExpandImageClick,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter =
                        painterResource(
                            id = R.drawable.ic_expand,
                        ),
                    contentDescription = "카드 이미지 확대",
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

@Composable
private fun CardDetailInformation(
    card: CardDetailUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        /*
         * 관광지명
         */
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter =
                    painterResource(
                        id = R.drawable.ic_location,
                    ),
                contentDescription = null,
                modifier = Modifier.size(26.dp),
            )

            Spacer(
                modifier = Modifier.width(10.dp),
            )

            Text(
                text = card.title,
                style =
                    LocalAppTypography.current.titleSmall.bold.copy(
                        color = Natural10,
                    ),
            )
        }

        Spacer(
            modifier = Modifier.height(22.dp),
        )

        CardInformationRow(
            label = "희귀도",
            value = card.rarity.displayName,
        )

        Spacer(
            modifier = Modifier.height(10.dp),
        )

        CardThemeInformationRow(
            themeType = card.themeType,
        )

        Spacer(
            modifier = Modifier.height(10.dp),
        )

        CardInformationRow(
            label = "획득일",
            value =
                card.acquiredDate
                    ?: "-",
        )
    }
}

@Composable
private fun CardInformationRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(
                    color = Natural99,
                    shape = RoundedCornerShape(10.dp),
                )
                .padding(
                    horizontal = 18.dp,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style =
                LocalAppTypography.current.bodySmall.medium.copy(
                    color = Natural60,
                ),
        )

        Text(
            text = value,
            style =
                LocalAppTypography.current.bodyLarge.bold.copy(
                    color = Natural10,
                ),
        )
    }
}

@Composable
private fun CardThemeInformationRow(
    themeType: ThemeType,
    modifier: Modifier = Modifier,
) {
    val themeIconRes =
        when (themeType) {
            ThemeType.HISTORY ->
                R.drawable.ic_theme_history_primary

            ThemeType.NATURE ->
                R.drawable.ic_theme_nature_primary

            ThemeType.CULTURE ->
                R.drawable.ic_theme_culture_primary
        }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(
                    color = Natural99,
                    shape = RoundedCornerShape(10.dp),
                )
                .padding(
                    horizontal = 18.dp,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "테마",
            style =
                LocalAppTypography.current.bodySmall.medium.copy(
                    color = Natural60,
                ),
        )

        Image(
            painter =
                painterResource(
                    id = themeIconRes,
                ),
            contentDescription = themeType.displayName,
            modifier = Modifier.size(26.dp),
        )
    }
}

@Composable
private fun AcquireGuide(
    message: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    color = Primary99,
                    shape = RoundedCornerShape(10.dp),
                )
                .padding(
                    horizontal = 18.dp,
                    vertical = 18.dp,
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter =
                painterResource(
                    id = R.drawable.ic_info,
                ),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )

        Spacer(
            modifier = Modifier.width(12.dp),
        )

        Text(
            text = message,
            style =
                LocalAppTypography.current.bodySmall.bold.copy(
                    color = Natural10,
                ),
        )
    }
}

@Composable
private fun AcquireCardButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    color = Primary,
                    shape = RoundedCornerShape(10.dp),
                )
                .clickable(
                    onClick = onClick,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "카드 획득하기",
            style =
                LocalAppTypography.current.bodyLarge.bold.copy(
                    color = Natural100,
                ),
        )
    }
}
