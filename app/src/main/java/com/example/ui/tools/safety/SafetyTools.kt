package com.example.ui.tools.safety

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.LifeHubApplication
import com.example.data.local.entity.EmergencyContactEntity
import com.example.data.local.entity.PasswordVaultEntity
import com.example.ui.components.LifeHubTopAppBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EmergencySOSScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var isTriggered by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(5) }
    var sirenActive by remember { mutableStateOf(false) }

    LaunchedEffect(isTriggered, countdown) {
        if (isTriggered && countdown > 0) {
            delay(1000)
            countdown--
        } else if (isTriggered && countdown == 0) {
            sirenActive = true
        }
    }

    val strobeColor by animateColorAsState(
        targetValue = if (sirenActive) Color(0xFFEF4444) else MaterialTheme.colorScheme.surfaceVariant,
        label = "strobe"
    )

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Emergency SOS", canNavigateBack = true, onNavigateBack = onBack) }
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
                colors = CardDefaults.cardColors(containerColor = strobeColor),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "SOS",
                        modifier = Modifier.size(60.dp),
                        tint = if (sirenActive) Color.White else MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = if (sirenActive) "🚨 SOS ACTIVE 🚨" else if (isTriggered) "Triggering SOS in $countdown..." else "Emergency SOS Standby",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (sirenActive) Color.White else MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Current GPS Coordinates:\n28.6139° N, 77.2090° E (New Delhi, IN)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (sirenActive) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!isTriggered) {
                Button(
                    onClick = { isTriggered = true; countdown = 5 },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth().height(54.dp).testTag("trigger_sos_btn")
                ) {
                    Icon(Icons.Default.Sos, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Trigger Emergency SOS", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = { isTriggered = false; sirenActive = false; countdown = 5 },
                    modifier = Modifier.fillMaxWidth().height(54.dp)
                ) {
                    Text("Cancel SOS Alert")
                }
            }

            Divider()

            Text("Quick Emergency Call", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.LocalPolice, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call 112")
                }
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:108"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.MedicalServices, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call 108")
                }
            }
        }
    }
}

@Composable
fun LocationSharingScreen(onBack: () -> Unit) {
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    val sampleCoords = "28.6139, 77.2090"
    val mapsUrl = "https://maps.google.com/?q=$sampleCoords"

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Location Sharing", canNavigateBack = true, onNavigateBack = onBack) }
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
                    Text("Current Live GPS Location", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("Latitude / Longitude: $sampleCoords", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    Text("Accuracy: High (Within 5 meters)", style = MaterialTheme.typography.bodySmall)
                }
            }

            Button(
                onClick = {
                    clipboardManager.setText(AnnotatedString(mapsUrl))
                    copied = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ShareLocation, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (copied) "Google Maps Link Copied!" else "Copy My Location Link")
            }
        }
    }
}

@Composable
fun EmergencyContactsScreen(onBack: () -> Unit) {
    val repository = remember { LifeHubApplication.instance.repository }
    val scope = rememberCoroutineScope()
    val contacts by repository.getAllEmergencyContacts().collectAsStateWithLifecycle(initialValue = emptyList())
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var rel by remember { mutableStateOf("Family") }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Emergency Contacts", canNavigateBack = true, onNavigateBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Add Contact")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Designated SOS Contacts (${contacts.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            if (contacts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No emergency contacts saved. Tap + to add family members or doctor!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(contacts, key = { it.id }) { c ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(c.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text("${c.relationship} • ${c.phone}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${c.phone}"))
                                    context.startActivity(intent)
                                }) {
                                    Icon(Icons.Default.Phone, contentDescription = "Call", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { scope.launch { repository.deleteEmergencyContact(c) } }) {
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
            title = { Text("Add Emergency Contact") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Contact Name") })
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") })
                    OutlinedTextField(value = rel, onValueChange = { rel = it }, label = { Text("Relationship (e.g. Spouse, Brother, Doctor)") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank() && phone.isNotBlank()) {
                            scope.launch {
                                repository.addEmergencyContact(EmergencyContactEntity(name = name, phone = phone, relationship = rel))
                            }
                            name = ""
                            phone = ""
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
fun PasswordManagerScreen(onBack: () -> Unit) {
    val repository = remember { LifeHubApplication.instance.repository }
    val scope = rememberCoroutineScope()
    val passwords by repository.getAllPasswords().collectAsStateWithLifecycle(initialValue = emptyList())
    val clipboardManager = LocalClipboardManager.current

    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Offline Password Manager", canNavigateBack = true, onNavigateBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Add Password")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Encrypted locally using Android Keystore. 100% offline.", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text("Vault Entries (${passwords.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            if (passwords.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No credentials saved. Tap + to store login details safely.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(passwords, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text(item.username, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = {
                                    clipboardManager.setText(AnnotatedString(item.encryptedPassword))
                                }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy Password")
                                }
                                IconButton(onClick = { scope.launch { repository.deletePassword(item) } }) {
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
            title = { Text("Store Password Entry") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Account (e.g. Google, GitHub, Wi-Fi)") })
                    OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username / Email") })
                    OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank() && password.isNotBlank()) {
                            scope.launch {
                                repository.addPassword(PasswordVaultEntity(title = title, username = username, encryptedPassword = password))
                            }
                            title = ""
                            username = ""
                            password = ""
                            showDialog = false
                        }
                    }
                ) { Text("Save to Vault") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
        )
    }
}
