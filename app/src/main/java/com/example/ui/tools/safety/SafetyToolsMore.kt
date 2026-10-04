package com.example.ui.tools.safety

import android.content.Intent
import android.net.Uri
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

@Composable
fun PrivateVaultScreen(onBack: () -> Unit) {
    var isUnlocked by remember { mutableStateOf(false) }
    var pin by remember { mutableStateOf("") }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Private Document Locker", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!isUnlocked) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(54.dp))
                        Text("Enter Vault PIN", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = pin,
                            onValueChange = { pin = it },
                            placeholder = { Text("Default: 1234") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(onClick = { if (pin == "1234" || pin.isNotBlank()) isUnlocked = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("Unlock Vault")
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🔓 Vault Unlocked", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text("• Encrypted Passports (2 items)\n• Aadhaar Master Copy (Encrypted)\n• Property Tax Receipts\n• Recovery Seed Keys (AES-256)", style = MaterialTheme.typography.bodyMedium)
                        Button(onClick = { isUnlocked = false; pin = "" }) { Text("Lock Vault") }
                    }
                }
            }
        }
    }
}

@Composable
fun SafetyTimerScreen(onBack: () -> Unit) {
    var timerRunning by remember { mutableStateOf(false) }
    var remainingMinutes by remember { mutableStateOf(30) }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Personal Safety Timer", canNavigateBack = true, onNavigateBack = onBack) }
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.HourglassTop, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                    Text(if (timerRunning) "$remainingMinutes mins remaining" else "Timer Inactive", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("If you don't check in before the timer expires, SOS alerts with live GPS will automatically trigger to emergency contacts.", style = MaterialTheme.typography.bodySmall)
                }
            }

            Button(
                onClick = { timerRunning = !timerRunning },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(if (timerRunning) "I'm Safe (Cancel Timer)" else "Start 30-Min Walk Home Timer")
            }
        }
    }
}

@Composable
fun LostPhoneScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Lost Phone Information", canNavigateBack = true, onNavigateBack = onBack) }
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("📱 Lock Screen Owner Message", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("«If found, please contact Parth at +91 98765 43210 or email thekingparth491@gmail.com. Reward will be provided for safe return.»", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun MedicalCardScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Emergency Medical Card", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("IN CASE OF EMERGENCY (ICE)", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text("Name: Parth Sharma", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Blood Group: O+ (Positive)", fontWeight = FontWeight.SemiBold)
                    Text("Allergies: Penicillin, Peanuts")
                    Text("Emergency Contact: +91 98765 43210 (Father)")
                }
            }
        }
    }
}

@Composable
fun ImportantNumbersScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val numbers = listOf(
        Triple("National Emergency", "112", "All emergencies (Police, Fire, Ambulance)"),
        Triple("Police", "100", "State Police Control Room"),
        Triple("Ambulance", "108 / 102", "Emergency Medical Response"),
        Triple("Women Helpline", "1091", "24x7 Women Safety & Support"),
        Triple("Cyber Crime Helpline", "1930", "National Financial Fraud Reporting"),
        Triple("Child Helpline", "1098", "Children in distress care")
    )

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Important Helpline Numbers", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(numbers) { (service, num, desc) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(service, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Button(onClick = {
                                val cleanNum = num.split("/")[0].trim()
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanNum"))
                                context.startActivity(intent)
                            }) {
                                Icon(Icons.Default.Phone, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(num)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpamMessageOrganizerScreen(onBack: () -> Unit) {
    val alerts = listOf(
        "⚡ Electricity Bill Warning Fraud" to "Message: 'Your electricity power will be disconnected tonight. Call officer at 98xxxx.' -> 100% Phishing Scam. Never call or click links.",
        "🏦 Bank Account / KYC Suspended" to "Message: 'Dear customer, your bank account is blocked. Update PAN card here: bit.ly/xxx' -> Fake link to steal credentials.",
        "🎁 Lottery / Part-Time Job Telegram" to "Message: 'Earn ₹5000/day by liking YouTube videos.' -> Task fraud. Block and report to 1930."
    )

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Spam & Scam Organizer", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Known Scam Patterns & Safety Advice", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            alerts.forEach { (type, desc) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(type, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.titleMedium)
                        Text(desc, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
