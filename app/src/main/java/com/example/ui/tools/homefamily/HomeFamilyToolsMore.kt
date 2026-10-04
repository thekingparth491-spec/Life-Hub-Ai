package com.example.ui.tools.homefamily

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.LifeHubTopAppBar

@Composable
fun FamilyExpenseScreen(onBack: () -> Unit) {
    val expenses = listOf(
        Triple("Groceries & Provisions", 12400.0, "Shared by Dad"),
        Triple("Electricity & Utility Bills", 3850.0, "Shared by Parth"),
        Triple("House Maintenance", 2200.0, "Shared by Mom")
    )

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Family Expense Manager", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            expenses.forEach { (cat, amt, by) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(cat, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(by, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("₹${"%.0f".format(amt)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
fun HouseholdInventoryScreen(onBack: () -> Unit) {
    val items = listOf(
        "Living Room 55-inch OLED TV" to "Hall • Purchased 2024",
        "Microwave Oven" to "Kitchen • In daily use",
        "Water Purifier (RO)" to "Kitchen • Filter replacement due in 3 months",
        "Ergonomic Study Chair" to "Study Room • Good condition"
    )

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Household Inventory", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items.forEach { (name, loc) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(loc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun HomeMaintenanceScreen(onBack: () -> Unit) {
    val alerts = listOf(
        "AC Filter Cleaning" to "Due in 5 days • Recommended every 60 days",
        "Water Purifier Sediment Filter" to "Due in 25 days • Recommended every 6 months",
        "Chimney Deep Cleaning" to "Due in 40 days • Annual maintenance"
    )

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Home Maintenance Reminder", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            alerts.forEach { (item, schedule) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(item, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(schedule, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun WaterUsageScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Water Usage Tracker", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Daily Household Water Consumption", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("480 Liters", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Within healthy conservation range (~120L / person / day)", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun FamilyTasksScreen(onBack: () -> Unit) {
    var tasks by remember {
        mutableStateOf(
            listOf(
                Pair("Pick up laundry from dry cleaner", true),
                Pair("Water the balcony plants", false),
                Pair("Refill home water can", false),
                Pair("Book AC technician slot", true)
            )
        )
    }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Family Task Manager", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tasks) { (task, completed) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = completed,
                                onCheckedChange = { checked ->
                                    tasks = tasks.map { if (it.first == task) Pair(task, checked) else it }
                                }
                            )
                            Text(task, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}
