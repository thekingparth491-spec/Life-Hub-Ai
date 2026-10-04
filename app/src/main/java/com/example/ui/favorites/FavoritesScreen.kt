package com.example.ui.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.LifeHubApplication
import com.example.domain.model.Tool
import com.example.ui.components.LifeHubTopAppBar
import com.example.ui.components.ToolCard
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(
    onToolClick: (Tool) -> Unit,
    onExploreTools: () -> Unit
) {
    val repository = remember { LifeHubApplication.instance.repository }
    val scope = rememberCoroutineScope()
    val favorites by repository.getFavoriteTools().collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        topBar = {
            LifeHubTopAppBar(title = "My Favorites")
        }
    ) { padding ->
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "No Favorites Yet",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tap the heart icon on any tool from Categories or All Tools to keep your most-used tools pinned right here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(onClick = onExploreTools) {
                        Icon(Icons.Default.Widgets, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Browse All 100 Tools")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Pinned Quick Shortcuts (${favorites.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                items(favorites, key = { it.id }) { tool ->
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
}
