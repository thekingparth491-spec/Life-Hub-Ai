package com.example.ui.tools.homefamily

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.LifeHubApplication
import com.example.data.local.entity.ApplianceWarrantyEntity
import com.example.data.local.entity.GroceryItemEntity
import com.example.ui.components.LifeHubTopAppBar
import kotlinx.coroutines.launch

@Composable
fun GroceryListScreen(onBack: () -> Unit) {
    val repository = remember { LifeHubApplication.instance.repository }
    val scope = rememberCoroutineScope()
    val groceries by repository.getAllGroceries().collectAsStateWithLifecycle(initialValue = emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemQty by remember { mutableStateOf("1 kg") }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Family Grocery List", canNavigateBack = true, onNavigateBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Pantry & Shopping Checklist (${groceries.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            if (groceries.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No items on list! Tap + to add milk, fruits, veggies...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(groceries, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = item.isBought,
                                    onCheckedChange = { checked ->
                                        scope.launch { repository.updateGrocery(item.copy(isBought = checked)) }
                                    }
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.name, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                                    Text("Qty: ${item.quantity} • ${item.category}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = { scope.launch { repository.deleteGrocery(item) } }) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Grocery Item") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = itemName, onValueChange = { itemName = it }, label = { Text("Item Name (e.g. Olive Oil, Almonds)") })
                    OutlinedTextField(value = itemQty, onValueChange = { itemQty = it }, label = { Text("Quantity (e.g. 2 packets, 500g)") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (itemName.isNotBlank()) {
                            scope.launch {
                                repository.addGrocery(GroceryItemEntity(name = itemName, quantity = itemQty))
                            }
                            itemName = ""
                            showDialog = false
                        }
                    }
                ) { Text("Add Item") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun WarrantyTrackerScreen(onBack: () -> Unit) {
    val repository = remember { LifeHubApplication.instance.repository }
    val scope = rememberCoroutineScope()
    val warranties by repository.getAllWarranties().collectAsStateWithLifecycle(initialValue = emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var appliance by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var months by remember { mutableStateOf("24") }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Appliance Warranty Tracker", canNavigateBack = true, onNavigateBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Add Warranty")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Registered Household Appliances (${warranties.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            if (warranties.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No appliances tracked yet. Tap + to record TV, Refrigerator, AC warranties.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(warranties, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(item.applianceName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text("Brand: ${item.brand} • Purchased: ${item.purchaseDate}", style = MaterialTheme.typography.bodySmall)
                                Text("Warranty: ${item.warrantyMonths} Months Active", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Record Appliance Warranty") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = appliance, onValueChange = { appliance = it }, label = { Text("Appliance (e.g. Smart TV)") })
                    OutlinedTextField(value = brand, onValueChange = { brand = it }, label = { Text("Brand (e.g. Sony, LG)") })
                    OutlinedTextField(value = months, onValueChange = { months = it }, label = { Text("Warranty (Months)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val m = months.toIntOrNull() ?: 12
                        if (appliance.isNotBlank()) {
                            scope.launch {
                                repository.addWarranty(ApplianceWarrantyEntity(applianceName = appliance, brand = brand, purchaseDate = "2026-05-10", warrantyMonths = m))
                            }
                            appliance = ""
                            showDialog = false
                        }
                    }
                ) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun ElectricityTrackerScreen(onBack: () -> Unit) {
    var units by remember { mutableStateOf("280") }
    var ratePerUnit by remember { mutableStateOf("7.5") }

    val u = units.toDoubleOrNull() ?: 0.0
    val r = ratePerUnit.toDoubleOrNull() ?: 0.0
    val totalBill = u * r

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Electricity Bill Tracker", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = units, onValueChange = { units = it }, label = { Text("Units Consumed (kWh)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = ratePerUnit, onValueChange = { ratePerUnit = it }, label = { Text("Rate per Unit (₹/kWh)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Estimated Electricity Bill", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("₹${"%.2f".format(totalBill)}", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Total 280 units consumption for this billing period", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun FamilyCalendarScreen(onBack: () -> Unit) {
    val events = listOf(
        Triple("Mom's Birthday", "18 Oct 2026", "Family dinner & surprise cake"),
        Triple("Home AC Annual Maintenance", "25 Oct 2026", "Technician visit booked"),
        Triple("Diwali Family Gathering", "12 Nov 2026", "Puja and celebrations at home")
    )

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Family Calendar", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            events.forEach { (name, date, note) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(date, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        }
                        Text(note, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun ImportantDocsScreen(onBack: () -> Unit) {
    val docs = listOf(
        "Aadhaar Card" to "Updated & Verified • PDF available",
        "PAN Card" to "Permanent Account Number linked",
        "Passport" to "Valid till Aug 2031",
        "Health Insurance Policy" to "Family Floater ₹15 Lakhs coverage"
    )

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Important Documents", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Secure Family Documents Index", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            docs.forEach { (doc, status) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(doc, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(status, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
