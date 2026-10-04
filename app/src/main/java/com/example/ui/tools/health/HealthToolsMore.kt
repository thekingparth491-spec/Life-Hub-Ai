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
    var workouts by remember {
        mutableStateOf(
            listOf(
                "Day 1: Chest & Triceps" to "Bench Press (4x10), Incline Dumbbell Press (3x12), Cable Pushdowns (4x15)",
                "Day 2: Back & Biceps" to "Pull-ups (4x8), Barbell Rows (4x10), Lat Pulldowns (3x12), Bicep Curls (4x12)",
                "Day 3: Legs & Core" to "Barbell Squats (4x10), Romanian Deadlifts (3x10), Planks (3x60s)"
            )
        )
    }
    var showDialog by remember { mutableStateOf(false) }
    var routineDay by remember { mutableStateOf("") }
    var routineExercises by remember { mutableStateOf("") }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Workout Planner", canNavigateBack = true, onNavigateBack = onBack) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                text = { Text("Add Workout") },
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
                Text("Weekly Workouts (${workouts.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                FilledTonalButton(onClick = { showDialog = true }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add New", style = MaterialTheme.typography.labelSmall)
                }
            }

            if (workouts.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text("No workout days scheduled. Tap '+ Add New' to plan your fitness routine.", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                }
            }

            workouts.forEachIndexed { index, (day, exercises) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(day, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            Text(exercises, style = MaterialTheme.typography.bodyMedium)
                        }
                        IconButton(onClick = {
                            workouts = workouts.filterIndexed { i, _ -> i != index }
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
            title = { Text("Add Workout Routine") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = routineDay,
                        onValueChange = { routineDay = it },
                        label = { Text("Day / Muscle Focus") },
                        placeholder = { Text("e.g. Day 4: Shoulders & Abs") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = routineExercises,
                        onValueChange = { routineExercises = it },
                        label = { Text("Exercises, Sets & Reps") },
                        placeholder = { Text("e.g. Overhead Press (4x10), Lateral Raises (4x15)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (routineDay.isNotBlank() && routineExercises.isNotBlank()) {
                            workouts = workouts + (routineDay.trim() to routineExercises.trim())
                            routineDay = ""
                            routineExercises = ""
                            showDialog = false
                        }
                    },
                    enabled = routineDay.isNotBlank() && routineExercises.isNotBlank()
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun CalorieJournalScreen(onBack: () -> Unit) {
    var meals by remember {
        mutableStateOf(
            listOf(
                Triple("Breakfast", "Oatmeal with Almond Milk & Berries", 380),
                Triple("Lunch", "Brown Rice, Dal & Grilled Paneer", 620),
                Triple("Snack", "Green Tea & Walnuts", 140),
                Triple("Dinner", "Vegetable Stir Fry & Quinoa", 510)
            )
        )
    }
    var showDialog by remember { mutableStateOf(false) }
    var mealType by remember { mutableStateOf("Breakfast") }
    var foodDesc by remember { mutableStateOf("") }
    var calValue by remember { mutableStateOf("") }

    val totalCalories = meals.sumOf { it.third }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Calorie Journal", canNavigateBack = true, onNavigateBack = onBack) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                text = { Text("Log Meal") },
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Meals Logged (${meals.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                FilledTonalButton(onClick = { showDialog = true }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add New", style = MaterialTheme.typography.labelSmall)
                }
            }

            if (meals.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text("No meals logged today. Tap '+ Add New' to record calories.", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                }
            }

            meals.forEachIndexed { index, (meal, desc, cal) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(meal, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("$desc ($cal kcal)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = {
                            meals = meals.filterIndexed { i, _ -> i != index }
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
            title = { Text("Log Meal / Food Item") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = mealType,
                        onValueChange = { mealType = it },
                        label = { Text("Meal Category") },
                        placeholder = { Text("Breakfast, Lunch, Snack, Dinner") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = foodDesc,
                        onValueChange = { foodDesc = it },
                        label = { Text("Food Item & Portions") },
                        placeholder = { Text("e.g. 2 Boiled Eggs & Whole Wheat Toast") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = calValue,
                        onValueChange = { calValue = it },
                        label = { Text("Calories (kcal)") },
                        placeholder = { Text("e.g. 240") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsedCal = calValue.toIntOrNull() ?: 200
                        if (foodDesc.isNotBlank()) {
                            val cat = if (mealType.isBlank()) "Snack" else mealType.trim()
                            meals = meals + Triple(cat, foodDesc.trim(), parsedCal)
                            foodDesc = ""
                            calValue = ""
                            showDialog = false
                        }
                    },
                    enabled = foodDesc.isNotBlank()
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun HealthAppointmentScreen(onBack: () -> Unit) {
    var appts by remember {
        mutableStateOf(
            listOf(
                Triple("Dental Scaling & Checkup", "Dr. Mehta • Smile Care Clinic", "Date: 24 Oct 2026, 05:00 PM"),
                Triple("Annual Eye Vision Test", "Dr. Verma • ClearVision Opticals", "Date: 10 Nov 2026, 11:30 AM")
            )
        )
    }
    var showDialog by remember { mutableStateOf(false) }
    var apptTitle by remember { mutableStateOf("") }
    var doctorDetails by remember { mutableStateOf("") }
    var apptDateTime by remember { mutableStateOf("") }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Health Appointments", canNavigateBack = true, onNavigateBack = onBack) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                text = { Text("Add Appointment") },
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
                Text("Upcoming Consultations (${appts.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                FilledTonalButton(onClick = { showDialog = true }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add New", style = MaterialTheme.typography.labelSmall)
                }
            }

            if (appts.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text("No appointments scheduled. Tap '+ Add New' to schedule a doctor visit.", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                }
            }

            appts.forEachIndexed { index, (title, doctor, time) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(doctor, style = MaterialTheme.typography.bodyMedium)
                            Text(time, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = {
                            appts = appts.filterIndexed { i, _ -> i != index }
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
            title = { Text("Schedule Health Appointment") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = apptTitle,
                        onValueChange = { apptTitle = it },
                        label = { Text("Appointment / Reason") },
                        placeholder = { Text("e.g. Dermatology Consultation, Blood Test") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = doctorDetails,
                        onValueChange = { doctorDetails = it },
                        label = { Text("Doctor & Clinic / Hospital") },
                        placeholder = { Text("e.g. Dr. Sharma • Apollo Hospital") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = apptDateTime,
                        onValueChange = { apptDateTime = it },
                        label = { Text("Date & Time") },
                        placeholder = { Text("e.g. Date: 15 Nov 2026, 04:00 PM") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (apptTitle.isNotBlank()) {
                            val doc = if (doctorDetails.isBlank()) "General Physician" else doctorDetails.trim()
                            val dt = if (apptDateTime.isBlank()) "Date: TBD" else if (!apptDateTime.startsWith("Date")) "Date: $apptDateTime" else apptDateTime
                            appts = appts + Triple(apptTitle.trim(), doc, dt)
                            apptTitle = ""
                            doctorDetails = ""
                            apptDateTime = ""
                            showDialog = false
                        }
                    },
                    enabled = apptTitle.isNotBlank()
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun HealthRecordScreen(onBack: () -> Unit) {
    var records by remember {
        mutableStateOf(
            listOf(
                "Blood Group" to "O Positive (O+)",
                "Known Allergies" to "Peanuts, Dust Mites",
                "Chronic Conditions" to "None reported",
                "Emergency Medical Contact" to "Dr. Arvind (Family Physician) • +91 98765 43210"
            )
        )
    }
    var showDialog by remember { mutableStateOf(false) }
    var recordLabel by remember { mutableStateOf("") }
    var recordValue by remember { mutableStateOf("") }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Personal Health Record", canNavigateBack = true, onNavigateBack = onBack) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                text = { Text("Add Record") },
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Health Metrics & Contacts (${records.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                FilledTonalButton(onClick = { showDialog = true }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add New", style = MaterialTheme.typography.labelSmall)
                }
            }

            records.forEachIndexed { index, (label, value) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                        IconButton(onClick = {
                            records = records.filterIndexed { i, _ -> i != index }
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
            title = { Text("Add Personal Health Metric") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = recordLabel,
                        onValueChange = { recordLabel = it },
                        label = { Text("Field / Health Category") },
                        placeholder = { Text("e.g. Blood Pressure Baseline, Vaccination Date") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = recordValue,
                        onValueChange = { recordValue = it },
                        label = { Text("Value / Details") },
                        placeholder = { Text("e.g. 120/80 mmHg, Tetanus Booster Jan 2025") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (recordLabel.isNotBlank() && recordValue.isNotBlank()) {
                            records = records + (recordLabel.trim() to recordValue.trim())
                            recordLabel = ""
                            recordValue = ""
                            showDialog = false
                        }
                    },
                    enabled = recordLabel.isNotBlank() && recordValue.isNotBlank()
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}
