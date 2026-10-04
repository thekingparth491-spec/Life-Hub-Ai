package com.example.ui.tools.health

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.LifeHubApplication
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.MedicineEntity
import com.example.ui.components.LifeHubTopAppBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WaterReminderScreen(onBack: () -> Unit) {
    var drunkMl by remember { mutableStateOf(1750) }
    val goalMl = 2500
    val progress = (drunkMl.toFloat() / goalMl).coerceIn(0f, 1f)

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Water Reminder", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.LocalDrink, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(54.dp))
                    Text("$drunkMl / $goalMl ml", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("${(progress * 100).toInt()}% of daily target completed", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Text("Quick Log Water", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { drunkMl = (drunkMl + 250).coerceAtMost(5000) }, modifier = Modifier.weight(1f)) {
                    Text("+250 ml")
                }
                Button(onClick = { drunkMl = (drunkMl + 500).coerceAtMost(5000) }, modifier = Modifier.weight(1f)) {
                    Text("+500 ml")
                }
                OutlinedButton(onClick = { drunkMl = 0 }) {
                    Text("Reset")
                }
            }
        }
    }
}

@Composable
fun SleepJournalScreen(onBack: () -> Unit) {
    var bedtime by remember { mutableStateOf("11:30 PM") }
    var wakeTime by remember { mutableStateOf("07:00 AM") }
    var totalSleep by remember { mutableStateOf("7.5") }
    var qualityRating by remember { mutableStateOf(4) }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Sleep Journal", canNavigateBack = true, onNavigateBack = onBack) }
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
                    Text("Last Night's Sleep", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("$totalSleep Hours", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Bedtime: $bedtime • Wake up: $wakeTime", style = MaterialTheme.typography.bodyMedium)
                    Row {
                        repeat(5) { index ->
                            Icon(
                                imageVector = if (index < qualityRating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            OutlinedTextField(value = bedtime, onValueChange = { bedtime = it }, label = { Text("Bedtime") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = wakeTime, onValueChange = { wakeTime = it }, label = { Text("Wake Time") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = totalSleep, onValueChange = { totalSleep = it }, label = { Text("Total Hours") }, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun HabitTrackerScreen(onBack: () -> Unit) {
    val repository = remember { LifeHubApplication.instance.repository }
    val scope = rememberCoroutineScope()
    val habits by repository.getAllHabits().collectAsStateWithLifecycle(initialValue = emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var habitName by remember { mutableStateOf("") }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Habit Tracker", canNavigateBack = true, onNavigateBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "New Habit")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Active Daily Streaks (${habits.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            if (habits.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No habits added. Tap + to track Daily Reading, Gym, Meditation, Yoga...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(habits, key = { it.id }) { habit ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = habit.completedToday,
                                    onCheckedChange = { checked ->
                                        val newStreak = if (checked) habit.currentStreak + 1 else (habit.currentStreak - 1).coerceAtLeast(0)
                                        scope.launch { repository.updateHabit(habit.copy(completedToday = checked, currentStreak = newStreak)) }
                                    }
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(habit.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text("🔥 ${habit.currentStreak} day streak", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                                }
                                IconButton(onClick = { scope.launch { repository.deleteHabit(habit) } }) {
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
            title = { Text("Add Habit") },
            text = {
                OutlinedTextField(value = habitName, onValueChange = { habitName = it }, label = { Text("Habit Name (e.g. 20 Mins Reading)") })
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (habitName.isNotBlank()) {
                            scope.launch {
                                repository.addHabit(HabitEntity(name = habitName, targetDays = 30, currentStreak = 1, completedToday = false))
                            }
                            habitName = ""
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
fun MeditationTimerScreen(onBack: () -> Unit) {
    var isPacing by remember { mutableStateOf(false) }
    var phase by remember { mutableStateOf("Inhale...") }

    val infiniteTransition = rememberInfiniteTransition(label = "breathe")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(isPacing) {
        while (isPacing) {
            phase = "Inhale slowly..."
            delay(4000)
            phase = "Hold breath..."
            delay(3000)
            phase = "Exhale gently..."
            delay(4000)
        }
    }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Mindfulness & Meditation", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(if (isPacing) scale else 1.0f)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isPacing) phase else "Tap Start",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = { isPacing = !isPacing },
                modifier = Modifier.width(180.dp).height(50.dp)
            ) {
                Text(if (isPacing) "End Session" else "Start Breathing")
            }
        }
    }
}

@Composable
fun MedicineReminderScreen(onBack: () -> Unit) {
    val repository = remember { LifeHubApplication.instance.repository }
    val scope = rememberCoroutineScope()
    val medicines by repository.getAllMedicines().collectAsStateWithLifecycle(initialValue = emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var medName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("1 tablet") }
    var timing by remember { mutableStateOf("Morning & Night") }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Medicine Reminder", canNavigateBack = true, onNavigateBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Add Medicine")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Scheduled Prescriptions (${medicines.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            if (medicines.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No medicines listed. Tap + to add Multivitamins, BP meds, etc.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(medicines, key = { it.id }) { med ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = med.takenToday,
                                    onCheckedChange = { checked ->
                                        scope.launch { repository.updateMedicine(med.copy(takenToday = checked)) }
                                    }
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(med.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text("${med.dosage} • ${med.timeSchedule}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = { scope.launch { repository.deleteMedicine(med) } }) {
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
            title = { Text("Add Medication") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = medName, onValueChange = { medName = it }, label = { Text("Medicine Name") })
                    OutlinedTextField(value = dosage, onValueChange = { dosage = it }, label = { Text("Dosage (e.g. 500mg, 1 tablet)") })
                    OutlinedTextField(value = timing, onValueChange = { timing = it }, label = { Text("Schedule (e.g. Morning after breakfast)") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (medName.isNotBlank()) {
                            scope.launch {
                                repository.addMedicine(MedicineEntity(name = medName, dosage = dosage, timeSchedule = timing))
                            }
                            medName = ""
                            showDialog = false
                        }
                    }
                ) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
        )
    }
}
