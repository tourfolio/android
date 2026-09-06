@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.auth.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.Primary70
import com.hdb.tourfolio.ui.theme.TourfolioTheme

@Composable
fun SignupScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }

    SignupScreenContent(
        modifier = modifier,
        email = email,
        onEmailChange = { email = it },
        password = password,
        onPasswordChange = { password = it },
        nickname = nickname,
        onNicknameChange = { nickname = it },
        uiState = uiState.signupState,
        onBackClick = onBackClick,
        onSignupClick = {
            viewModel.processIntent(AuthIntent.Signup(email, password, nickname))
        },
    )
}

@Composable
private fun SignupScreenContent(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    nickname: String,
    onNicknameChange: (String) -> Unit,
    uiState: AuthRequestState,
    onBackClick: () -> Unit,
    onSignupClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        SignupHeader(onBackClick = onBackClick)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Tourfolio",
            style =
                LocalAppTypography.current.headlineLarge.heavy.copy(
                    color = Primary70,
                ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
        ) {
            AuthLabeledField(
                label = "이메일",
                value = email,
                onValueChange = onEmailChange,
                placeholder = "이메일을 입력해주세요.",
                keyboardType = KeyboardType.Email,
            )

            Spacer(modifier = Modifier.height(14.dp))

            AuthPasswordField(
                label = "비밀번호",
                value = password,
                onValueChange = onPasswordChange,
                placeholder = "비밀번호를 입력해주세요.",
            )

            Spacer(modifier = Modifier.height(14.dp))

            AuthLabeledField(
                label = "닉네임",
                value = nickname,
                onValueChange = onNicknameChange,
                placeholder = "닉네임을 입력해주세요.",
                keyboardType = KeyboardType.Text,
            )

            Spacer(modifier = Modifier.height(28.dp))

            AuthButton(
                label = "회원가입",
                containerColor = Primary,
                onClick = onSignupClick,
                height = 44.dp,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthResult(
                uiState = uiState,
                successMessage = { "${it.nickname}님, 회원가입이 완료되었습니다." },
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "로그인 화면으로 돌아가기",
                style = LocalAppTypography.current.bodySmall.medium,
                color = Natural60,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onBackClick),
            )
        }
    }
}

@Composable
private fun SignupHeader(
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
    }
}

@Preview(
    name = "Signup Screen Preview",
    showBackground = true,
    widthDp = 412,
    heightDp = 800,
)
@Composable
private fun SignupScreenPreview() {
    TourfolioTheme(dynamicColor = false) {
        SignupScreenContent(
            email = "user@example.com",
            onEmailChange = {},
            password = "",
            onPasswordChange = {},
            nickname = "투어폴리오유저",
            onNicknameChange = {},
            uiState = AuthRequestState.Idle,
            onBackClick = {},
            onSignupClick = {},
        )
    }
}
