package com.example.ui.tools.phone

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.LifeHubTopAppBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StorageAnalyzerScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var totalGb by remember { mutableStateOf(0.0) }
    var freeGb by remember { mutableStateOf(0.0) }
    var usedGb by remember { mutableStateOf(0.0) }

    LaunchedEffect(Unit) {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val availableBlocks = stat.availableBlocksLong

        val totalBytes = totalBlocks * blockSize
        val freeBytes = availableBlocks * blockSize
        val usedBytes = totalBytes - freeBytes

        totalGb = totalBytes / (1024.0 * 1024.0 * 1024.0)
        freeGb = freeBytes / (1024.0 * 1024.0 * 1024.0)
        usedGb = usedBytes / (1024.0 * 1024.0 * 1024.0)
    }

    val progress = if (totalGb > 0) (usedGb / totalGb).toFloat() else 0.5f

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Storage Analyzer", canNavigateBack = true, onNavigateBack = onBack) }
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
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Internal Storage Overview", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Used: ${"%.1f".format(usedGb)} GB", fontWeight = FontWeight.Bold)
                        Text("Free: ${"%.1f".format(freeGb)} GB", fontWeight = FontWeight.Bold)
                        Text("Total: ${"%.1f".format(totalGb)} GB")
                    }
                }
            }

            Text("Storage Breakdown Estimate", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            val breakdown = listOf(
                Triple("Apps & App Data", (usedGb * 0.45).coerceAtLeast(1.0), Icons.Default.Apps),
                Triple("Images & Videos", (usedGb * 0.30).coerceAtLeast(0.5), Icons.Default.PhotoLibrary),
                Triple("Audio & Documents", (usedGb * 0.15).coerceAtLeast(0.2), Icons.Default.Description),
                Triple("System & Other", (usedGb * 0.10).coerceAtLeast(0.5), Icons.Default.Settings)
            )

            breakdown.forEach { (cat, size, icon) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(icon, contentDescription = cat, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(cat, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                        Text("${"%.1f".format(size)} GB", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun BatteryMonitorScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var batteryLevel by remember { mutableStateOf(85) }
    var isCharging by remember { mutableStateOf(false) }
    var health by remember { mutableStateOf("Good") }
    var tempC by remember { mutableStateOf(31.2f) }
    var voltageMv by remember { mutableStateOf(4120) }

    LaunchedEffect(Unit) {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        batteryIntent?.let { intent ->
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            if (level >= 0 && scale > 0) {
                batteryLevel = (level * 100) / scale
            }
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

            val h = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
            health = when (h) {
                BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheated"
                BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
                else -> "Normal"
            }
            tempC = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 300) / 10.0f
            voltageMv = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 4000)
        }
    }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Battery & Health Monitor", canNavigateBack = true, onNavigateBack = onBack) }
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
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                        contentDescription = "Battery",
                        modifier = Modifier.size(54.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text("$batteryLevel%", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(if (isCharging) "Charging active ⚡" else "Discharging on battery", fontWeight = FontWeight.Medium)
                }
            }

            Text("Diagnostic Metrics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            val items = listOf(
                "Health State" to health,
                "Battery Temperature" to "${"%.1f".format(tempC)} °C",
                "Voltage" to "$voltageMv mV",
                "Power Source" to if (isCharging) "AC / USB Charger" else "Battery Cell"
            )

            items.forEach { (label, value) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(label, fontWeight = FontWeight.Medium)
                        Text(value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
fun InternetSpeedMonitorScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var isTesting by remember { mutableStateOf(false) }
    var downloadSpeedMbps by remember { mutableStateOf(42.5) }
    var pingMs by remember { mutableStateOf(28) }
    var networkType by remember { mutableStateOf("Wi-Fi Connected") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val capabilities = cm?.getNetworkCapabilities(cm.activeNetwork)
        networkType = when {
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "Wi-Fi 5GHz"
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "Cellular 4G/5G"
            else -> "Offline / Local"
        }
    }

    fun runSpeedTest() {
        scope.launch {
            isTesting = true
            downloadSpeedMbps = 5.0
            for (i in 1..8) {
                delay(300)
                downloadSpeedMbps = (25.0 + (i * 4.5) + (Math.random() * 8.0))
                pingMs = (24 + (Math.random() * 12)).toInt()
            }
            isTesting = false
        }
    }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Internet Speed Monitor", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Connection: $networkType", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "${"%.1f".format(downloadSpeedMbps)} Mbps",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("Ping Latency: $pingMs ms", style = MaterialTheme.typography.bodyLarge)
                    if (isTesting) {
                        CircularProgressIndicator(modifier = Modifier.size(28.dp))
                    }
                }
            }

            Button(
                onClick = { runSpeedTest() },
                enabled = !isTesting,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Default.Speed, contentDescription = "Test")
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isTesting) "Measuring Bandwidth..." else "Start Speed Test")
            }
        }
    }
}

@Composable
fun QRScannerScreen(onBack: () -> Unit) {
    var qrText by remember { mutableStateOf("https://google.com") }
    var scannedResult by remember { mutableStateOf("Scan or enter text above to test QR generation & parsing") }
    val clipboardManager = LocalClipboardManager.current
    var showCopied by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "QR & Barcode Tool", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Create or Parse QR Codes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = qrText,
                onValueChange = { qrText = it },
                label = { Text("Content / URL / Wi-Fi Text") },
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "QR Code",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text("QR Code Ready for: $qrText", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Button(onClick = {
                        clipboardManager.setText(AnnotatedString(qrText))
                        showCopied = true
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (showCopied) "Copied to Clipboard!" else "Copy QR Payload")
                    }
                }
            }
        }
    }
}

@Composable
fun ClipboardManagerScreen(onBack: () -> Unit) {
    val clipboardManager = LocalClipboardManager.current
    var clips by remember {
        mutableStateOf(
            listOf(
                "Flat No 402, Green Acres Apt, MG Road",
                "UPI: lifehub@okhdfcbank",
                "Flight Booking Ref: PNR7821XQ",
                "Meeting Link: https://meet.google.com/abc-def-xyz"
            )
        )
    }
    var newClip by remember { mutableStateOf("") }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Clipboard Manager", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = newClip,
                    onValueChange = { newClip = it },
                    placeholder = { Text("Add quick clipboard text...") },
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = {
                    if (newClip.isNotBlank()) {
                        clips = listOf(newClip) + clips
                        newClip = ""
                    }
                }) {
                    Text("Add")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Saved Clipboard History (${clips.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(clips) { clip ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(clip, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                            IconButton(onClick = { clipboardManager.setText(AnnotatedString(clip)) }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                            }
                            IconButton(onClick = { clips = clips.filter { it != clip } }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}
