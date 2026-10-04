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
    val policies = listOf(
        Triple("Car Comprehensive Cover", "ICICI Lombard", "Expires: 14 Dec 2026 (72 days left)"),
        Triple("Bike Two-Wheeler Insurance", "Acko General Insurance", "Expires: 22 Feb 2027 (142 days left)")
    )

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Vehicle Insurance Reminder", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            policies.forEach { (title, provider, expiry) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(provider, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(expiry, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
fun PUCReminderScreen(onBack: () -> Unit) {
    val pucList = listOf(
        Pair("Hyundai Creta (DL-08-AB-1234)", "PUC Valid till 19 Nov 2026 (Green Status)"),
        Pair("Royal Enfield Hunter (DL-03-XY-9876)", "PUC Valid till 02 Jan 2027 (Green Status)")
    )

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "PUC Reminder", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            pucList.forEach { (veh, validity) ->
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
                    }
                }
            }
        }
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
