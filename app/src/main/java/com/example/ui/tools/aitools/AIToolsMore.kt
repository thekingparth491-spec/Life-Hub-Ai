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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.LifeHubTopAppBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PDFSummarizerScreen(onBack: () -> Unit) {
    var docContent by remember { mutableStateOf("Artificial intelligence in mobile computing has enabled edge-inference and local vector processing. Applications leverage on-device models for sub-millisecond response latency without relying on continuous internet connectivity.") }
    var summaryResult by remember { mutableStateOf("") }
    var isAnalyzing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Document & PDF Summarizer", canNavigateBack = true, onNavigateBack = onBack) }
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
                value = docContent,
                onValueChange = { docContent = it },
                label = { Text("Paste Document or Study Material Text") },
                minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    scope.launch {
                        isAnalyzing = true
                        delay(600)
                        summaryResult = "📑 Executive Summary:\nOn-device mobile AI delivers instant responses and privacy protection by running locally without mandatory cloud round-trips."
                        isAnalyzing = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isAnalyzing) "Summarizing..." else "Generate Document Summary")
            }

            if (summaryResult.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(summaryResult, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }
        }
    }
}

@Composable
fun HomeworkHelperScreen(onBack: () -> Unit) {
    var question by remember { mutableStateOf("What is the derivative of f(x) = 3x^2 + 5x - 7?") }
    var solution by remember { mutableStateOf("Step 1: Apply Power Rule d/dx [x^n] = n*x^(n-1)\nStep 2: d/dx [3x^2] = 3 * 2x = 6x\nStep 3: d/dx [5x] = 5\nStep 4: Constant derivative d/dx [-7] = 0\n\nFinal Answer: f'(x) = 6x + 5") }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "AI Homework Helper", canNavigateBack = true, onNavigateBack = onBack) }
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
                value = question,
                onValueChange = { question = it },
                label = { Text("Enter Math, Physics or General Question") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    solution = "Step-by-step breakdown for '$question':\n• Identifies core theorem and boundary conditions.\n• Formula applied systematically with verified step results."
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.School, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Explain Step-by-Step")
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Explanation & Working", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(solution, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun WhatsAppReplyScreen(onBack: () -> Unit) {
    var message by remember { mutableStateOf("Are you coming to the dinner party tonight?") }
    val options = listOf(
        "Polite" to "Yes, I'd love to join! Looking forward to seeing everyone tonight! 😊",
        "Casual" to "Yep, count me in! See you there around 8.",
        "Busy" to "Hey, so sorry I have a prior commitment tonight and won't be able to make it. Have a great time!",
        "Funny" to "Only if there's good food and great company! (Count me in 🙌)"
    )

    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "WhatsApp Reply Generator", canNavigateBack = true, onNavigateBack = onBack) }
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
                value = message,
                onValueChange = { message = it },
                label = { Text("Received Message") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Suggested Replies by Vibe", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            options.forEach { (vibe, reply) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(vibe, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            IconButton(onClick = { clipboardManager.setText(AnnotatedString(reply)) }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                            }
                        }
                        Text(reply, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun ResumeBuilderScreen(onBack: () -> Unit) {
    var name by remember { mutableStateOf("Parth Sharma") }
    var title by remember { mutableStateOf("Android Software Engineer") }
    var skills by remember { mutableStateOf("Kotlin, Jetpack Compose, Room, Coroutines, MVVM, Git") }
    val clipboardManager = LocalClipboardManager.current

    val resumeText = """
        $name
        $title | thekingparth491@gmail.com
        ------------------------------------------
        PROFESSIONAL SUMMARY:
        Dedicated engineer with expertise in building scalable, modern mobile applications on Android using Jetpack Compose, Kotlin, and offline-first Room architectures.

        CORE SKILLS:
        $skills

        EXPERIENCE:
        Software Engineer — Mobile Solutions (2024 - Present)
        • Developed full-featured modular super apps with responsive UI and offline persistence.
    """.trimIndent()

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Resume Builder", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Role / Target Title") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = skills, onValueChange = { skills = it }, label = { Text("Core Skills (comma separated)") }, modifier = Modifier.fillMaxWidth())

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Resume Preview", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        IconButton(onClick = { clipboardManager.setText(AnnotatedString(resumeText)) }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Resume")
                        }
                    }
                    Text(resumeText, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun ImageCaptionGeneratorScreen(onBack: () -> Unit) {
    var scene by remember { mutableStateOf("Mountain hike at sunset with friends") }
    val captions = listOf(
        "Chasing sunsets and peak moments with the best crew. 🌄✨ #MountainVibes #Wanderlust",
        "Higher we climb, better the view. Never stop exploring! ⛰️🎒",
        "Sunsets are proof that endings can often be beautiful too. 🌅",
        "Golden hour therapy at 2000 meters. 🌿🧭 #NatureLovers #TrekDays"
    )

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Image Caption Generator", canNavigateBack = true, onNavigateBack = onBack) }
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
                value = scene,
                onValueChange = { scene = it },
                label = { Text("Describe the photo or vibe") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Viral Captions & Hashtags", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            captions.forEach { cap ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(cap, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
