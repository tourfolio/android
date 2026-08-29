package com.hdb.tourfolio.feature.notification.presentation

import com.hdb.tourfolio.core.mvi.MviEffect
import com.hdb.tourfolio.core.mvi.MviIntent
import com.hdb.tourfolio.core.mvi.MviState
import com.hdb.tourfolio.core.mvi.MviViewModel
import com.hdb.tourfolio.domain.notification.model.NotificationList
import com.hdb.tourfolio.domain.notification.usecase.GetNotificationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

/*
 * 알림 목록 조회 상태
 */
sealed interface NotificationRequestState {
    data object Loading : NotificationRequestState

    data class Success(
        val notifications: NotificationList,
    ) : NotificationRequestState

    data class Error(
        val message: String,
    ) : NotificationRequestState
}

/*
 * Intent
 */
sealed interface NotificationIntent : MviIntent {
    data object FetchNotifications : NotificationIntent
}

/*
 * State
 */
data class NotificationState(
    val notificationState: NotificationRequestState =
        NotificationRequestState.Loading,
) : MviState

/*
 * 현재는 별도의 일회성 이벤트가 없으므로 비어 있음
 */
sealed interface NotificationEffect : MviEffect

@HiltViewModel
class NotificationViewModel
@Inject
constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
) : MviViewModel<
        NotificationIntent,
        NotificationState,
        NotificationEffect,
        >(
    NotificationState(),
) {

    init {
        processIntent(
            NotificationIntent.FetchNotifications,
        )
    }

    override suspend fun handleIntent(
        intent: NotificationIntent,
    ) {
        when (intent) {
            NotificationIntent.FetchNotifications ->
                fetchNotifications()
        }
    }

    private suspend fun fetchNotifications() {
        setState {
            copy(
                notificationState =
                    NotificationRequestState.Loading,
            )
        }

        val result =
            try {
                NotificationRequestState.Success(
                    notifications =
                        getNotificationsUseCase(),
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                NotificationRequestState.Error(
                    message =
                        e.message
                            ?: "알림 목록을 불러오지 못했습니다.",
                )
            }

        setState {
            copy(
                notificationState =
                    result,
            )
        }
    }
}