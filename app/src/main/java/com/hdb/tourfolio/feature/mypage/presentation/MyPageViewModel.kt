package com.hdb.tourfolio.feature.mypage.presentation

import com.hdb.tourfolio.core.mvi.MviEffect
import com.hdb.tourfolio.core.mvi.MviIntent
import com.hdb.tourfolio.core.mvi.MviState
import com.hdb.tourfolio.core.mvi.MviViewModel
import com.hdb.tourfolio.domain.mypage.model.MyPage
import com.hdb.tourfolio.domain.mypage.usecase.DeleteAccountUseCase
import com.hdb.tourfolio.domain.mypage.usecase.GetMyPageUseCase
import com.hdb.tourfolio.domain.mypage.usecase.UpdateNicknameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

/*
 * 마이페이지 조회 상태
 */
sealed interface MyPageRequestState {
    data object Loading : MyPageRequestState

    data class Success(
        val myPage: MyPage,
    ) : MyPageRequestState

    data class Error(
        val message: String,
    ) : MyPageRequestState
}

/*
 * 닉네임 수정 상태
 */
sealed interface NicknameUpdateState {
    data object Idle : NicknameUpdateState

    data object Loading : NicknameUpdateState

    data class Success(
        val nickname: String,
    ) : NicknameUpdateState

    data class Error(
        val message: String,
    ) : NicknameUpdateState
}

/*
 * Intent
 */
sealed interface MyPageIntent : MviIntent {
    data object FetchMyPage : MyPageIntent

    data class UpdateNickname(
        val nickname: String,
    ) : MyPageIntent

    data object DeleteAccount : MyPageIntent

    data object ClearNicknameUpdateState : MyPageIntent
}

/*
 * State
 */
data class MyPageState(
    val myPageState: MyPageRequestState =
        MyPageRequestState.Loading,
    val nicknameUpdateState: NicknameUpdateState =
        NicknameUpdateState.Idle,
) : MviState

/*
 * 일회성 이벤트
 */
sealed interface MyPageEffect : MviEffect {
    data object DeleteAccountSuccess : MyPageEffect

    data class Error(
        val message: String,
    ) : MyPageEffect
}

@HiltViewModel
class MyPageViewModel
@Inject
constructor(
    private val getMyPageUseCase: GetMyPageUseCase,
    private val updateNicknameUseCase: UpdateNicknameUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
) : MviViewModel<MyPageIntent, MyPageState, MyPageEffect>(
    MyPageState(),
) {

    init {
        processIntent(
            MyPageIntent.FetchMyPage,
        )
    }

    override suspend fun handleIntent(
        intent: MyPageIntent,
    ) {
        when (intent) {
            MyPageIntent.FetchMyPage ->
                fetchMyPage()

            is MyPageIntent.UpdateNickname ->
                updateNickname(
                    nickname = intent.nickname,
                )

            MyPageIntent.DeleteAccount ->
                deleteAccount()

            MyPageIntent.ClearNicknameUpdateState ->
                clearNicknameUpdateState()
        }
    }

    private suspend fun fetchMyPage() {
        setState {
            copy(
                myPageState =
                    MyPageRequestState.Loading,
            )
        }

        val result =
            try {
                MyPageRequestState.Success(
                    myPage =
                        getMyPageUseCase(),
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                MyPageRequestState.Error(
                    message =
                        e.message
                            ?: "마이페이지 정보를 불러오지 못했습니다.",
                )
            }

        setState {
            copy(
                myPageState = result,
            )
        }
    }

    private suspend fun updateNickname(
        nickname: String,
    ) {
        if (nickname.isBlank()) {
            setState {
                copy(
                    nicknameUpdateState =
                        NicknameUpdateState.Error(
                            message =
                                "닉네임을 입력해주세요.",
                        ),
                )
            }

            return
        }

        setState {
            copy(
                nicknameUpdateState =
                    NicknameUpdateState.Loading,
            )
        }

        try {
            val updatedNickname =
                updateNicknameUseCase(
                    nickname =
                        nickname.trim(),
                )

            setState {
                copy(
                    nicknameUpdateState =
                        NicknameUpdateState.Success(
                            nickname =
                                updatedNickname,
                        ),
                    myPageState =
                        when (
                            val current =
                                myPageState
                        ) {
                            is MyPageRequestState.Success ->
                                MyPageRequestState.Success(
                                    myPage =
                                        current.myPage.copy(
                                            nickname =
                                                updatedNickname,
                                        ),
                                )

                            else ->
                                current
                        },
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            setState {
                copy(
                    nicknameUpdateState =
                        NicknameUpdateState.Error(
                            message =
                                e.message
                                    ?: "닉네임 수정에 실패했습니다.",
                        ),
                )
            }
        }
    }

    private suspend fun deleteAccount() {
        try {
            deleteAccountUseCase()

            sendEffect(
                MyPageEffect.DeleteAccountSuccess,
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            sendEffect(
                MyPageEffect.Error(
                    message =
                        e.message
                            ?: "회원 탈퇴에 실패했습니다.",
                ),
            )
        }
    }

    private fun clearNicknameUpdateState() {
        setState {
            copy(
                nicknameUpdateState =
                    NicknameUpdateState.Idle,
            )
        }
    }
}
