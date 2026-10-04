package com.example.ui.tools.student

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.LifeHubApplication
import com.example.data.local.entity.NoteEntity
import com.example.data.local.entity.TaskEntity
import com.example.ui.components.LifeHubTopAppBar
import kotlinx.coroutines.launch

@Composable
fun StudyPlannerScreen(onBack: () -> Unit) {
    val plan = listOf(
        Triple("09:00 AM - 10:30 AM", "Advanced Algorithms", "Dynamic Programming problems"),
        Triple("11:00 AM - 12:30 PM", "Database Systems", "ACID properties & normal forms"),
        Triple("02:00 PM - 03:30 PM", "Computer Networks", "OSI model layers & TCP handshake"),
        Triple("04:30 PM - 05:30 PM", "Revision & Quiz", "Solve 20 past mock questions")
    )

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Study Planner", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Today's Revision Schedule", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            plan.forEach { (slot, subject, topic) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(slot, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text(subject, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(topic, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun ExamCountdownScreen(onBack: () -> Unit) {
    val exams = listOf(
        Triple("Operating Systems Midterms", "12 Days Left", "15 Oct 2026"),
        Triple("Machine Learning Final Exam", "28 Days Left", "31 Oct 2026"),
        Triple("Software Engineering Project Viva", "44 Days Left", "16 Nov 2026")
    )

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Exam Countdown", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            exams.forEach { (name, days, date) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text("Scheduled: $date", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                        }
                        Text(days, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun HomeworkTrackerScreen(onBack: () -> Unit) {
    val repository = remember { LifeHubApplication.instance.repository }
    val scope = rememberCoroutineScope()
    val tasks by repository.getAllTasks().collectAsStateWithLifecycle(initialValue = emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var taskTitle by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("Tomorrow 5 PM") }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Homework Tracker", canNavigateBack = true, onNavigateBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Add Homework")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Pending Assignments (${tasks.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            if (tasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No pending homework! Tap + to add assignments.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tasks, key = { it.id }) { task ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = task.isCompleted,
                                    onCheckedChange = { checked ->
                                        scope.launch { repository.updateTask(task.copy(isCompleted = checked)) }
                                    }
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(task.title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                                    Text("Due: ${task.dueDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = { scope.launch { repository.deleteTask(task) } }) {
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
            title = { Text("Add Homework Assignment") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = taskTitle, onValueChange = { taskTitle = it }, label = { Text("Assignment Title") })
                    OutlinedTextField(value = dueDate, onValueChange = { dueDate = it }, label = { Text("Due Date / Time") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (taskTitle.isNotBlank()) {
                            scope.launch {
                                repository.addTask(TaskEntity(title = taskTitle, dueDate = dueDate, category = "Student"))
                            }
                            taskTitle = ""
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
fun ClassTimetableScreen(onBack: () -> Unit) {
    val schedule = listOf(
        "Monday" to "09:00 Data Structures • 11:00 Operating Systems • 02:00 Lab",
        "Tuesday" to "10:00 Database Systems • 01:00 Computer Networks",
        "Wednesday" to "09:00 Software Engineering • 11:00 AI Basics • 03:00 Seminar",
        "Thursday" to "10:00 Cloud Computing • 02:00 Project Lab",
        "Friday" to "09:00 Cyber Security • 11:00 Discrete Mathematics"
    )

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Class Timetable", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            schedule.forEach { (day, classes) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(day, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
                        Text(classes, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun NotesOrganizerScreen(onBack: () -> Unit) {
    val repository = remember { LifeHubApplication.instance.repository }
    val scope = rememberCoroutineScope()
    val notes by repository.getAllNotes().collectAsStateWithLifecycle(initialValue = emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Notes Organizer", canNavigateBack = true, onNavigateBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Saved Study Notes (${notes.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            if (notes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No notes yet. Tap + to create study or class notes!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(notes, key = { it.id }) { note ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(note.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    IconButton(onClick = { scope.launch { repository.deleteNote(note) } }) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                                Text(note.content, style = MaterialTheme.typography.bodyMedium)
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
            title = { Text("Create Study Note") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Note Title") })
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Notes Body") }, minLines = 3)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            scope.launch {
                                repository.addNote(NoteEntity(title = title, content = content, tag = "Study"))
                            }
                            title = ""
                            content = ""
                            showDialog = false
                        }
                    }
                ) { Text("Save Note") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun QuestionPaperOrganizerScreen(onBack: () -> Unit) {
    val papers = listOf(
        Triple("Data Structures & Algorithms", "2025 Mid-Sem Exam", "Solved with Model Answers"),
        Triple("Computer Organization & Architecture", "2024 End-Sem Exam", "Question Paper PDF attached"),
        Triple("Discrete Mathematics", "2023 University Final", "Includes practice formula sheet")
    )

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Question Paper Organizer", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            papers.forEach { (sub, exam, note) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(sub, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(exam, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        Text(note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
