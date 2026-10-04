package com.example.ui.home

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.ui.components.QuickActionChip
import com.example.ui.components.ToolCard
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    onNavigateToCategory: (Category) -> Unit,
    onNavigateToTool: (Tool) -> Unit,
    onNavigateToToolRoute: (String) -> Unit,
    onNavigateToAllTools: () -> Unit,
    onNavigateToAI: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { LifeHubApplication.instance.repository }
    val scope = rememberCoroutineScope()

    val favorites by repository.getFavoriteTools().collectAsStateWithLifecycle(initialValue = emptyList())
    val recentlyUsed by repository.getRecentlyUsedTools().collectAsStateWithLifecycle(initialValue = emptyList())
    val totalExpense by repository.getTotalExpenses().collectAsStateWithLifecycle(initialValue = 0.0)

    var batteryPercent by remember { mutableStateOf(84) }
    var isCharging by remember { mutableStateOf(false) }
    var storageFreeGb by remember { mutableStateOf(42.8) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        batteryIntent?.let { intent ->
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            if (level >= 0 && scale > 0) batteryPercent = (level * 100) / scale
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
        }
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        storageFreeGb = (stat.availableBlocksLong * stat.blockSizeLong) / (1024.0 * 1024.0 * 1024.0)
    }

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 4..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    val currentDateStr = remember {
        SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date())
    }

    val searchResults = remember(searchQuery) {
        if (searchQuery.isBlank()) emptyList()
        else ToolRegistry.allTools.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.description.contains(searchQuery, ignoreCase = true) ||
            it.categoryId.contains(searchQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$greeting ✨",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = currentDateStr,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    modifier = Modifier.clickable { onNavigateToAllTools() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Widgets,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "100 Tools",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Search Bar with live search filter
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search 100 tools, expenses, notes...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_search_bar"),
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
        }

        // Live Search Results Dropdown (if user is searching)
        if (searchQuery.isNotBlank()) {
            item {
                Text(
                    text = "Matching Tools (${searchResults.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (searchResults.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = "No tools matched \"$searchQuery\". Try \"EMI\", \"Unit\", \"Budget\", or \"Water\".",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                items(searchResults, key = { "search_${it.id}" }) { tool ->
                    ToolCard(
                        tool = tool,
                        onToolClick = {
                            scope.launch { repository.recordToolUsage(tool.id) }
                            onNavigateToTool(tool)
                        },
                        onFavoriteToggle = {
                            scope.launch { repository.toggleFavorite(tool.id, !tool.isFavorite) }
                        }
                    )
                }
            }
        }

        // Quick Actions Carousel
        if (searchQuery.isBlank()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionChip(title = "Calculator", icon = Icons.Default.Calculate, onClick = { onNavigateToToolRoute("tool_calculator") })
                    QuickActionChip(title = "Expense", icon = Icons.Default.Receipt, onClick = { onNavigateToToolRoute("tool_expense_tracker") })
                    QuickActionChip(title = "QR Scanner", icon = Icons.Default.QrCodeScanner, onClick = { onNavigateToToolRoute("tool_qr_scanner") })
                    QuickActionChip(title = "Pomodoro", icon = Icons.Default.Timer, onClick = { onNavigateToToolRoute("tool_pomodoro_timer") })
                    QuickActionChip(title = "Water", icon = Icons.Default.LocalDrink, onClick = { onNavigateToToolRoute("tool_water_reminder") })
                    QuickActionChip(title = "Emergency SOS", icon = Icons.Default.Warning, onClick = { onNavigateToToolRoute("tool_emergency_sos") })
                }
            }

            // LifeHub AI Hero Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .clickable { onNavigateToAI() }
                        .testTag("home_ai_assistant_card"),
                    shape = RoundedCornerShape(22.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF3730A3),
                                        Color(0xFF4F46E5),
                                        Color(0xFF7C3AED)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(54.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "AI",
                                        tint = Color.White,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "LifeHub AI Assistant",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF10B981)
                                    ) {
                                        Text(
                                            text = "ONLINE",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Ask anything, log expenses, manage study or execute tool shortcuts.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = "Chat",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Real-time Pulse Metrics (Battery, Storage, Expenses)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Battery Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onNavigateToToolRoute("tool_battery_monitor") },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                                    contentDescription = null,
                                    tint = if (batteryPercent > 20) MaterialTheme.colorScheme.primary else Color(0xFFEF4444),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                if (isCharging) {
                                    Text("⚡", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("$batteryPercent%", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                            Text("Battery", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // Storage Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onNavigateToToolRoute("tool_storage_analyzer") },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Icon(Icons.Default.SdStorage, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("${"%.0f".format(storageFreeGb)} GB", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                            Text("Free Storage", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // Expense Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onNavigateToToolRoute("tool_expense_tracker") },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Icon(Icons.Default.Payments, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("₹${"%.0f".format(totalExpense ?: 0.0)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                            Text("Expenses", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Favorites Preview
            if (favorites.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Favorites",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = onNavigateToFavorites) {
                            Text("See All (${favorites.size})")
                        }
                    }
                }

                items(favorites.take(3), key = { "fav_${it.id}" }) { tool ->
                    ToolCard(
                        tool = tool,
                        onToolClick = {
                            scope.launch { repository.recordToolUsage(tool.id) }
                            onNavigateToTool(tool)
                        },
                        onFavoriteToggle = {
                            scope.launch { repository.toggleFavorite(tool.id, !tool.isFavorite) }
                        }
                    )
                }
            }

            // Recently Used Tools
            if (recentlyUsed.isNotEmpty()) {
                item {
                    Text(
                        text = "Recently Used",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(recentlyUsed.take(3), key = { "recent_${it.id}" }) { tool ->
                    ToolCard(
                        tool = tool,
                        onToolClick = {
                            scope.launch { repository.recordToolUsage(tool.id) }
                            onNavigateToTool(tool)
                        },
                        onFavoriteToggle = {
                            scope.launch { repository.toggleFavorite(tool.id, !tool.isFavorite) }
                        }
                    )
                }
            }

            // Categories Preview Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tool Categories",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onNavigateToAllTools) {
                        Text("All 100 Tools")
                    }
                }
            }

            items(ToolRegistry.categories.take(5), key = { "cat_${it.id}" }) { cat ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onNavigateToCategory(cat) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = IconHelper.getIcon(cat.iconName),
                                    contentDescription = cat.name,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(cat.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("${cat.toolCount} tools • ${cat.description}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Emergency Quick Action Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onNavigateToToolRoute("tool_emergency_sos") }
                        .testTag("home_emergency_sos_banner"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(46.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.error
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Shield, contentDescription = "SOS", tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Emergency SOS & Safety", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onErrorContainer)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.error) {
                                    Text("112 / 108", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                            Text("Instant 1-tap call, strobe alert, siren & GPS broadcast", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f))
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
