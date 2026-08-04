@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Primary

enum class SearchBarStyle {
    DEFAULT,
    ACTIVE,
}

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "관광지를 입력해주세요",
    style: SearchBarStyle = SearchBarStyle.DEFAULT,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null,
    onSearch: () -> Unit = {},
    onFocusChanged: (Boolean) -> Unit = {},
) {
    val accentColor =
        when (style) {
            SearchBarStyle.DEFAULT -> Color(0xFF8B8B8B)
            SearchBarStyle.ACTIVE -> Primary
        }

    val borderColor =
        when (style) {
            SearchBarStyle.DEFAULT -> Color(0xFFB8B8B8)
            SearchBarStyle.ACTIVE -> Primary
        }

    val searchIconRes =
        when (style) {
            SearchBarStyle.DEFAULT -> R.drawable.ic_search_gray
            SearchBarStyle.ACTIVE -> R.drawable.ic_search_primary
        }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    color = Natural100,
                    shape = RoundedCornerShape(30.dp),
                )
                .border(
                    width = 1.2.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(30.dp),
                )
                .then(
                    if (onClick != null) {
                        Modifier.clickable(onClick = onClick)
                    } else {
                        Modifier
                    },
                )
                .padding(
                    start = 20.dp,
                    end = 16.dp,
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
                readOnly = readOnly,
                enabled = onClick == null,
                singleLine = true,
                textStyle =
                    LocalAppTypography.current.bodyLarge.medium.copy(
                        color =
                            if (style == SearchBarStyle.ACTIVE) {
                                accentColor
                            } else {
                                Natural10
                            },
                    ),
                cursorBrush = SolidColor(accentColor),
                keyboardOptions =
                    KeyboardOptions(
                        imeAction = ImeAction.Search,
                    ),
                keyboardActions =
                    KeyboardActions(
                        onSearch = {
                            onSearch()
                        },
                    ),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            onFocusChanged(
                                focusState.isFocused,
                            )
                        },
            )
        }

        Image(
            painter = painterResource(id = searchIconRes),
            contentDescription = "검색",
            modifier =
                Modifier
                    .size(26.dp)
                    .clickable {
                        if (onClick != null) {
                            onClick()
                        } else {
                            onSearch()
                        }
                    },
        )
    }
}
