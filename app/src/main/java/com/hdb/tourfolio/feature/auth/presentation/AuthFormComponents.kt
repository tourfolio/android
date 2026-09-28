@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.domain.auth.model.User
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Natural80
import com.hdb.tourfolio.ui.theme.Natural90
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.Red

@Composable
fun AuthLabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = LocalAppTypography.current.bodySmall.bold,
            color = Natural10,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Natural100)
                    .border(
                        width = 1.dp,
                        color = Natural80,
                        shape = RoundedCornerShape(12.dp),
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = LocalAppTypography.current.bodyLarge.medium.copy(color = Natural60),
                )
            }

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = LocalAppTypography.current.bodyLarge.medium.copy(color = Natural10),
                cursorBrush = SolidColor(Primary),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun AuthPasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = LocalAppTypography.current.bodySmall.bold,
            color = Natural10,
        )

        Spacer(modifier = Modifier.height(8.dp))

        PasswordInputRow(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
        )
    }
}

@Composable
fun AuthPasswordGroup(
    label: String,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordPlaceholder: String,
    passwordConfirm: String,
    onPasswordConfirmChange: (String) -> Unit,
    passwordConfirmPlaceholder: String,
    modifier: Modifier = Modifier,
    showVisibilityToggle: Boolean = true,
) {
    val mismatch = passwordConfirm.isNotEmpty() && password != passwordConfirm

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = LocalAppTypography.current.bodySmall.bold,
            color = Natural10,
        )

        Spacer(modifier = Modifier.height(8.dp))

        PasswordInputRow(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = passwordPlaceholder,
            showVisibilityToggle = showVisibilityToggle,
        )

        Spacer(modifier = Modifier.height(8.dp))

        PasswordInputRow(
            value = passwordConfirm,
            onValueChange = onPasswordConfirmChange,
            placeholder = passwordConfirmPlaceholder,
            showVisibilityToggle = showVisibilityToggle,
        )

        if (mismatch) {
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "비밀번호가 일치하지 않습니다.",
                style = LocalAppTypography.current.bodySmall.medium,
                color = Red,
            )
        }
    }
}

@Composable
private fun PasswordInputRow(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    showVisibilityToggle: Boolean = true,
) {
    var passwordVisible by remember { mutableStateOf(!showVisibilityToggle) }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Natural100)
                .border(
                    width = 1.dp,
                    color = Natural80,
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = LocalAppTypography.current.bodyLarge.medium.copy(color = Natural60),
                )
            }

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = LocalAppTypography.current.bodyLarge.medium.copy(color = Natural10),
                cursorBrush = SolidColor(Primary),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation =
                    if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (showVisibilityToggle) {
            Image(
                painter =
                    painterResource(
                        id = if (passwordVisible) R.drawable.ic_eye else R.drawable.ic_eye_off,
                    ),
                contentDescription = if (passwordVisible) "비밀번호 숨기기" else "비밀번호 표시",
                colorFilter = ColorFilter.tint(Natural60),
                modifier =
                    Modifier
                        .size(22.dp)
                        .clickable {
                            passwordVisible = !passwordVisible
                        },
            )
        }
    }
}

@Composable
fun AuthButton(
    label: String,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 56.dp,
    enabled: Boolean = true,
) {
    Box(
        modifier =
            modifier
                .height(height)
                .clip(RoundedCornerShape(14.dp))
                .background(if (enabled) containerColor else Natural90)
                .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = LocalAppTypography.current.titleSmall.bold,
            color = if (enabled) Natural100 else Natural60,
        )
    }
}

@Composable
fun AuthResult(
    uiState: AuthRequestState,
    modifier: Modifier = Modifier,
    successMessage: (User) -> String = { "${it.nickname}님 환영합니다." },
) {
    when (uiState) {
        is AuthRequestState.Idle -> {}

        is AuthRequestState.Loading -> {
            Box(
                modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = Primary)
            }
        }

        is AuthRequestState.Success -> {
            Text(
                text = successMessage(uiState.user),
                style = LocalAppTypography.current.bodyLarge.bold,
                color = Natural10,
                modifier = modifier.fillMaxWidth(),
            )
        }

        is AuthRequestState.Error -> {
            Text(
                text = uiState.message,
                style = LocalAppTypography.current.bodySmall.bold,
                color = Red,
                modifier = modifier.fillMaxWidth(),
            )
        }
    }
}
