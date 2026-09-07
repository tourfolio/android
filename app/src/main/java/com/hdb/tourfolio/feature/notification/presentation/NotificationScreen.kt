@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.notification.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hdb.tourfolio.domain.notification.model.Notification
import com.hdb.tourfolio.feature.notification.presentation.utils.formatNotificationDate
import com.hdb.tourfolio.feature.notification.presentation.utils.getNotificationIconRes
import com.hdb.tourfolio.feature.notification.presentation.utils.getNotificationTypeLabel
import com.hdb.tourfolio.ui.components.CommonBackHeader
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural50
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.Primary99

@Composable
fun NotificationScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = hiltViewModel(),
) {
    val state by
        viewModel.state
            .collectAsStateWithLifecycle()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    Natural100,
                ),
    ) {
        CommonBackHeader(
            title = "알림",
            onBackClick = onBackClick,
        )

        when (
            val notificationState =
                state.notificationState
        ) {
            NotificationRequestState.Loading -> {
                NotificationLoading()
            }

            is NotificationRequestState.Error -> {
                NotificationError(
                    message =
                        notificationState.message,
                    onRetryClick = {
                        viewModel.processIntent(
                            NotificationIntent.FetchNotifications,
                        )
                    },
                )
            }

            is NotificationRequestState.Success -> {
                NotificationContent(
                    newNotifications =
                        notificationState
                            .notifications
                            .newNotifications,
                    pastNotifications =
                        notificationState
                            .notifications
                            .pastNotifications,
                )
            }
        }
    }
}

@Composable
private fun NotificationContent(
    newNotifications: List<Notification>,
    pastNotifications: List<Notification>,
    modifier: Modifier = Modifier,
) {
    if (
        newNotifications.isEmpty() &&
        pastNotifications.isEmpty()
    ) {
        Box(
            modifier =
                modifier.fillMaxSize(),
            contentAlignment =
                Alignment.Center,
        ) {
            Text(
                text = "새로운 알림이 없습니다.",
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

        return
    }

    LazyColumn(
        modifier =
            modifier.fillMaxSize(),
    ) {
        /*
         * 새 알림
         */
        items(
            items = newNotifications,
            key = { notification ->
                "new_${notification.id}"
            },
        ) { notification ->
            NotificationItem(
                notification =
                notification,
                isNew =
                true,
            )
        }

        /*
         * 지난 알림
         */
        items(
            items = pastNotifications,
            key = { notification ->
                "past_${notification.id}"
            },
        ) { notification ->
            NotificationItem(
                notification =
                notification,
                isNew =
                false,
            )
        }

        item {
            Spacer(
                modifier =
                    Modifier.height(
                        30.dp,
                    ),
            )
        }
    }
}

@Composable
private fun NotificationItem(
    notification: Notification,
    isNew: Boolean,
    modifier: Modifier = Modifier,
) {
    val iconRes =
        getNotificationIconRes(
            type = notification.type,
        )

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    color =
                        if (isNew) {
                            Primary99
                        } else {
                            Natural100
                        },
                )
                .padding(
                    start = 28.dp,
                    top = 18.dp,
                    end = 28.dp,
                    bottom = 20.dp,
                ),
    ) {
        /*
         * 타입 / 날짜
         */
        Row(
            modifier =
                Modifier.fillMaxWidth(),
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment =
                    Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.spacedBy(
                        9.dp,
                    ),
            ) {
                if (iconRes != null) {
                    Image(
                        painter =
                            painterResource(
                                id = iconRes,
                            ),
                        contentDescription =
                            getNotificationTypeLabel(notification.type),
                        modifier =
                            Modifier.size(
                                16.dp,
                            ),
                    )
                }

                Text(
                    text =
                        getNotificationTypeLabel(notification.type),
                    style =
                        LocalAppTypography
                            .current
                            .labelLarge
                            .medium
                            .copy(
                                color = Natural50,
                            ),
                )
            }

            Text(
                text =
                    formatNotificationDate(
                        notification.createdAt,
                    ),
                style =
                    LocalAppTypography
                        .current
                        .labelLarge
                        .medium
                        .copy(
                            color = Natural50,
                        ),
            )
        }

        Spacer(
            modifier =
                Modifier.height(
                    14.dp,
                ),
        )

        Text(
            text =
                notification.message,
            style =
                LocalAppTypography
                    .current
                    .bodyLarge
                    .medium
                    .copy(
                        color = Natural10,
                    ),
            modifier =
                Modifier.padding(
                    start =
                        if (iconRes != null) {
                            29.dp
                        } else {
                            0.dp
                        },
                ),
        )
    }
}

@Composable
private fun NotificationLoading(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier.fillMaxSize(),
        contentAlignment =
            Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = Primary,
        )
    }
}

@Composable
private fun NotificationError(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier.fillMaxSize(),
        contentAlignment =
            Alignment.Center,
    ) {
        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally,
        ) {
            Text(
                text =
                    "알림 목록을 불러오지 못했습니다.",
                style =
                    LocalAppTypography
                        .current
                        .bodyLarge
                        .bold
                        .copy(
                            color = Natural10,
                        ),
            )

            Spacer(
                modifier =
                    Modifier.height(
                        8.dp,
                    ),
            )

            Text(
                text = message,
                style =
                    LocalAppTypography
                        .current
                        .bodySmall
                        .medium
                        .copy(
                            color = Natural60,
                        ),
            )

            Spacer(
                modifier =
                    Modifier.height(
                        8.dp,
                    ),
            )

            TextButton(
                onClick =
                onRetryClick,
            ) {
                Text(
                    text = "다시 시도",
                    style =
                        LocalAppTypography
                            .current
                            .bodySmall
                            .bold
                            .copy(
                                color = Primary,
                            ),
                )
            }
        }
    }
}
