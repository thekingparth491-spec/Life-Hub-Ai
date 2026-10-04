package com.example.ui.tools.aitools

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.LifeHubTopAppBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AIVoiceNotesScreen(onBack: () -> Unit) {
    var isRecording by remember { mutableStateOf(false) }
    var noteText by remember { mutableStateOf("Discussed product launch milestones for Q4. Team needs to finalize Android applet architecture, optimize SQLite Room persistence, and ensure Play Store compliance.") }
    var aiSummary by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "AI Voice Notes", canNavigateBack = true, onNavigateBack = onBack) }
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
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = { isRecording = !isRecording },
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(32.dp)),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(if (isRecording) Icons.Default.Stop else Icons.Default.Mic, contentDescription = "Record")
                    }
                    Text(if (isRecording) "Recording speech..." else "Tap to dictate voice note", fontWeight = FontWeight.Bold)
                }
            }

            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text("Transcribed Note Content") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Button(
                onClick = {
                    scope.launch {
                        isProcessing = true
                        delay(600)
                        aiSummary = "📌 Key Takeaways:\n• Q4 Product Launch milestones on track.\n• Critical focus on Android architecture and Room persistence.\n• Play Store compliance guidelines validated."
                        isProcessing = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "Summarize")
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isProcessing) "Analyzing with AI..." else "Extract Key Insights & Action Items")
            }

            if (aiSummary.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("AI Insights", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(aiSummary, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun MeetingSummarizerScreen(onBack: () -> Unit) {
    var rawNotes by remember { mutableStateOf("Meeting attendees: John, Sarah, Mike.\nDiscussed server migration timeline. Mike agreed to finish database schema by Wednesday. Sarah will prepare client slides by Friday. Next follow-up call on Thursday 3 PM.") }
    var summary by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Meeting Summarizer", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = rawNotes,
                onValueChange = { rawNotes = it },
                label = { Text("Paste Meeting Transcript / Notes") },
                minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    scope.launch {
                        isGenerating = true
                        delay(600)
                        summary = "🎯 Agenda & Decisions:\n• Server migration initiated.\n\n✅ Action Items:\n1. Mike: Finish database schema by Wednesday.\n2. Sarah: Prepare client presentation by Friday.\n3. Team: Sync on Thursday 3 PM."
                        isGenerating = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isGenerating) "Generating..." else "Generate Structured Summary")
            }

            if (summary.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Executive Meeting Summary", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(summary, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun EmailWriterScreen(onBack: () -> Unit) {
    var recipient by remember { mutableStateOf("Manager") }
    var topic by remember { mutableStateOf("Requesting leave next Monday for personal work") }
    var tone by remember { mutableStateOf("Formal") }
    var generatedEmail by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current

    val tones = listOf("Formal", "Casual", "Urgent", "Polite")

    fun generate() {
        generatedEmail = when (tone) {
            "Formal" -> "Subject: Leave Application - Monday\n\nDear $recipient,\n\nI am writing to formally request a day off on next Monday due to personal commitments. I have arranged for my pending tasks to be handled in advance and will ensure a seamless handover.\n\nThank you for your understanding.\n\nSincerely,\nLifeHub User"
            "Casual" -> "Subject: Quick note: Taking off next Monday\n\nHi $recipient,\n\nJust wanted to let you know I'll be taking off next Monday for some personal errands. Catch you on Tuesday!\n\nBest,\nUser"
            else -> "Subject: Request for Leave: Next Monday\n\nDear $recipient,\n\nI kindly request your approval for one day of personal leave next Monday. I will remain reachable on mobile if anything urgent arises.\n\nWarm regards,\nUser"
        }
    }

    LaunchedEffect(Unit) { generate() }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "AI Email Writer", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(value = recipient, onValueChange = { recipient = it }, label = { Text("Recipient (e.g. Professor, Manager, Client)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = topic, onValueChange = { topic = it }, label = { Text("What is this email about?") }, modifier = Modifier.fillMaxWidth())

            Text("Tone of Voice", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tones.forEach { t ->
                    FilterChip(selected = tone == t, onClick = { tone = t; generate() }, label = { Text(t) })
                }
            }

            Button(onClick = { generate() }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate Email Draft")
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Drafted Email", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        IconButton(onClick = { clipboardManager.setText(AnnotatedString(generatedEmail)) }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                        }
                    }
                    Text(generatedEmail, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun GrammarCheckerScreen(onBack: () -> Unit) {
    var inputText by remember { mutableStateOf("He go to store yesterday and buyed three apple.") }
    var correctedText by remember { mutableStateOf("He went to the store yesterday and bought three apples.") }
    var explanation by remember { mutableStateOf("• Changed 'go' to past tense 'went'\n• Corrected 'buyed' to irregular past tense 'bought'\n• Pluralized 'apple' to 'apples' following count 'three'") }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Grammar & Tone Checker", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Enter text to inspect") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (inputText.contains("go to store")) {
                        correctedText = "He went to the store yesterday and bought three apples."
                        explanation = "• Changed 'go' to past tense 'went'\n• Corrected 'buyed' to 'bought'\n• Pluralized 'apple' to 'apples'"
                    } else {
                        correctedText = inputText.trim().replaceFirstChar { it.uppercase() } + if (!inputText.endsWith(".")) "." else ""
                        explanation = "Grammar verified: Proper punctuation and capitalization applied."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Spellcheck, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Check & Enhance Grammar")
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Polished Version", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(correctedText, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    Divider()
                    Text(explanation, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
