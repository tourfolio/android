@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Primary

@Composable
fun CommonInfoModal(
    message: String,
    onDismissRequest: () -> Unit,
    onSettingsClick: (() -> Unit)? = null,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = Natural100,
        icon = {
            Image(painterResource(R.drawable.ic_info), contentDescription = null, modifier = Modifier.size(48.dp))
        },
        text = {
            Text(
                message,
                modifier = Modifier.fillMaxWidth(),
                style = LocalAppTypography.current.bodyLarge.medium,
                color = Natural10,
                textAlign = TextAlign.Center,
            )
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                if (onSettingsClick != null) {
                    TextButton(onClick = onDismissRequest) { Text("닫기", color = Natural10) }
                }
                TextButton(onClick = onSettingsClick ?: onDismissRequest) {
                    Text(if (onSettingsClick != null) "설정으로 이동" else "확인", color = Primary)
                }
            }
        },
    )
}
