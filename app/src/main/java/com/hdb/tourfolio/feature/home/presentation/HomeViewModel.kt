package com.hdb.tourfolio.feature.home.presentation

import com.hdb.tourfolio.core.mvi.MviEffect
import com.hdb.tourfolio.core.mvi.MviIntent
import com.hdb.tourfolio.core.mvi.MviState
import com.hdb.tourfolio.core.mvi.MviViewModel
import com.hdb.tourfolio.domain.home.model.Home
import com.hdb.tourfolio.domain.home.usecase.GetHomeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

/*
 * 홈 화면 조회 상태
 */
sealed interface HomeRequestState {
    data object Loading : HomeRequestState

    data class Success(
        val home: Home,
    ) : HomeRequestState

    data class Error(
        val message: String,
    ) : HomeRequestState
}

/*
 * Intent
 */
sealed interface HomeIntent : MviIntent {
    data object FetchHome : HomeIntent
}

/*
 * State
 */
data class HomeState(
    val homeState: HomeRequestState =
        HomeRequestState.Loading,
) : MviState

/*
 * 현재 별도의 일회성 이벤트 없음
 */
sealed interface HomeEffect : MviEffect

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    private val getHomeUseCase: GetHomeUseCase,
) : MviViewModel<
        HomeIntent,
        HomeState,
        HomeEffect,
        >(
    HomeState(),
) {

    init {
        processIntent(
            HomeIntent.FetchHome,
        )
    }

    override suspend fun handleIntent(
        intent: HomeIntent,
    ) {
        when (intent) {
            HomeIntent.FetchHome ->
                fetchHome()
        }
    }

    private suspend fun fetchHome() {
        setState {
            copy(
                homeState =
                    HomeRequestState.Loading,
            )
        }

        val result =
            try {
                HomeRequestState.Success(
                    home =
                        getHomeUseCase(),
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                HomeRequestState.Error(
                    message =
                        e.message
                            ?: "홈 정보를 불러오지 못했습니다.",
                )
            }

        setState {
            copy(
                homeState = result,
            )
        }
    }
}