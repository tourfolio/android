@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.trade.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural70

private val DROPDOWN_MIN_WIDTH = 88.dp

@Composable
fun FilterDropdown(
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier,
    ) {
        Row(
            modifier =
                Modifier
                    .clickable {
                        expanded = true
                    }
                    .padding(
                        start = 5.dp,
                    ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = selectedOption,
                style = LocalAppTypography.current.bodySmall.bold,
                color = Natural70,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
            )

            Image(
                painter =
                    painterResource(
                        id = R.drawable.ic_arrow_down_gray,
                    ),
                contentDescription =
                    if (expanded) {
                        "정렬 목록 닫기"
                    } else {
                        "정렬 목록 열기"
                    },
                modifier =
                    Modifier
                        .size(12.dp)
                        .rotate(
                            if (expanded) {
                                180f
                            } else {
                                0f
                            },
                        ),
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier.widthIn(min = DROPDOWN_MIN_WIDTH),
            offset =
                DpOffset(
                    x = 0.dp,
                    y = 5.dp,
                ),
            containerColor = Natural100,
            tonalElevation = 0.dp,
            shadowElevation = 4.dp,
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = option,
                                style = LocalAppTypography.current.bodyLarge.bold,
                                color =
                                    if (option == selectedOption) {
                                        Natural10
                                    } else {
                                        Natural70
                                    },
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    contentPadding =
                        PaddingValues(
                            horizontal = 8.dp,
                            vertical = 0.dp,
                        ),
                )
            }
        }
    }
}
