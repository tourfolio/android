@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural60

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "관광지를 입력해주세요",
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(30.dp),
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFFBDBDBD),
                    shape = RoundedCornerShape(30.dp),
                )
                .padding(
                    start = 20.dp,
                    end = 18.dp,
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style =
                        LocalAppTypography.current.bodyLarge.medium.copy(
                            color = Natural60,
                        ),
                )
            }

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle =
                    LocalAppTypography.current.bodyLarge.medium.copy(
                        color = Natural10,
                    ),
                cursorBrush = SolidColor(Natural10),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Image(
            painter = painterResource(id = R.drawable.ic_search_gray),
            contentDescription = "검색",
            modifier = Modifier.size(26.dp),
        )
    }
}