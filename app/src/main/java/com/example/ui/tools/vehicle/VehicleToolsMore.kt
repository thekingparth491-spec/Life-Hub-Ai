package com.example.ui.tools.vehicle

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
fun InsuranceReminderScreen(onBack: () -> Unit) {
    var policies by remember {
        mutableStateOf(
            listOf(
                Triple("Car Comprehensive Cover", "ICICI Lombard", "Expires: 14 Dec 2026 (72 days left)"),
                Triple("Bike Two-Wheeler Insurance", "Acko General Insurance", "Expires: 22 Feb 2027 (142 days left)")
            )
        )
    }
    var showDialog by remember { mutableStateOf(false) }
    var policyTitle by remember { mutableStateOf("") }
    var providerName by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Vehicle Insurance Reminder", canNavigateBack = true, onNavigateBack = onBack) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                text = { Text("Add Policy") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Insurance Policies (${policies.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                FilledTonalButton(onClick = { showDialog = true }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add New", style = MaterialTheme.typography.labelSmall)
                }
            }

            if (policies.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text("No policies saved. Tap '+ Add New' to track vehicle insurance renewal.", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                }
            }

            policies.forEachIndexed { index, (title, provider, expiry) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(provider, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(expiry, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = {
                            policies = policies.filterIndexed { i, _ -> i != index }
                        }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Insurance Policy Reminder") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = policyTitle,
                        onValueChange = { policyTitle = it },
                        label = { Text("Policy Name / Vehicle") },
                        placeholder = { Text("e.g. Maruti Swift Zero-Dep") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = providerName,
                        onValueChange = { providerName = it },
                        label = { Text("Insurance Company") },
                        placeholder = { Text("e.g. HDFC ERGO, Bajaj Allianz") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = expiryDate,
                        onValueChange = { expiryDate = it },
                        label = { Text("Expiry / Renewal Date") },
                        placeholder = { Text("e.g. Expires: 15 Jan 2027") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (policyTitle.isNotBlank()) {
                            val prov = if (providerName.isBlank()) "General Insurance" else providerName.trim()
                            val exp = if (expiryDate.isBlank()) "Renewal date pending" else if (!expiryDate.startsWith("Expires")) "Expires: $expiryDate" else expiryDate
                            policies = policies + Triple(policyTitle.trim(), prov, exp)
                            policyTitle = ""
                            providerName = ""
                            expiryDate = ""
                            showDialog = false
                        }
                    },
                    enabled = policyTitle.isNotBlank()
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun PUCReminderScreen(onBack: () -> Unit) {
    var pucList by remember {
        mutableStateOf(
            listOf(
                Pair("Hyundai Creta (DL-08-AB-1234)", "PUC Valid till 19 Nov 2026 (Green Status)"),
                Pair("Royal Enfield Hunter (DL-03-XY-9876)", "PUC Valid till 02 Jan 2027 (Green Status)")
            )
        )
    }
    var showDialog by remember { mutableStateOf(false) }
    var vehicleName by remember { mutableStateOf("") }
    var validityDate by remember { mutableStateOf("") }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "PUC Reminder", canNavigateBack = true, onNavigateBack = onBack) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                text = { Text("Add PUC") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("PUC Certificates (${pucList.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                FilledTonalButton(onClick = { showDialog = true }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add New", style = MaterialTheme.typography.labelSmall)
                }
            }

            if (pucList.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text("No PUC certificates logged. Tap '+ Add New' to track emission certificate validity.", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                }
            }

            pucList.forEachIndexed { index, (veh, validity) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Eco, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(veh, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text(validity, style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = {
                            pucList = pucList.filterIndexed { i, _ -> i != index }
                        }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add PUC Reminder") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = vehicleName,
                        onValueChange = { vehicleName = it },
                        label = { Text("Vehicle & Number") },
                        placeholder = { Text("e.g. Honda Activa (DL-05-CD-4321)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = validityDate,
                        onValueChange = { validityDate = it },
                        label = { Text("Validity Till Date") },
                        placeholder = { Text("e.g. Valid till 25 Dec 2026") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (vehicleName.isNotBlank()) {
                            val valStr = if (validityDate.isBlank()) "PUC Valid (Active Status)" else if (!validityDate.startsWith("PUC")) "PUC $validityDate" else validityDate
                            pucList = pucList + Pair(vehicleName.trim(), valStr)
                            vehicleName = ""
                            validityDate = ""
                            showDialog = false
                        }
                    },
                    enabled = vehicleName.isNotBlank()
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun VehicleDocumentLockerScreen(onBack: () -> Unit) {
    val docs = listOf(
        "Registration Certificate (RC)" to "DL08AB1234 • Stored Securely",
        "Driving Licence (DL)" to "Valid till 2038 • Stored Securely",
        "Insurance Certificate" to "Policy PDF linked",
        "Pollution Certificate (PUC)" to "Verified Active"
    )

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Vehicle Document Locker", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            docs.forEach { (title, subtitle) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MaintenanceLogScreen(isCar: Boolean, onBack: () -> Unit) {
    val carLogs = listOf(
        "Engine Oil & Filter Replaced" to "28,500 km • Fully Synthetic 5W-30",
        "Wheel Alignment & Balancing" to "25,000 km • All 4 tyres aligned",
        "Front Brake Pads Replaced" to "22,000 km • OEM Genuine pads"
    )
    val bikeLogs = listOf(
        "Chain Clean & Lubrication" to "6,500 km • Motul Chain Paste",
        "Engine Oil Change (15W-50)" to "5,000 km • Castrol Power1",
        "Brake Fluid Top-up" to "4,200 km • DOT4"
    )
    val logs = if (isCar) carLogs else bikeLogs

    Scaffold(
        topBar = { LifeHubTopAppBar(title = if (isCar) "Car Maintenance Log" else "Bike Maintenance Log", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            logs.forEach { (task, notes) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(task, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun VehicleDashboardScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Vehicle Expense Dashboard", canNavigateBack = true, onNavigateBack = onBack) }
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
                    Text("Total Vehicle Spending (This Year)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("₹42,850", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Fuel: ₹28,400 • Service: ₹8,250 • Insurance: ₹6,200", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
