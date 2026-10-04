package com.example.ui.tools.health

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
fun WalkingTrackerScreen(onBack: () -> Unit) {
    var steps by remember { mutableStateOf(7420) }
    val goal = 10000
    val progress = (steps.toFloat() / goal).coerceIn(0f, 1f)
    val distanceKm = (steps * 0.00076)
    val calories = (steps * 0.04).toInt()

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Walking & Step Tracker", canNavigateBack = true, onNavigateBack = onBack) }
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
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                    Text("$steps", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Daily Goal: $goal steps", fontWeight = FontWeight.Medium)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(10.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Distance: ${"%.2f".format(distanceKm)} km")
                        Text("Burned: $calories kcal")
                    }
                }
            }

            Button(onClick = { steps += 500 }, modifier = Modifier.fillMaxWidth()) {
                Text("+500 Steps Quick Log")
            }
        }
    }
}

@Composable
fun WorkoutPlannerScreen(onBack: () -> Unit) {
    val workouts = listOf(
        "Day 1: Chest & Triceps" to "Bench Press (4x10), Incline Dumbbell Press (3x12), Cable Pushdowns (4x15)",
        "Day 2: Back & Biceps" to "Pull-ups (4x8), Barbell Rows (4x10), Lat Pulldowns (3x12), Bicep Curls (4x12)",
        "Day 3: Legs & Core" to "Barbell Squats (4x10), Romanian Deadlifts (3x10), Planks (3x60s)"
    )

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Workout Planner", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            workouts.forEach { (day, exercises) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(day, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Text(exercises, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun CalorieJournalScreen(onBack: () -> Unit) {
    val meals = listOf(
        Pair("Breakfast", "Oatmeal with Almond Milk & Berries (380 kcal)"),
        Pair("Lunch", "Brown Rice, Dal & Grilled Paneer (620 kcal)"),
        Pair("Snack", "Green Tea & Walnuts (140 kcal)"),
        Pair("Dinner", "Vegetable Stir Fry & Quinoa (510 kcal)")
    )
    val totalCalories = 380 + 620 + 140 + 510

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Calorie Journal", canNavigateBack = true, onNavigateBack = onBack) }
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
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Today's Calorie Intake", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("$totalCalories kcal / 2,000 kcal", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

            meals.forEach { (meal, desc) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(meal, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun HealthAppointmentScreen(onBack: () -> Unit) {
    val appts = listOf(
        Triple("Dental Scaling & Checkup", "Dr. Mehta • Smile Care Clinic", "Date: 24 Oct 2026, 05:00 PM"),
        Triple("Annual Eye Vision Test", "Dr. Verma • ClearVision Opticals", "Date: 10 Nov 2026, 11:30 AM")
    )

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Health Appointments", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            appts.forEach { (title, doctor, time) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(doctor, style = MaterialTheme.typography.bodyMedium)
                        Text(time, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
fun HealthRecordScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Personal Health Record", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Mandatory Disclaimer Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "«This app is for personal tracking and information only. It is not a medical device and does not provide medical diagnosis.»",
                    modifier = Modifier.padding(14.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Medium
                )
            }

            val records = listOf(
                "Blood Group" to "O Positive (O+)",
                "Known Allergies" to "Peanuts, Dust Mites",
                "Chronic Conditions" to "None reported",
                "Emergency Medical Contact" to "Dr. Arvind (Family Physician) • +91 98765 43210"
            )

            records.forEach { (label, value) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
