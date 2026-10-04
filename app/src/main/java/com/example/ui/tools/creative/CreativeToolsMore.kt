package com.example.ui.tools.creative

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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.LifeHubTopAppBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun InstaCaptionScreen(onBack: () -> Unit) {
    val clipboardManager = LocalClipboardManager.current
    val captions = listOf(
        "Less perfection, more authenticity. ✨📸 #GoodVibesOnly #Aesthetic #Moments",
        "Collecting memories, not things. 🌍✈️ #TravelGram #ExploreMore",
        "Life is better when you're laughing. 😄✨ #HappySoul #SmileAlways",
        "Golden hour magic hitting just right. ☀️💛 #SunKissed #Vibes"
    )

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Instagram Caption Generator", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(captions) { cap ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(cap, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                            IconButton(onClick = { clipboardManager.setText(AnnotatedString(cap)) }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AIStoryGeneratorScreen(onBack: () -> Unit) {
    var genre by remember { mutableStateOf("Sci-Fi") }
    var prompt by remember { mutableStateOf("A curious botanist discovers a glowing seedling on Mars") }
    var story by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "AI Story Generator", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(value = prompt, onValueChange = { prompt = it }, label = { Text("Story Prompt / Characters") }, modifier = Modifier.fillMaxWidth())

            Button(
                onClick = {
                    scope.launch {
                        isGenerating = true
                        delay(600)
                        story = "Under the vermilion sky of the Martian crater, Dr. Elena noticed a faint emerald pulse beneath the permafrost. The seedling didn't thrive on nitrogen or water—it hummed in harmonic frequency with the planet's core. As she reached out with her sensor probe, the crystal leaves unfurled, whispering ancient secrets of a forgotten green galaxy."
                        isGenerating = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AutoStories, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isGenerating) "Writing Story..." else "Generate Story")
            }

            if (story.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(story, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }
        }
    }
}

@Composable
fun PhotoCollageScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Photo Collage Maker", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Select Collage Layout Grid", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            // Grid mockup
            Row(modifier = Modifier.fillMaxWidth().height(160.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(modifier = Modifier.weight(1f).fillMaxHeight(), shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                    Box(contentAlignment = Alignment.Center) { Text("Photo 1 (Hero)", fontWeight = FontWeight.Bold) }
                }
                Column(modifier = Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(modifier = Modifier.fillMaxWidth().weight(1f), shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
                        Box(contentAlignment = Alignment.Center) { Text("Photo 2") }
                    }
                    Surface(modifier = Modifier.fillMaxWidth().weight(1f), shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.tertiaryContainer) {
                        Box(contentAlignment = Alignment.Center) { Text("Photo 3") }
                    }
                }
            }
            Text("Tap to insert photos from gallery into 3-grid frame.", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun VideoScriptGeneratorScreen(onBack: () -> Unit) {
    var topic by remember { mutableStateOf("3 productivity habits that will change your life") }
    var script by remember { mutableStateOf("") }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Short Video Script Generator", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(value = topic, onValueChange = { topic = it }, label = { Text("Reel / Short Video Topic") }, modifier = Modifier.fillMaxWidth())

            Button(
                onClick = {
                    script = """
                        🎬 60-SECOND REEL SCRIPT:
                        
                        [0:00 - 0:03] THE HOOK:
                        "Stop scrolling! If you feel like 24 hours isn't enough, here are 3 habits to save 10 hours this week."
                        
                        [0:03 - 0:45] CORE VALUE:
                        1. The 2-Minute Rule: If it takes under 2 mins, do it right now.
                        2. Time-Boxing: Schedule your day in 30-min focused blocks.
                        3. Digital Sunset: Turn off notifications 1 hour before bed.
                        
                        [0:45 - 0:60] CALL TO ACTION:
                        "Save this reel for tomorrow morning and drop a 🔥 in the comments!"
                    """.trimIndent()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Videocam, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate Hook + Body + CTA Script")
            }

            if (script.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(script, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceJournalScreen(onBack: () -> Unit) {
    var mood by remember { mutableStateOf("Grateful") }
    var journalNotes by remember { mutableStateOf("Today was very productive. Completed the LifeHub application architecture, took a walk in the evening, and read 20 pages of a great book.") }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Voice Journal", canNavigateBack = true, onNavigateBack = onBack) }
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
                    Text("Today's Reflection • Mood: $mood 😊", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("🎙️ Audio Journal Recorded (02:45)", style = MaterialTheme.typography.bodySmall)
                }
            }

            OutlinedTextField(
                value = journalNotes,
                onValueChange = { journalNotes = it },
                label = { Text("Journal Notes & Transcript") },
                minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun LifeOrganizerScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = { LifeHubTopAppBar(title = "AI Life Organizer", canNavigateBack = true, onNavigateBack = onBack) }
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
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🌟 LifeHub 360° Daily Pulse", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("• Tasks Completed: 3 of 4 today\n• Water Intake: 1,750 / 2,500 ml\n• Daily Budget: ₹250 spent (Under ₹1,500 limit)\n• Active Streaks: Reading (14 days), Walking (7 days)", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("💡 AI Recommendation for Tonight", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("You've met 80% of your daily goals! Wind down with a 5-minute meditation and log your sleep journal by 11 PM for optimal recovery.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
