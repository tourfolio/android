@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.mypage.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Natural90
import com.hdb.tourfolio.ui.theme.Primary

@Composable
fun NicknameSection(
    nickname: String,
    nicknameInput: String,
    isEditing: Boolean,
    isUpdating: Boolean,
    errorMessage: String?,
    onNicknameChange: (String) -> Unit,
    onEditClick: () -> Unit,
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        /*
         * 닉네임 표시 / 입력
         */
        if (isEditing) {
            NicknameInput(
                value = nicknameInput,
                onValueChange = onNicknameChange,
            )
        } else {
            Text(
                text = nickname,
                style =
                    LocalAppTypography
                        .current
                        .titleMedium
                        .bold
                        .copy(
                            color = Natural10,
                        ),
                textAlign = TextAlign.Center,
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp),
        )

        /*
         * 수정 / 확인 / 취소
         */
        if (isEditing) {
            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(14.dp),
                verticalAlignment =
                    Alignment.CenterVertically,
            ) {
                Text(
                    text =
                        if (isUpdating) {
                            "수정 중..."
                        } else {
                            "확인"
                        },
                    style =
                        LocalAppTypography
                            .current
                            .bodyLarge
                            .bold
                            .copy(
                                color = Primary,
                            ),
                    modifier =
                        Modifier.clickable(
                            enabled = !isUpdating,
                            onClick = onConfirmClick,
                        ),
                )

                Text(
                    text = "취소",
                    style =
                        LocalAppTypography
                            .current
                            .bodyLarge
                            .medium
                            .copy(
                                color = Natural60,
                            ),
                    modifier =
                        Modifier.clickable(
                            onClick = onCancelClick,
                        ),
                )
            }
        } else {
            Text(
                text = "닉네임 수정",
                style =
                    LocalAppTypography
                        .current
                        .bodySmall
                        .medium
                        .copy(
                            color = Natural60,
                        ),
                modifier =
                    Modifier
                        .background(
                            color =
                                Natural90.copy(
                                    alpha = 0.4f,
                                ),
                            shape =
                                RoundedCornerShape(20.dp),
                        )
                        .clickable(
                            onClick = onEditClick,
                        )
                        .padding(
                            horizontal = 16.dp,
                            vertical = 9.dp,
                        ),
            )
        }

        /*
         * 닉네임 수정 에러
         */
        if (errorMessage != null) {
            Spacer(
                modifier = Modifier.height(8.dp),
            )

            Text(
                text = errorMessage,
                style =
                    LocalAppTypography
                        .current
                        .bodySmall
                        .medium
                        .copy(
                            color = Primary,
                        ),
            )
        }
    }
}
