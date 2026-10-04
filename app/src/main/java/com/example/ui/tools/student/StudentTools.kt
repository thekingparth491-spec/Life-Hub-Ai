package com.example.ui.tools.student

import androidx.compose.foundation.clickable
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
import com.example.data.local.entity.AttendanceEntity
import com.example.data.local.entity.FlashcardEntity
import com.example.data.local.entity.TaskEntity
import com.example.ui.components.LifeHubTopAppBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PomodoroTimerScreen(onBack: () -> Unit) {
    var timeLeftSeconds by remember { mutableStateOf(25 * 60) }
    var isRunning by remember { mutableStateOf(false) }
    var completedSessions by remember { mutableStateOf(3) }
    var isBreak by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning, timeLeftSeconds) {
        if (isRunning && timeLeftSeconds > 0) {
            delay(1000)
            timeLeftSeconds--
        } else if (isRunning && timeLeftSeconds == 0) {
            if (!isBreak) {
                completedSessions++
                isBreak = true
                timeLeftSeconds = 5 * 60
            } else {
                isBreak = false
                timeLeftSeconds = 25 * 60
            }
            isRunning = false
        }
    }

    val minutes = timeLeftSeconds / 60
    val seconds = timeLeftSeconds % 60
    val timeFormatted = "%02d:%02d".format(minutes, seconds)

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Pomodoro Focus Timer", canNavigateBack = true, onNavigateBack = onBack) }
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
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(if (isBreak) "☕ Break Time" else "🎯 Deep Focus Time", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(timeFormatted, style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Completed Focus Rounds: $completedSessions", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { isRunning = !isRunning },
                    modifier = Modifier.weight(1f).height(50.dp)
                ) {
                    Icon(if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isRunning) "Pause" else "Start Focus")
                }
                OutlinedButton(
                    onClick = {
                        isRunning = false
                        isBreak = false
                        timeLeftSeconds = 25 * 60
                    },
                    modifier = Modifier.weight(1f).height(50.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset")
                }
            }
        }
    }
}

@Composable
fun AttendanceCalculatorScreen(onBack: () -> Unit) {
    val repository = remember { LifeHubApplication.instance.repository }
    val scope = rememberCoroutineScope()
    val attendanceList by repository.getAllAttendance().collectAsStateWithLifecycle(initialValue = emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var subjectName by remember { mutableStateOf("") }
    var attendedCount by remember { mutableStateOf("28") }
    var totalCount by remember { mutableStateOf("34") }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Attendance Calculator", canNavigateBack = true, onNavigateBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Add Subject")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Subjects & 75% Criteria Tracker", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                FilledTonalButton(onClick = { showDialog = true }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add New", style = MaterialTheme.typography.labelSmall)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (attendanceList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No subjects added. Tap + to track attendance per subject!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(attendanceList, key = { it.id }) { item ->
                        val pct = if (item.total > 0) (item.attended.toDouble() / item.total) * 100 else 0.0
                        val isSafe = pct >= 75.0
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(item.subjectName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text("${"%.1f".format(pct)}%", fontWeight = FontWeight.Bold, color = if (isSafe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                                }
                                Text("Attended: ${item.attended} / ${item.total} lectures", style = MaterialTheme.typography.bodySmall)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(onClick = {
                                        scope.launch {
                                            repository.updateAttendance(item.copy(attended = item.attended + 1, total = item.total + 1))
                                        }
                                    }) { Text("+ Attended") }
                                    TextButton(onClick = {
                                        scope.launch {
                                            repository.updateAttendance(item.copy(total = item.total + 1))
                                        }
                                    }) { Text("+ Missed") }
                                    Spacer(modifier = Modifier.weight(1f))
                                    IconButton(onClick = { scope.launch { repository.deleteAttendance(item) } }) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
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
            title = { Text("Add Subject Attendance") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = subjectName, onValueChange = { subjectName = it }, label = { Text("Subject (e.g. Operating Systems)") })
                    OutlinedTextField(value = attendedCount, onValueChange = { attendedCount = it }, label = { Text("Classes Attended") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = totalCount, onValueChange = { totalCount = it }, label = { Text("Total Classes Conducted") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val a = attendedCount.toIntOrNull() ?: 0
                        val t = totalCount.toIntOrNull() ?: 1
                        if (subjectName.isNotBlank()) {
                            scope.launch {
                                repository.addAttendance(AttendanceEntity(subjectName = subjectName, attended = a, total = t))
                            }
                            subjectName = ""
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
fun GPACalculatorScreen(onBack: () -> Unit) {
    var subject1Credits by remember { mutableStateOf("4") }
    var subject1Grade by remember { mutableStateOf("9") }
    var subject2Credits by remember { mutableStateOf("3") }
    var subject2Grade by remember { mutableStateOf("8") }
    var subject3Credits by remember { mutableStateOf("4") }
    var subject3Grade by remember { mutableStateOf("10") }

    val c1 = subject1Credits.toDoubleOrNull() ?: 0.0
    val g1 = subject1Grade.toDoubleOrNull() ?: 0.0
    val c2 = subject2Credits.toDoubleOrNull() ?: 0.0
    val g2 = subject2Grade.toDoubleOrNull() ?: 0.0
    val c3 = subject3Credits.toDoubleOrNull() ?: 0.0
    val g3 = subject3Grade.toDoubleOrNull() ?: 0.0

    val totalCredits = c1 + c2 + c3
    val totalPoints = (c1 * g1) + (c2 * g2) + (c3 * g3)
    val gpa = if (totalCredits > 0) totalPoints / totalCredits else 0.0

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "GPA / CGPA Calculator", canNavigateBack = true, onNavigateBack = onBack) }
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
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Calculated Semester GPA", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("${"%.2f".format(gpa)}", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Total Credits: $totalCredits", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Text("Course Grade Points (10-point scale)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = subject1Credits, onValueChange = { subject1Credits = it }, label = { Text("Course 1 Credits") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = subject1Grade, onValueChange = { subject1Grade = it }, label = { Text("Grade (0-10)") }, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = subject2Credits, onValueChange = { subject2Credits = it }, label = { Text("Course 2 Credits") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = subject2Grade, onValueChange = { subject2Grade = it }, label = { Text("Grade (0-10)") }, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = subject3Credits, onValueChange = { subject3Credits = it }, label = { Text("Course 3 Credits") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = subject3Grade, onValueChange = { subject3Grade = it }, label = { Text("Grade (0-10)") }, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun FlashcardsScreen(onBack: () -> Unit) {
    val repository = remember { LifeHubApplication.instance.repository }
    val scope = rememberCoroutineScope()
    val flashcards by repository.getAllFlashcards().collectAsStateWithLifecycle(initialValue = emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var question by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("Computer Science") }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Study Flashcards", canNavigateBack = true, onNavigateBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Add Card")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Active Revision Decks (${flashcards.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            if (flashcards.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No flashcards added. Tap + to add Q&A cards for exams!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(flashcards, key = { it.id }) { card ->
                        var isFlipped by remember { mutableStateOf(false) }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { isFlipped = !isFlipped },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isFlipped) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(card.subject, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                Text(
                                    if (isFlipped) "A: ${card.answer}" else "Q: ${card.question}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("Tap card to flip between Question and Answer", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            title = { Text("Create Flashcard") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject / Topic") })
                    OutlinedTextField(value = question, onValueChange = { question = it }, label = { Text("Question / Prompt") })
                    OutlinedTextField(value = answer, onValueChange = { answer = it }, label = { Text("Answer / Definition") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (question.isNotBlank() && answer.isNotBlank()) {
                            scope.launch {
                                repository.addFlashcard(FlashcardEntity(subject = subject, question = question, answer = answer))
                            }
                            question = ""
                            answer = ""
                            showDialog = false
                        }
                    }
                ) { Text("Save Card") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
        )
    }
}
