@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hdb.tourfolio.domain.auth.model.User
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural20
import com.hdb.tourfolio.ui.theme.Natural99
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.Red
import com.hdb.tourfolio.ui.theme.TourfolioTheme

private enum class AuthAction { LOGIN, SIGNUP }

@Composable
fun AuthOverlay(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var lastAction by remember { mutableStateOf<AuthAction?>(null) }

    val requestState =
        when (lastAction) {
            AuthAction.LOGIN -> uiState.loginState
            AuthAction.SIGNUP -> uiState.signupState
            null -> AuthRequestState.Idle
        }

    AuthOverlayContent(
        modifier = modifier,
        email = email,
        onEmailChange = { email = it },
        password = password,
        onPasswordChange = { password = it },
        nickname = nickname,
        onNicknameChange = { nickname = it },
        uiState = requestState,
        onBackClick = onBackClick,
        onLoginClick = {
            lastAction = AuthAction.LOGIN
            viewModel.processIntent(AuthIntent.Login(email, password))
        },
        onSignupClick = {
            lastAction = AuthAction.SIGNUP
            viewModel.processIntent(AuthIntent.Signup(email, password, nickname))
        },
    )
}

@Composable
private fun AuthOverlayContent(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    nickname: String,
    onNicknameChange: (String) -> Unit,
    uiState: AuthRequestState,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        AuthHeader(onBackClick = onBackClick)

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            AuthTextField(
                label = "이메일",
                value = email,
                onValueChange = onEmailChange,
                keyboardType = KeyboardType.Email,
            )

            Spacer(modifier = Modifier.height(14.dp))

            AuthTextField(
                label = "비밀번호",
                value = password,
                onValueChange = onPasswordChange,
                keyboardType = KeyboardType.Password,
                isPassword = true,
            )

            Spacer(modifier = Modifier.height(14.dp))

            AuthTextField(
                label = "닉네임",
                value = nickname,
                onValueChange = onNicknameChange,
                keyboardType = KeyboardType.Text,
            )

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AuthButton(
                    label = "로그인",
                    containerColor = Primary,
                    onClick = onLoginClick,
                    modifier = Modifier.weight(1f),
                )

                AuthButton(
                    label = "회원가입",
                    containerColor = Natural20,
                    onClick = onSignupClick,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            AuthResult(uiState = uiState)
        }
    }
}

@Composable
private fun AuthHeader(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "←",
            style = LocalAppTypography.current.titleMedium.bold,
            color = Natural10,
            modifier =
                Modifier
                    .clickable(onClick = onBackClick)
                    .padding(end = 12.dp),
        )

        Text(
            text = "로그인 / 회원가입",
            style = LocalAppTypography.current.titleSmall.bold,
            color = Natural10,
        )
    }
}

@Composable
private fun AuthTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
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
                    .background(Natural99)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = LocalAppTypography.current.bodyLarge.medium.copy(color = Natural10),
                cursorBrush = SolidColor(Primary),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun AuthButton(
    label: String,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .height(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(containerColor)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = LocalAppTypography.current.titleSmall.bold,
            color = Natural100,
        )
    }
}

@Composable
private fun AuthResult(
    uiState: AuthRequestState,
    modifier: Modifier = Modifier,
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
                text = "${uiState.user.nickname}님 환영합니다.",
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

@Preview(
    name = "Auth Overlay Preview",
    showBackground = true,
    widthDp = 412,
    heightDp = 700,
)
@Composable
private fun AuthOverlayPreview() {
    TourfolioTheme(dynamicColor = false) {
        AuthOverlayContent(
            email = "user@example.com",
            onEmailChange = {},
            password = "",
            onPasswordChange = {},
            nickname = "투어폴리오유저",
            onNicknameChange = {},
            uiState =
                AuthRequestState.Success(
                    User(
                        id = 1L,
                        email = "user@example.com",
                        nickname = "투어폴리오유저",
                    ),
                ),
            onBackClick = {},
            onLoginClick = {},
            onSignupClick = {},
        )
    }
}
