package com.fastflow.app.presentation.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fastflow.app.R
import com.fastflow.app.domain.model.*
import com.fastflow.app.domain.repository.CommunityFeedFilter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var filter by remember { mutableStateOf(CommunityFeedFilter.ALL) }
    var group by remember { mutableStateOf<CommunityGroup?>(null) }

    LaunchedEffect(filter) { viewModel.setFilter(filter) }
    LaunchedEffect(group) { viewModel.setGroup(group) }

    if (uiState.showProfileDialog) {
        ProfileSetupDialog(
            profile = uiState.profile,
            onDismiss = viewModel::hideProfileDialog,
            onSave = viewModel::saveProfile
        )
    }

    if (uiState.showComposeDialog) {
        ComposePostDialog(
            onDismiss = viewModel::hideComposeDialog,
            onPublish = viewModel::publishPost
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.community_title), fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = viewModel::showProfileDialog) {
                        Icon(Icons.Default.Person, contentDescription = stringResource(R.string.community_profile))
                    }
                    IconButton(
                        onClick = { viewModel.shareProgress() },
                        enabled = uiState.profile.isSetupComplete
                    ) {
                        Icon(Icons.Default.Share, contentDescription = stringResource(R.string.community_share_progress))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (uiState.profile.isSetupComplete) {
                        viewModel.showComposeDialog()
                    } else {
                        viewModel.showProfileDialog()
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.community_compose))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!uiState.profile.isSetupComplete) {
                item {
                    ProfilePromptCard(onSetup = viewModel::showProfileDialog)
                }
            }

            uiState.error?.let { error ->
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            item {
                Text(
                    stringResource(R.string.community_sync_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = filter == CommunityFeedFilter.ALL,
                            onClick = { filter = CommunityFeedFilter.ALL },
                            label = { Text(stringResource(R.string.community_filter_all)) }
                        )
                    }
                    item {
                        FilterChip(
                            selected = filter == CommunityFeedFilter.MOTIVATION,
                            onClick = { filter = CommunityFeedFilter.MOTIVATION },
                            label = { Text(stringResource(R.string.community_filter_motivation)) }
                        )
                    }
                    item {
                        FilterChip(
                            selected = filter == CommunityFeedFilter.MEAL_IDEA,
                            onClick = { filter = CommunityFeedFilter.MEAL_IDEA },
                            label = { Text(stringResource(R.string.community_filter_meals)) }
                        )
                    }
                    item {
                        FilterChip(
                            selected = filter == CommunityFeedFilter.PROGRESS,
                            onClick = { filter = CommunityFeedFilter.PROGRESS },
                            label = { Text(stringResource(R.string.community_filter_progress)) }
                        )
                    }
                }
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = group == null,
                            onClick = { group = null },
                            label = { Text(stringResource(R.string.community_group_all)) }
                        )
                    }
                    CommunityGroup.entries.forEach { g ->
                        item {
                            FilterChip(
                                selected = group == g,
                                onClick = { group = g },
                                label = { Text(groupLabel(g)) }
                            )
                        }
                    }
                }
            }

            if (uiState.isLoading) {
                item {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (uiState.posts.isEmpty()) {
                item {
                    Text(
                        stringResource(R.string.community_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            } else {
                items(uiState.posts, key = { it.id }) { post ->
                    CommunityPostCard(
                        post = post,
                        onHide = { viewModel.hidePost(post.id) },
                        onReport = { viewModel.reportPost(post.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfilePromptCard(onSetup: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Groups, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text(
                    stringResource(R.string.community_profile_prompt),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = onSetup) {
                Text(stringResource(R.string.community_setup_profile))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommunityPostCard(
    post: CommunityPost,
    onHide: () -> Unit,
    onReport: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("dd MMM HH:mm", Locale.getDefault()) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(post.authorName, fontWeight = FontWeight.Bold)
                    Text(
                        "${postTypeLabel(post.type)} · ${groupLabel(post.group)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = null)
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.community_hide)) },
                            onClick = { menuExpanded = false; onHide() }
                        )
                        if (!post.isOwnPost) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.community_report)) },
                                onClick = { menuExpanded = false; onReport() }
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(post.content, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                dateFormat.format(Date(post.createdAt)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun ProfileSetupDialog(
    profile: CommunityProfile,
    onDismiss: () -> Unit,
    onSave: (String, Boolean) -> Unit
) {
    var name by remember(profile.displayName) { mutableStateOf(profile.displayName) }
    var anonymous by remember(profile.shareAnonymously) { mutableStateOf(profile.shareAnonymously) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.community_profile_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.community_display_name)) },
                    singleLine = true
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = anonymous, onCheckedChange = { anonymous = it })
                    Text(stringResource(R.string.community_anonymous))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name, anonymous) },
                enabled = name.trim().length >= 2
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComposePostDialog(
    onDismiss: () -> Unit,
    onPublish: (CommunityPostType, CommunityGroup, String) -> Unit
) {
    var content by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(CommunityPostType.MOTIVATION) }
    var group by remember { mutableStateOf(CommunityGroup.GENERAL) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.community_compose_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CommunityPostType.entries.forEach { t ->
                        FilterChip(
                            selected = type == t,
                            onClick = { type = t },
                            label = { Text(postTypeLabel(t)) }
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CommunityGroup.entries.forEach { g ->
                        FilterChip(
                            selected = group == g,
                            onClick = { group = g },
                            label = { Text(groupLabel(g)) }
                        )
                    }
                }
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.community_compose_hint)) },
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onPublish(type, group, content) },
                enabled = content.trim().length >= 3
            ) {
                Text(stringResource(R.string.community_publish))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun postTypeLabel(type: CommunityPostType): String = when (type) {
    CommunityPostType.MOTIVATION -> stringResource(R.string.community_type_motivation)
    CommunityPostType.MEAL_IDEA -> stringResource(R.string.community_type_meal)
    CommunityPostType.PROGRESS -> stringResource(R.string.community_type_progress)
}

@Composable
private fun groupLabel(group: CommunityGroup): String = when (group) {
    CommunityGroup.GENERAL -> stringResource(R.string.community_group_general)
    CommunityGroup.RAMADAN -> stringResource(R.string.community_group_ramadan)
    CommunityGroup.SUMMER -> stringResource(R.string.community_group_summer)
}
