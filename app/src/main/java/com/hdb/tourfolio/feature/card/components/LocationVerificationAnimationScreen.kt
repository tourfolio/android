@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.card.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.TourfolioTheme
import kotlinx.coroutines.delay

enum class LocationVerificationAnimationState {
    CHECKING,
    TOO_FAR,
}

@Composable
fun LocationVerificationAnimationScreen(
    state: LocationVerificationAnimationState,
    spotName: String,
    onCancelClick: () -> Unit = {},
    onCheckAgainClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Natural100)
                .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(
            modifier = Modifier.weight(1f),
        )

        when (state) {
            LocationVerificationAnimationState.CHECKING -> {
                CheckingLocationAnimation()
            }

            LocationVerificationAnimationState.TOO_FAR -> {
                TooFarLocationAnimation()
            }
        }

        Spacer(
            modifier = Modifier.height(48.dp),
        )

        Text(
            text =
                when (state) {
                    LocationVerificationAnimationState.CHECKING ->
                        "위치를 확인하고 있어요"

                    LocationVerificationAnimationState.TOO_FAR ->
                        "아직 너무 멀어요"
                },
            style =
                LocalAppTypography.current.titleMedium.bold.copy(
                    color = Natural10,
                ),
            textAlign = TextAlign.Center,
        )

        Spacer(
            modifier = Modifier.height(14.dp),
        )

        Text(
            text =
                when (state) {
                    LocationVerificationAnimationState.CHECKING ->
                        "내 위치와 $spotName 획득 시점을 대조하는 중 입니다\n" +
                                "잠시만 기다려 주세요"

                    LocationVerificationAnimationState.TOO_FAR ->
                        "$spotName 200m 반경 안으로 이동하신 후\n" +
                                "위치 확인을 다시 눌러주세요"
                },
            style =
                LocalAppTypography.current.bodyLarge.medium.copy(
                    color = Natural60,
                ),
            textAlign = TextAlign.Center,
        )

        Spacer(
            modifier = Modifier.height(56.dp),
        )

        when (state) {
            LocationVerificationAnimationState.CHECKING -> {
                LocationPrimaryButton(
                    text = "취소하기",
                    onClick = onCancelClick,
                )
            }

            LocationVerificationAnimationState.TOO_FAR -> {
                LocationPrimaryButton(
                    text = "위치 확인",
                    onClick = onCheckAgainClick,
                )

                Spacer(
                    modifier = Modifier.height(12.dp),
                )

                LocationSecondaryButton(
                    text = "돌아가기",
                    onClick = onBackClick,
                )
            }
        }

        Spacer(
            modifier = Modifier.weight(1f),
        )
    }
}

/*
 * 위치 확인 중 애니메이션
 *
 * Figma에서 만든 위치 아이콘 3개를
 * 순차적으로 교체하여 통통 튀는 효과를 만듭니다.
 */
@Composable
private fun CheckingLocationAnimation(
    modifier: Modifier = Modifier,
) {
    val frames =
        remember {
            listOf(
                R.drawable.ic_location_check_1,
                R.drawable.ic_location_check_2,
                R.drawable.ic_location_check_3,
                R.drawable.ic_location_check_2,
            )
        }

    var currentFrame by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(180L)

            currentFrame =
                (currentFrame + 1) % frames.size
        }
    }

    Box(
        modifier =
            modifier
                .size(230.dp)
                .background(
                    color = androidx.compose.ui.graphics.Color(0xFFFFF8F6),
                    shape = RoundedCornerShape(50),
                ),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter =
                painterResource(
                    id = frames[currentFrame],
                ),
            contentDescription = null,
            modifier = Modifier.size(130.dp),
        )
    }
}

/*
 * 거리가 먼 경우 애니메이션
 *
 * 1. 바깥 땅 모양은 계속 360도 회전
 * 2. 중앙 Tourfolio 로고는
 *    회전 주기에 맞춰 Fade In / Fade Out
 */
@Composable
private fun TooFarLocationAnimation(
    modifier: Modifier = Modifier,
) {
    val infiniteTransition =
        rememberInfiniteTransition(
            label = "tooFarAnimation",
        )

    /*
     * 땅 모양 한 바퀴 회전
     */
    val rotation by
    infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = 2400,
                        easing = LinearEasing,
                    ),
                repeatMode = RepeatMode.Restart,
            ),
        label = "groundRotation",
    )

    /*
     * 중앙 Tourfolio 로고
     *
     * 한 회전 안에서:
     * 안 보임
     * → Fade In
     * → 유지
     * → Fade Out
     * → 안 보임
     */
    val logoAlpha by
    infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec =
            infiniteRepeatable(
                animation =
                    keyframes {
                        durationMillis = 2400

                        0f at 0
                        0f at 350
                        1f at 750
                        1f at 1500
                        0f at 1950
                        0f at 2400
                    },
                repeatMode = RepeatMode.Restart,
            ),
        label = "logoAlpha",
    )

    Box(
        modifier =
            modifier.size(240.dp),
        contentAlignment = Alignment.Center,
    ) {
        /*
         * 회전하는 땅 모양
         */
        Image(
            painter =
                painterResource(
                    id = R.drawable.bg_location_far,
                ),
            contentDescription = null,
            modifier =
                Modifier
                    .size(220.dp)
                    .rotate(rotation),
        )

        /*
         * 가운데 Tourfolio 흰색 로고
         *
         * 이 로고 자체는 회전하지 않고
         * Fade In / Fade Out만 합니다.
         */
        Image(
            painter =
                painterResource(
                    id = R.drawable.ic_tourfolio_location_white,
                ),
            contentDescription = null,
            modifier =
                Modifier
                    .size(105.dp)
                    .alpha(logoAlpha),
        )
    }
}

@Composable
private fun LocationPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(58.dp)
                .background(
                    color =
                        androidx.compose.ui.graphics.Color(
                            0xFF4A4A4A,
                        ),
                    shape = RoundedCornerShape(10.dp),
                )
                .clickable(
                    onClick = onClick,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style =
                LocalAppTypography.current.bodyLarge.bold.copy(
                    color = Natural100,
                ),
        )
    }
}

@Composable
private fun LocationSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(54.dp)
                .clickable(
                    onClick = onClick,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style =
                LocalAppTypography.current.bodyLarge.medium.copy(
                    color = Natural60,
                ),
        )
    }
}

@Preview(
    name = "Location Checking",
    showBackground = true,
    widthDp = 393,
    heightDp = 852,
)
@Composable
private fun LocationCheckingPreview() {
    TourfolioTheme {
        LocationVerificationAnimationScreen(
            state = LocationVerificationAnimationState.CHECKING,
            spotName = "남산",
        )
    }
}

@Preview(
    name = "Location Too Far",
    showBackground = true,
    widthDp = 393,
    heightDp = 852,
)
@Composable
private fun LocationTooFarPreview() {
    TourfolioTheme {
        LocationVerificationAnimationScreen(
            state = LocationVerificationAnimationState.TOO_FAR,
            spotName = "남산",
        )
    }
}