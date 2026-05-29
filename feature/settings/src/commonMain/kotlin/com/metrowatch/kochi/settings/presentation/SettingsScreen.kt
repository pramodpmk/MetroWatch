package com.metrowatch.kochi.settings.presentation

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
import com.metrowatch.kochi.navigation.Route
import com.metrowatch.kochi.ui.components.AppScaffold
import com.metrowatch.kochi.ui.components.BrandToolBar
import com.metrowatch.kochi.ui.theme.BrandBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigate: (Route) -> Unit = {}
) {
    val uriHandler = LocalUriHandler.current

    AppScaffold(
        toolBar = {
            BrandToolBar(
                title = "Settings"
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
                        title = "Give feedback",
                        icon = Icons.Outlined.Email,
                        onClick = {
                            try {
                                uriHandler.openUri("mailto:inbox.metrowatch@gmail.com")
                            } catch (_: Exception) {
                                // No mail app installed — silently ignore
                            }
                        }
                    )
                    SettingsItem(
                        title = "Rate us",
                        icon = Icons.Outlined.Star,
                        onClick = { uriHandler.openUri("https://play.google.com/store/apps/details?id=com.metrowatch.kochi") }
                    )
                    SettingsItem(
                        title = "Share app",
                        icon = Icons.Outlined.Share,
                        onClick = {
                            uriHandler.openUri("https://example.com/share?text=Check out this app!")
                        }
                    )
                }

                SettingsSection(title = "Information") {
                    SettingsItem(
                        title = "Policy & guidelines",
                        icon = Icons.Outlined.Lock,
                        onClick = {
                            onNavigate(
                                Route.WebView(
                                    title = "Policy & guidelines",
                                    url = "https://dk9nc3xontwyb.cloudfront.net/privacy"
                                )
                            )
                        }
                    )
                    SettingsItem(
                        title = "Terms & conditions",
                        icon = Icons.Outlined.Lock,
                        onClick = {
                            onNavigate(
                                Route.WebView(
                                    title = "Terms & conditions",
                                    url = "https://dk9nc3xontwyb.cloudfront.net/tnC"
                                )
                            )
                        }
                    )
                    SettingsItem(
                        title = "About us",
                        icon = Icons.Outlined.Info,
                        onClick = { uriHandler.openUri("https://dk9nc3xontwyb.cloudfront.net/aboutUs") }
                    )
                    ListItem(
                        headlineContent = { Text("Version") },
                        supportingContent = { Text("1.0.0") },
                        leadingContent = {
                            Icon(
                                Icons.Outlined.Verified,
                                contentDescription = null,
                                tint = BrandBlue
                            )
                        }
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
            color = BrandBlue,
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
        leadingContent = { Icon(icon, contentDescription = null, tint = BrandBlue) },
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}
