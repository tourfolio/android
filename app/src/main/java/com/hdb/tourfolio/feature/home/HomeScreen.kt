@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hdb.tourfolio.feature.trade.presentation.auth.AuthRequestState
import com.hdb.tourfolio.feature.trade.presentation.auth.AuthViewModel
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural20
import com.hdb.tourfolio.ui.theme.Natural50
import com.hdb.tourfolio.ui.theme.Natural99
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.Red

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var signupEmail by remember { mutableStateOf("") }
    var signupPassword by remember { mutableStateOf("") }
    var signupNickname by remember { mutableStateOf("") }

    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }

    LaunchedEffect(uiState.signupState) {
        if (uiState.signupState is AuthRequestState.Success) {
            Toast.makeText(context, "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show()
            signupEmail = ""
            signupPassword = ""
            signupNickname = ""
        }
    }

    LaunchedEffect(uiState.loginState) {
        if (uiState.loginState is AuthRequestState.Success) {
            Toast.makeText(context, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text(
            text = "홈",
            style = LocalAppTypography.current.titleMedium.bold,
            color = Natural10,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = uiState.currentUser?.let { "${it.nickname}님 (${it.email}) 로그인됨" } ?: "로그인되어 있지 않습니다.",
            style = LocalAppTypography.current.bodySmall.medium,
            color = Natural50,
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "회원가입",
            style = LocalAppTypography.current.titleSmall.bold,
            color = Natural10,
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeTextField(
            label = "이메일",
            value = signupEmail,
            onValueChange = { signupEmail = it },
            keyboardType = KeyboardType.Email,
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeTextField(
            label = "비밀번호",
            value = signupPassword,
            onValueChange = { signupPassword = it },
            keyboardType = KeyboardType.Password,
            isPassword = true,
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeTextField(
            label = "닉네임",
            value = signupNickname,
            onValueChange = { signupNickname = it },
            keyboardType = KeyboardType.Text,
        )

        Spacer(modifier = Modifier.height(16.dp))

        HomeButton(
            label = "회원가입",
            containerColor = Natural20,
            onClick = { viewModel.signup(signupEmail, signupPassword, signupNickname) },
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeResult(uiState = uiState.signupState)

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "로그인",
            style = LocalAppTypography.current.titleSmall.bold,
            color = Natural10,
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeTextField(
            label = "이메일",
            value = loginEmail,
            onValueChange = { loginEmail = it },
            keyboardType = KeyboardType.Email,
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeTextField(
            label = "비밀번호",
            value = loginPassword,
            onValueChange = { loginPassword = it },
            keyboardType = KeyboardType.Password,
            isPassword = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        HomeButton(
            label = "로그인",
            containerColor = Primary,
            onClick = { viewModel.login(loginEmail, loginPassword) },
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeResult(uiState = uiState.loginState)
    }
}

@Composable
private fun HomeTextField(
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
private fun HomeButton(
    label: String,
    containerColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(52.dp)
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
private fun HomeResult(
    uiState: AuthRequestState,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        is AuthRequestState.Idle -> {}

        is AuthRequestState.Loading -> {
            Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        }

        is AuthRequestState.Success -> {
            Text(
                text = "성공: ${uiState.response.nickname} / token: ${uiState.response.token}",
                style = LocalAppTypography.current.bodySmall.medium,
                color = Natural50,
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
