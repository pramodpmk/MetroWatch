package com.fungames.core.station.presentation.contact

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fungames.core.ui.components.AppScaffold
import com.fungames.core.ui.components.BrandToolBar
import com.fungames.core.ui.components.DisplayText
import com.fungames.core.ui.rememberPlatformActions

@Composable
fun ContactsScreen(
    state: ContactsUiState,
    navController: NavHostController
) {
    val platformActions = rememberPlatformActions()
    AppScaffold(
        toolBar = {
            BrandToolBar(
                title = "Emergency Contacts",
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.isError) {
                DisplayText(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else {
                val groupedContacts = state.contacts.groupBy { it.category }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    groupedContacts.forEach { (category, contacts) ->
                        item {
                            DisplayText(
                                text = category,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        items(contacts) { contact ->
                            ContactItem(
                                contact = contact,
                                onClick = {
                                    platformActions.showToast("Opening: ${contact.value}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContactItem(
    contact: com.fungames.core.station.domain.Contact,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        ListItem(
            headlineContent = { DisplayText(contact.name) },
            supportingContent = { DisplayText(contact.value) },
            trailingContent = {
                Row {
                    val isUrl = contact.value.startsWith("http")
                    IconButton(onClick = onClick) {
                        Icon(
                            imageVector = if (isUrl) Icons.Default.Public else Icons.Default.Call,
                            contentDescription = if (isUrl) "Open" else "Call",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        )
    }
}
