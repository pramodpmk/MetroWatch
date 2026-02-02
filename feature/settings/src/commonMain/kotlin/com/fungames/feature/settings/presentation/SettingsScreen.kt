package com.fungames.feature.settings.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.fungames.core.ui.components.AppScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val uriHandler = LocalUriHandler.current

    AppScaffold(
        toolBar = {
            MediumTopAppBar(
                title = { Text("Settings") },
                windowInsets = WindowInsets.statusBars
            )
        },
        bottomBar = { },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                SettingsSection(title = "Support") {
                    SettingsItem(
                        title = "Give Feedback",
                        icon = Icons.Outlined.Email,
                        onClick = { uriHandler.openUri("mailto:feedback@example.com") }
                    )
                    SettingsItem(
                        title = "Rate Us",
                        icon = Icons.Outlined.Star,
                        onClick = { uriHandler.openUri("https://play.google.com/store/apps/details?id=com.fungames.reminderapp") }
                    )
                    SettingsItem(
                        title = "Share App",
                        icon = Icons.Outlined.Share,
                        onClick = {
                            uriHandler.openUri("https://example.com/share?text=Check out this app!")
                        }
                    )
                }

                SettingsSection(title = "Information") {
                    SettingsItem(
                        title = "Policy & Guidelines",
                        icon = Icons.Outlined.Lock,
                        onClick = { uriHandler.openUri("https://example.com/policy") }
                    )
                    SettingsItem(
                        title = "About Us",
                        icon = Icons.Outlined.Info,
                        onClick = { uriHandler.openUri("https://example.com/about") }
                    )
                    ListItem(
                        headlineContent = { Text("Version") },
                        supportingContent = { Text("1.0.0") },
                        leadingContent = { Icon(Icons.Outlined.Verified, contentDescription = null) }
                    )
                }
            }
        }
    )
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        content()
    }
}

@Composable
fun SettingsItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        leadingContent = { Icon(icon, contentDescription = null) },
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}
