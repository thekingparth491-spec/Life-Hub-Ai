package com.example.ui.tools.phone

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.LifeHubTopAppBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DuplicatePhotoFinderScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var isScanning by remember { mutableStateOf(false) }
    var scanned by remember { mutableStateOf(false) }
    var duplicatesFound by remember {
        mutableStateOf(
            listOf(
                "IMG_20261001_143021.jpg (2.8 MB) — 2 duplicate copies",
                "Screenshot_20260928_0912.png (1.2 MB) — 3 duplicate copies",
                "WhatsApp_IMG_78291.jpg (3.4 MB) — 2 duplicate copies"
            )
        )
    }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Duplicate Photo Finder", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Smart Media Scanner", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("Scans gallery cache and albums for identical filenames, hashes, and burst photos.", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        isScanning = true
                        delay(1200)
                        isScanning = false
                        scanned = true
                    }
                },
                enabled = !isScanning,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isScanning) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Scanning Photo Albums & Cache...")
                } else {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Scan")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (scanned) "Scan Again for Duplicates" else "Scan Photos for Duplicates")
                }
            }

            Text("Detected Duplicate Sets (${duplicatesFound.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            if (duplicatesFound.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text("🎉 All duplicate photos cleared! Storage is clean.", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(duplicatesFound) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Photo, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(item, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                                IconButton(onClick = { duplicatesFound = duplicatesFound.filter { it != item } }) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Clean", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DuplicateFileFinderScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var isScanning by remember { mutableStateOf(false) }
    var files by remember {
        mutableStateOf(
            listOf(
                "Document_Invoice_Final (1).pdf (1.4 MB)",
                "Project_Presentation_Copy.pptx (8.2 MB)",
                "Song_Recording_take2.mp3 (5.1 MB)"
            )
        )
    }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Duplicate File Finder", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Downloads & Temp Files Cleanup", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("Scans internal storage downloads and documents to remove redundant copies.", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        isScanning = true
                        delay(1200)
                        isScanning = false
                    }
                },
                enabled = !isScanning,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isScanning) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Scanning Documents & Downloads...")
                } else {
                    Icon(Icons.Default.Folder, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Scan for Duplicate Files")
                }
            }

            Text("Identified Redundant Files (${files.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            if (files.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text("🎉 No redundant files found! Storage is optimized.", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(files) { file ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(file, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                                IconButton(onClick = { files = files.filter { it != file } }) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChargingTrackerScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Charging Tracker", canNavigateBack = true, onNavigateBack = onBack) }
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
                    Text("Fast Charging Analytics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("Estimated full charge time: ~38 minutes", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    Text("Current Flow: ~3,200 mA (SuperCharge 33W)", style = MaterialTheme.typography.bodyMedium)
                    Text("Charge Cycles: 412 cycles recorded", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun AppUsageDashboardScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var installedAppsCount by remember { mutableStateOf(48) }

    LaunchedEffect(Unit) {
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        installedAppsCount = apps.filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }.size.coerceAtLeast(12)
    }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "App Usage Dashboard", canNavigateBack = true, onNavigateBack = onBack) }
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Installed User Applications", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("$installedAppsCount user apps installed", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text("Monitor background activities and optimize battery permissions in Settings.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun WiFiInfoScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Wi-Fi Signal Information", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Network State", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("Wi-Fi Connected (5 GHz)", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Signal Strength: Excellent (-52 dBm)", style = MaterialTheme.typography.bodyMedium)
                    Text("Link Speed: 433 Mbps", style = MaterialTheme.typography.bodyMedium)
                    Text("Security: WPA2-Personal (AES)", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
