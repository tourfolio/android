@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.mypage.presentation

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.mypage.presentation.components.MyPageMenuCard
import com.hdb.tourfolio.feature.mypage.presentation.components.MyPageMenuItem
import com.hdb.tourfolio.feature.mypage.presentation.components.MyPageSummaryCard
import com.hdb.tourfolio.feature.mypage.presentation.components.NicknameSection
import com.hdb.tourfolio.ui.components.CommonBackHeader
import com.hdb.tourfolio.ui.components.CommonModal
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Primary

private enum class AccountDialogType {
    LOGOUT,
    DELETE_ACCOUNT,
}

@Composable
fun MyPageScreen(
    onBackClick: () -> Unit,
    onLogoutSuccess: () -> Unit = {},
    onDeleteAccountSuccess: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: MyPageViewModel = hiltViewModel(),
) {
    val state by
    viewModel.state
        .collectAsStateWithLifecycle()

    val context =
        LocalContext.current

    val isNicknameUpdating =
        state.nicknameUpdateState is
                NicknameUpdateState.Loading

    var isEditingNickname by remember {
        mutableStateOf(false)
    }

    var nicknameInput by remember {
        mutableStateOf("")
    }

    var accountDialogType by remember {
        mutableStateOf<AccountDialogType?>(null)
    }

    /*
     * 회원탈퇴 같은 일회성 이벤트
     */
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                MyPageEffect.LogoutSuccess -> {
                    onLogoutSuccess()
                }

                MyPageEffect.DeleteAccountSuccess -> {
                    onDeleteAccountSuccess()
                }

                is MyPageEffect.Error -> {
                    Toast
                        .makeText(
                            context,
                            effect.message,
                            Toast.LENGTH_SHORT,
                        )
                        .show()
                }
            }
        }
    }

    /*
     * 닉네임 수정 성공 시 편집 상태 종료
     */
    LaunchedEffect(state.nicknameUpdateState) {
        if (
            state.nicknameUpdateState
                    is NicknameUpdateState.Success
        ) {
            isEditingNickname =
                false

            viewModel.processIntent(
                MyPageIntent.ClearNicknameUpdateState,
            )
        }
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    Natural100,
                ),
    ) {
        /*
         * 고정 헤더
         */
        CommonBackHeader(
            title = "마이페이지",
            onBackClick = onBackClick,
        )

        when (
            val myPageState =
                state.myPageState
        ) {
            MyPageRequestState.Loading -> {
                Box(
                    modifier =
                        Modifier.fillMaxSize(),
                    contentAlignment =
                        Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = Primary,
                    )
                }
            }

            is MyPageRequestState.Error -> {
                Box(
                    modifier =
                        Modifier.fillMaxSize(),
                    contentAlignment =
                        Alignment.Center,
                ) {
                    Text(
                        text =
                            myPageState.message,
                        style =
                            LocalAppTypography
                                .current
                                .bodyLarge
                                .medium
                                .copy(
                                    color = Natural60,
                                ),
                    )
                }
            }

            is MyPageRequestState.Success -> {
                val myPage =
                    myPageState.myPage

                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(
                                rememberScrollState(),
                            )
                            .padding(
                                horizontal = 22.dp,
                            ),
                    horizontalAlignment =
                        Alignment.CenterHorizontally,
                ) {
                    Spacer(
                        modifier =
                            Modifier.height(
                                28.dp,
                            ),
                    )

                    /*
                     * 프로필 이미지
                     */
                    Image(
                        painter =
                            painterResource(
                                id = R.drawable.ic_profile,
                            ),
                        contentDescription =
                            "프로필 이미지",
                        modifier =
                            Modifier.size(
                                120.dp,
                            ),
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                24.dp,
                            ),
                    )

                    NicknameSection(
                        nickname =
                            myPage.nickname,
                        nicknameInput =
                            nicknameInput,
                        isEditing =
                            isEditingNickname,
                        isUpdating =
                            isNicknameUpdating,
                        errorMessage =
                            (
                                    state.nicknameUpdateState
                                            as? NicknameUpdateState.Error
                                    )?.message,
                        onNicknameChange = {
                            nicknameInput = it
                        },
                        onEditClick = {
                            nicknameInput =
                                myPage.nickname

                            isEditingNickname =
                                true
                        },
                        onConfirmClick = {
                            viewModel.processIntent(
                                MyPageIntent.UpdateNickname(
                                    nickname =
                                        nicknameInput,
                                ),
                            )
                        },
                        onCancelClick = {
                            nicknameInput =
                                myPage.nickname

                            isEditingNickname =
                                false

                            viewModel.processIntent(
                                MyPageIntent.ClearNicknameUpdateState,
                            )
                        },
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                30.dp,
                            ),
                    )

                    /*
                     * API 데이터
                     */
                    MyPageSummaryCard(
                        balance =
                            myPage.balance,
                        cardCount =
                            myPage.cardCount,
                        totalProfitRate =
                            myPage.totalProfitRate,
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                36.dp,
                            ),
                    )

                    /*
                     * 설정
                     */
                    MyPageMenuCard(
                        title = "설정",
                        iconRes =
                            R.drawable.ic_settings,
                        items =
                            listOf(
                                MyPageMenuItem(
                                    title =
                                        "알림 설정",
                                    onClick = {
                                        // 추후 구현
                                    },
                                ),
                                MyPageMenuItem(
                                    title =
                                        "라이트 / 다크모드",
                                    onClick = {
                                        // 추후 구현
                                    },
                                ),
                                MyPageMenuItem(
                                    title =
                                        "위치 권한 설정",
                                    onClick = {
                                        // 추후 구현
                                    },
                                ),
                            ),
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                14.dp,
                            ),
                    )

                    /*
                     * 앱 정보
                     */
                    MyPageMenuCard(
                        title = "앱 정보",
                        iconRes =
                            R.drawable.ic_smartphone,
                        items =
                            listOf(
                                MyPageMenuItem(
                                    title = "공지사항",
                                    onClick = {
                                        // 추후 구현
                                    },
                                ),
                                MyPageMenuItem(
                                    title = "이용약관",
                                    onClick = {
                                        // 추후 구현
                                    },
                                ),
                                MyPageMenuItem(
                                    title = "개인정보처리방침",
                                    onClick = {
                                        // 추후 구현
                                    },
                                ),
                                MyPageMenuItem(
                                    title = "앱버전",
                                    onClick = {
                                        // 추후 구현
                                    },
                                ),
                            ),
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                14.dp,
                            ),
                    )

                    /*
                     * 계정
                     */
                    MyPageMenuCard(
                        title = "계정",
                        iconRes =
                            R.drawable.ic_logout,
                        items =
                            listOf(
                                MyPageMenuItem(
                                    title =
                                        "로그아웃",
                                    onClick = {
                                        accountDialogType =
                                            AccountDialogType.LOGOUT
                                    },
                                ),
                                MyPageMenuItem(
                                    title =
                                        "회원 탈퇴",
                                    onClick = {
                                        accountDialogType =
                                            AccountDialogType.DELETE_ACCOUNT
                                    },
                                ),
                            ),
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                30.dp,
                            ),
                    )
                }
            }
        }
    }

    /*
     * 회원탈퇴 확인 모달
     */
    when (accountDialogType) {
        AccountDialogType.LOGOUT -> {
            CommonModal(
                message =
                    "로그아웃하시겠습니까?",
                onConfirmClick = {
                    accountDialogType =
                        null

                    viewModel.processIntent(
                        MyPageIntent.Logout,
                    )
                },
                onDismissClick = {
                    accountDialogType =
                        null
                },
            )
        }

        AccountDialogType.DELETE_ACCOUNT -> {
            CommonModal(
                message =
                    "정말 회원 탈퇴하시겠습니까?",
                onConfirmClick = {
                    accountDialogType =
                        null

                    viewModel.processIntent(
                        MyPageIntent.DeleteAccount,
                    )
                },
                onDismissClick = {
                    accountDialogType =
                        null
                },
            )
        }

        null ->
            Unit
    }
}
