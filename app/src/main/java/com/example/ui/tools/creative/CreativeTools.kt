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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.LifeHubTopAppBar

@Composable
fun StatusCaptionScreen(onBack: () -> Unit) {
    val clipboardManager = LocalClipboardManager.current
    var category by remember { mutableStateOf("Motivation") }
    val categories = listOf("Motivation", "Attitude", "Life", "Success")

    val captionsMap = mapOf(
        "Motivation" to listOf(
            "Every champion was once a contender that refused to give up. 🏆",
            "Small daily improvements over time lead to stunning results. 📈",
            "Turn your wounds into wisdom and your doubts into fuel. 🔥"
        ),
        "Attitude" to listOf(
            "I don't follow crowds, I create my own path. 🚶‍♂️✨",
            "Be a voice, not an echo. Silent moves, loud results. ⚡",
            "Confidence isn't walking into a room thinking you're better than everyone; it's walking in not needing to compare."
        ),
        "Life" to listOf(
            "Life is 10% what happens to you and 90% how you react to it. 🌱",
            "Enjoy the little things, for one day you may look back and realize they were the big things.",
            "Simplicity is the ultimate sophistication."
        ),
        "Success" to listOf(
            "Success is the sum of small efforts, repeated day in and day out.",
            "Don't wish it were easier; wish you were better.",
            "Discipline equals freedom."
        )
    )

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Status Caption Generator", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEach { cat ->
                    FilterChip(selected = category == cat, onClick = { category = cat }, label = { Text(cat) })
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(captionsMap[category] ?: emptyList()) { cap ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(cap, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
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
fun QuoteMakerScreen(onBack: () -> Unit) {
    var quoteText by remember { mutableStateOf("The future belongs to those who believe in the beauty of their dreams.") }
    var author by remember { mutableStateOf("Eleanor Roosevelt") }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Quote Maker", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Visual Card Preview with gradient
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF4F46E5), Color(0xFF7C3AED), Color(0xFFEC4899))
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("“$quoteText”", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("— $author", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f), fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            OutlinedTextField(value = quoteText, onValueChange = { quoteText = it }, label = { Text("Quote Text") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Author / Attribution") }, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun BirthdayInvitationScreen(onBack: () -> Unit) {
    var name by remember { mutableStateOf("Aarav") }
    var age by remember { mutableStateOf("25") }
    var date by remember { mutableStateOf("Saturday, 24th October 2026") }
    var venue by remember { mutableStateOf("Skyline Lounge, Cyber Hub") }
    val clipboardManager = LocalClipboardManager.current

    val inviteText = """
        🎉 YOU ARE INVITED! 🎂
        Join us to celebrate $name's ${age}th Birthday!
        
        📅 Date: $date
        📍 Venue: $venue
        ⏰ Time: 07:30 PM Onwards
        
        RSVP: +91 98765 43210
        See you there for great food, music & memories! 🎈
    """.trimIndent()

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Birthday Invitation Maker", canNavigateBack = true, onNavigateBack = onBack) }
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
                    Text("Invitation Preview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(inviteText, style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = { clipboardManager.setText(AnnotatedString(inviteText)) }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Invitation")
                    }
                }
            }

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Birthday Person Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Turning Age") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date & Time") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = venue, onValueChange = { venue = it }, label = { Text("Venue Location") }, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun FestivalPosterScreen(onBack: () -> Unit) {
    var festival by remember { mutableStateOf("Diwali") }
    var senderName by remember { mutableStateOf("The Sharma Family") }

    val festivals = listOf("Diwali", "Holi", "Eid", "New Year", "Christmas")

    val greetingsMap = mapOf(
        "Diwali" to "✨ Happy Deepavali! May the divine light of Diwali illuminate your life with peace, prosperity, health and boundless happiness! 🪔✨",
        "Holi" to "🎨 Wishing you a vibrant, joyful and colorful Holi filled with laughter and sweet moments! 🌈",
        "Eid" to "🌙 Eid Mubarak! May this blessed day bring joy, harmony, and peace to your home and loved ones. ✨",
        "New Year" to "🎆 Happy New Year 2027! Here's to 365 new chances to learn, achieve and celebrate! 🥂",
        "Christmas" to "🎄 Merry Christmas! May the season bring warmth, love and cheer to your family! 🎅"
    )

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Festival Poster Maker", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                festivals.forEach { fest ->
                    FilterChip(selected = festival == fest, onClick = { festival = fest }, label = { Text(fest) })
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFF59E0B), Color(0xFFEF4444), Color(0xFF8B5CF6))
                            )
                        )
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(greetingsMap[festival] ?: "", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Warm wishes from: $senderName", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.95f), fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            OutlinedTextField(value = senderName, onValueChange = { senderName = it }, label = { Text("Sender / Family Name") }, modifier = Modifier.fillMaxWidth())
        }
    }
}
