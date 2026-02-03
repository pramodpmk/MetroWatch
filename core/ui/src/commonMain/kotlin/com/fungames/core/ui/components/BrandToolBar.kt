package com.fungames.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fungames.core.ui.theme.BrandBlue
import com.fungames.core.ui.theme.BrandWhite

@Composable
fun BrandToolBar(
    title: String,
    navigationIcon: ImageVector? = null,
    onNavigationClick: () -> Unit = {},
    trailingIcon: ImageVector? = null,
    onTrailingClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrandBlue)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (navigationIcon != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = null,
                        tint = BrandWhite
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            DisplayText(
                text = title,
                color = BrandWhite,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )

            if (trailingIcon != null) {
                IconButton(onClick = onTrailingClick) {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        tint = BrandWhite
                    )
                }
            }
        }
        content()
    }
}
