package com.example.ui.categories

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.LifeHubApplication
import com.example.domain.model.Category
import com.example.domain.model.Tool
import com.example.domain.model.ToolRegistry
import com.example.ui.components.IconHelper
import com.example.ui.components.LifeHubTopAppBar
import com.example.ui.components.ToolCard
import kotlinx.coroutines.launch

@Composable
fun CategoriesScreen(
    onCategoryClick: (Category) -> Unit,
    onNavigateToAllTools: () -> Unit
) {
    val categories = ToolRegistry.categories

    Scaffold(
        topBar = {
            LifeHubTopAppBar(
                title = "Categories",
                actions = {
                    TextButton(onClick = onNavigateToAllTools) {
                        Text("All 100 Tools", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Explore 10 Domains",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Every domain is packed with real, functional utilities designed for your daily routine.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(categories, key = { it.id }) { cat ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onCategoryClick(cat) }
                        .testTag("category_card_${cat.id}"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = IconHelper.getIcon(cat.iconName),
                                    contentDescription = cat.name,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = cat.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = cat.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = "${cat.toolCount} Tools",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun CategoryDetailScreen(
    categoryId: String,
    onToolClick: (Tool) -> Unit,
    onBack: () -> Unit
) {
    val repository = remember { LifeHubApplication.instance.repository }
    val scope = rememberCoroutineScope()
    val category = ToolRegistry.categories.find { it.id == categoryId } ?: ToolRegistry.categories.first()
    val tools by repository.getToolsByCategory(categoryId).collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        topBar = {
            LifeHubTopAppBar(
                title = category.name,
                canNavigateBack = true,
                onNavigateBack = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = IconHelper.getIcon(category.iconName),
                                    contentDescription = category.name,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(category.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text(category.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(tools, key = { it.id }) { tool ->
                ToolCard(
                    tool = tool,
                    onToolClick = {
                        scope.launch { repository.recordToolUsage(tool.id) }
                        onToolClick(tool)
                    },
                    onFavoriteToggle = {
                        scope.launch { repository.toggleFavorite(tool.id, !tool.isFavorite) }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun AllToolsScreen(
    onToolClick: (Tool) -> Unit,
    onBack: () -> Unit
) {
    val repository = remember { LifeHubApplication.instance.repository }
    val scope = rememberCoroutineScope()
    val allTools by repository.getAllTools().collectAsStateWithLifecycle(initialValue = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("all") }
    var isGridView by remember { mutableStateOf(false) }

    val filteredTools = remember(allTools, searchQuery, selectedCategoryFilter) {
        allTools.filter { tool ->
            val matchesCategory = selectedCategoryFilter == "all" || tool.categoryId == selectedCategoryFilter
            val matchesQuery = searchQuery.isBlank() || tool.name.contains(searchQuery, ignoreCase = true) || tool.description.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    Scaffold(
        topBar = {
            LifeHubTopAppBar(
                title = "All 100 Tools",
                canNavigateBack = true,
                onNavigateBack = onBack,
                actions = {
                    IconButton(onClick = { isGridView = !isGridView }) {
                        Icon(
                            imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                            contentDescription = "Toggle Grid/List View"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search all 100 tools...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("all_tools_search"),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category Filter Scrollable Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategoryFilter == "all",
                    onClick = { selectedCategoryFilter = "all" },
                    label = { Text("All (${allTools.size})") }
                )
                ToolRegistry.categories.take(3).forEach { cat ->
                    FilterChip(
                        selected = selectedCategoryFilter == cat.id,
                        onClick = { selectedCategoryFilter = if (selectedCategoryFilter == cat.id) "all" else cat.id },
                        label = { Text(cat.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Showing ${filteredTools.size} Tools", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))

            if (!isGridView) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filteredTools, key = { it.id }) { tool ->
                        ToolCard(
                            tool = tool,
                            onToolClick = {
                                scope.launch { repository.recordToolUsage(tool.id) }
                                onToolClick(tool)
                            },
                            onFavoriteToggle = {
                                scope.launch { repository.toggleFavorite(tool.id, !tool.isFavorite) }
                            }
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredTools, key = { it.id }) { tool ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    scope.launch { repository.recordToolUsage(tool.id) }
                                    onToolClick(tool)
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = IconHelper.getIcon(tool.iconName),
                                        contentDescription = tool.name,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    IconButton(
                                        onClick = { scope.launch { repository.toggleFavorite(tool.id, !tool.isFavorite) } },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (tool.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = null,
                                            tint = if (tool.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Column {
                                    Text(tool.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.titleSmall)
                                    Text(tool.description, maxLines = 2, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
