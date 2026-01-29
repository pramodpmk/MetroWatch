package com.fungames.feature.settings.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.fungames.core.ui.components.AppScaffold
import com.fungames.core.ui.components.DisplayText

@Composable
fun SettingsScreen() {
    val uriHandler = LocalUriHandler.current

    AppScaffold(
        toolBar = { SettingsToolBar() },
        bottomBar = {  },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Spacer(Modifier.height(64.dp))

                SettingsItem(
                    title = "Give Feedback",
                    icon = Icons.Outlined.Email,
                    onClick = { uriHandler.openUri("mailto:feedback@example.com") }
                )
                SettingsItem(
                    title = "Policy & Guidelines",
                    icon = Icons.Outlined.Lock,
                    onClick = { uriHandler.openUri("https://example.com/policy") }
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
                        // For now, use uriHandler to "share" via a link or just a placeholder
                        // Ideally this would trigger a platform share sheet
                        uriHandler.openUri("https://example.com/share?text=Check out this app!")
                    }
                )

                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))

                SettingsItem(
                    title = "About Us",
                    icon = Icons.Outlined.Info,
                    onClick = { uriHandler.openUri("https://example.com/about") }
                )

                ListItem(
                    headlineContent = { Text("Version") },
                    supportingContent = { Text("1.0.0") },
                    leadingContent = { Icon(Icons.Outlined.Info, contentDescription = null) }
                )
            }
        }
    )
}

@Composable
fun SettingsToolBar() {
    Row(modifier = Modifier.padding(16.dp)) {
        DisplayText(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium
        )
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
        modifier = Modifier.clickable(onClick = onClick)
    )
}
