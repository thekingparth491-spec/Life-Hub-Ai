package com.example.ui.ai

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.LifeHubApplication
import com.example.data.local.entity.ExpenseEntity
import com.example.data.local.entity.NoteEntity
import com.example.data.remote.GeminiService
import com.example.data.remote.VoiceEngine
import com.example.data.remote.VoiceService
import com.example.util.DeviceHardwareHelper
import com.example.util.GalleryPhotoItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val actionPayload: ActionProposal? = null,
    val photosList: List<GalleryPhotoItem>? = null
)

data class ActionProposal(
    val title: String,
    val description: String,
    val actionType: String,
    val amount: Double = 0.0,
    val note: String = "",
    val targetRoute: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAssistantScreen(
    onNavigateToTool: (String) -> Unit = {},
    onBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val repository = remember {
        try {
            LifeHubApplication.instance.repository
        } catch (e: Exception) {
            (context.applicationContext as? LifeHubApplication)?.repository
                ?: throw IllegalStateException("LifeHubApplication repository not ready")
        }
    }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val clipboardManager = LocalClipboardManager.current

    // Voice Service for ElevenLabs and Android Natural TTS
    val voiceService = remember { VoiceService(context) }
    var isSpeaking by remember { mutableStateOf(false) }
    var selectedVoice by remember { mutableStateOf(VoiceEngine.ELEVEN_LABS_RACHEL) }
    var showVoiceDialog by remember { mutableStateOf(false) }
    var autoSpeakResponses by remember { mutableStateOf(false) }

    DisposableEffect(voiceService) {
        voiceService.isSpeakingCallback = { speaking ->
            isSpeaking = speaking
        }
        onDispose {
            voiceService.release()
        }
    }

    // Device Hardware Live Telemetry
    var batteryInfo by remember { mutableStateOf(DeviceHardwareHelper.getBatteryInfo(context)) }
    var storageInfo by remember { mutableStateOf(DeviceHardwareHelper.getStorageInfo()) }
    var hasStoragePermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
            } else {
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            }
        )
    }

    var showTelemetryBar by remember { mutableStateOf(true) }

    // Speech Recognizer for Voice Input
    var isListening by remember { mutableStateOf(false) }
    val speechRecognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else null
    }

    var inputText by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }

    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    sender = "ai",
                    text = "👋 **Welcome to LifeHub AI Assistant!**\n\nI am your unified phone brain equipped with **ElevenLabs AI Voice**, real device telemetry, and 100 tools:\n\n• 📸 **Find Photos**: *\"Find my photos in my gallery\"*\n• 🔋 **Battery Telemetry**: *\"How is my phone battery?\"*\n• 💾 **Storage Telemetry**: *\"Check available storage space\"*\n• 💸 **Log Expenses**: *\"Add ₹500 petrol expense\"*\n• 🧮 **Solve Math**: *\"18% GST on ₹4,500\"* or *\"15 km to miles\"*\n• 🗣️ **Voice Talking**: Tap the speaker icon on any message to hear me speak with ElevenLabs or Android TTS!"
                )
            )
        )
    }

    var pendingAction by remember { mutableStateOf<ActionProposal?>(null) }
    var actionSnackbarMessage by remember { mutableStateOf<String?>(null) }

    // Permission launcher for Storage & Photos
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.any { it }
        hasStoragePermission = granted
        if (granted) {
            actionSnackbarMessage = "✅ Full phone storage & photo access granted!"
            storageInfo = DeviceHardwareHelper.getStorageInfo()
        } else {
            actionSnackbarMessage = "Storage permission was not granted."
        }
    }

    // Permission launcher for Voice Dictation (Microphone)
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && speechRecognizer != null) {
            try {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to LifeHub AI...")
                }
                speechRecognizer.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: android.os.Bundle?) { isListening = true }
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() { isListening = false }
                    override fun onError(error: Int) { isListening = false }
                    override fun onResults(results: android.os.Bundle?) {
                        isListening = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            inputText = matches[0]
                        }
                    }
                    override fun onPartialResults(partialResults: android.os.Bundle?) {}
                    override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
                })
                speechRecognizer.startListening(intent)
            } catch (e: Exception) {
                isListening = false
            }
        } else {
            actionSnackbarMessage = "Microphone permission is needed for voice input."
        }
    }

    val quickSuggestions = listOf(
        "Find my photos in my gallery",
        "How is my battery and storage?",
        "Add ₹500 petrol expense",
        "Convert 15 km to miles",
        "18% GST on ₹4,500",
        "Pomodoro study routine",
        "Emergency SOS shortcuts"
    )

    fun processUserMessage(query: String) {
        val cleanQuery = query.trim()
        if (cleanQuery.isBlank() || isThinking) return

        val userMsg = ChatMessage(sender = "user", text = cleanQuery)
        messages = messages + userMsg
        inputText = ""

        // Update live battery & storage telemetry
        batteryInfo = DeviceHardwareHelper.getBatteryInfo(context)
        storageInfo = DeviceHardwareHelper.getStorageInfo()

        scope.launch {
            isThinking = true
            delay(100)
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size - 1)
            }

            val q = cleanQuery.lowercase()

            // 1. Check for Gallery Photos Search intent
            var foundPhotos: List<GalleryPhotoItem>? = null
            if (q.contains("photo") || q.contains("gallery") || q.contains("pictures") || q.contains("images")) {
                if (hasStoragePermission) {
                    foundPhotos = DeviceHardwareHelper.searchGalleryPhotos(context, limit = 8)
                }
            }

            // 2. Check for direct actionable intents
            val actionPayload: ActionProposal? = when {
                (q.contains("photo") || q.contains("gallery")) && !hasStoragePermission -> {
                    ActionProposal(
                        title = "Allow Storage & Photos Access",
                        description = "Grant storage permission to find photos in your phone gallery",
                        actionType = "REQUEST_STORAGE_PERMISSION"
                    )
                }

                q.contains("expense") || (q.contains("add") && (q.contains("₹") || q.contains("rs") || q.contains("spend") || q.contains("petrol") || q.contains("grocery") || q.contains("dinner") || q.contains("lunch"))) -> {
                    val numbers = Regex("\\d+").findAll(q).map { it.value.toDoubleOrNull() ?: 0.0 }.toList()
                    val amount = numbers.firstOrNull() ?: 500.0
                    val title = when {
                        q.contains("petrol") -> "Petrol / Fuel"
                        q.contains("grocery") -> "Groceries"
                        q.contains("dinner") -> "Dinner Food"
                        q.contains("lunch") -> "Lunch"
                        else -> "Daily Expense"
                    }
                    ActionProposal(
                        title = "Log ₹${amount.toInt()} Expense",
                        description = "Save ₹${amount.toInt()} for '$title' into your Expense Tracker",
                        actionType = "ADD_EXPENSE",
                        amount = amount,
                        note = title
                    )
                }

                q.contains("battery") -> {
                    ActionProposal(
                        title = "Open Battery Monitor",
                        description = "View real-time temperature, voltage & charge telemetry",
                        actionType = "NAVIGATE_TOOL",
                        targetRoute = "tool_battery_monitor"
                    )
                }

                q.contains("storage") -> {
                    ActionProposal(
                        title = "Open Storage Analyzer",
                        description = "Analyze free space, large files & redundant cache",
                        actionType = "NAVIGATE_TOOL",
                        targetRoute = "tool_storage_analyzer"
                    )
                }

                q.contains("calculator") || (q.contains("calculate") && (q.contains("+") || q.contains("-") || q.contains("*") || q.contains("/"))) -> {
                    ActionProposal(
                        title = "Open Smart Calculator",
                        description = "Launch calculation history & scientific tools",
                        actionType = "NAVIGATE_TOOL",
                        targetRoute = "tool_calculator"
                    )
                }

                q.contains("emi") || q.contains("loan") -> {
                    ActionProposal(
                        title = "Open EMI Calculator",
                        description = "Calculate repayment schedules & interest breakdowns",
                        actionType = "NAVIGATE_TOOL",
                        targetRoute = "tool_emi_calc"
                    )
                }

                q.contains("convert") || q.contains("unit") -> {
                    ActionProposal(
                        title = "Open Unit Converter",
                        description = "Convert Length, Weight, Area, Speed, Volume",
                        actionType = "NAVIGATE_TOOL",
                        targetRoute = "tool_unit_converter"
                    )
                }

                q.contains("study") || q.contains("pomodoro") -> {
                    ActionProposal(
                        title = "Open Pomodoro Focus Timer",
                        description = "Start a 25-minute deep focus study session",
                        actionType = "NAVIGATE_TOOL",
                        targetRoute = "tool_pomodoro_timer"
                    )
                }

                q.contains("water") || q.contains("drink") -> {
                    ActionProposal(
                        title = "Open Water Reminder",
                        description = "Log your glasses and track daily 2,500 ml target",
                        actionType = "NAVIGATE_TOOL",
                        targetRoute = "tool_water_reminder"
                    )
                }

                q.contains("sos") || q.contains("emergency") -> {
                    ActionProposal(
                        title = "Open Emergency SOS",
                        description = "1-Tap 112 calling, siren strobe & location broadcast",
                        actionType = "NAVIGATE_TOOL",
                        targetRoute = "tool_emergency_sos"
                    )
                }

                else -> null
            }

            // Generate AI response with Gemini / Local fallback passing device context
            val responseText = GeminiService.generateResponse(
                userPrompt = cleanQuery,
                systemInstruction = "You are LifeHub AI, an intelligent, helpful, and concise assistant embedded in an all-in-one Android super app with 100 tools. Answer based specifically on the user's text. Avoid generic repeated answers. Use bullet points and friendly emojis.",
                context = context
            )

            val aiResponse = ChatMessage(
                sender = "ai",
                text = responseText,
                actionPayload = actionPayload,
                photosList = foundPhotos
            )

            messages = messages + aiResponse
            isThinking = false
            delay(100)
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size - 1)
            }

            // Auto-speak if enabled
            if (autoSpeakResponses) {
                voiceService.speak(responseText, selectedVoice)
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFF4F46E5),
                                                Color(0xFF7C3AED)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AI Core",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "LifeHub AI",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (isSpeaking) Color(0xFF3B82F6) else Color(0xFF10B981))
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (isSpeaking) "Speaking with AI Voice..." else "Online • ElevenLabs & 100 Tools",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    },
                    navigationIcon = {
                        if (onBack != null) {
                            IconButton(onClick = onBack) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    },
                    actions = {
                        // Voice Engine Selector Button
                        IconButton(onClick = { showVoiceDialog = true }) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.RecordVoiceOver,
                                contentDescription = "AI Voice Settings",
                                tint = if (isSpeaking) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Toggle Live Telemetry panel
                        IconButton(onClick = { showTelemetryBar = !showTelemetryBar }) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Device Metrics",
                                tint = if (showTelemetryBar) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Reset Chat
                        IconButton(
                            onClick = {
                                voiceService.stop()
                                messages = listOf(
                                    ChatMessage(
                                        sender = "ai",
                                        text = "Chat cleared! How can I assist you right now? Try saying: *\"Find my photos in my gallery\"* or *\"Show battery status\"*."
                                    )
                                )
                            }
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Clear Chat",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
        ) {
            // Live Device Hardware Panel (Battery, Storage, and Gallery Permission Status)
            AnimatedVisibility(visible = showTelemetryBar) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Battery Panel Metric
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { processUserMessage("How is my battery?") },
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (batteryInfo.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                                        contentDescription = "Battery",
                                        tint = if (batteryInfo.percentage > 20) MaterialTheme.colorScheme.primary else Color(0xFFEF4444),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = "${batteryInfo.percentage}% Battery",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (batteryInfo.isCharging) "Charging" else batteryInfo.health,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // Storage Panel Metric
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { processUserMessage("How much storage left?") },
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SdStorage,
                                        contentDescription = "Storage",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = "${String.format(Locale.getDefault(), "%.1f", storageInfo.freeGb)} GB Free",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${storageInfo.usedPercentage}% storage used",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // Photos Permission / Status Badge
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        if (hasStoragePermission) {
                                            processUserMessage("Find my photos in my gallery")
                                        } else {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                storagePermissionLauncher.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
                                            } else {
                                                storagePermissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
                                            }
                                        }
                                    },
                                color = if (hasStoragePermission) Color(0xFF10B981).copy(alpha = 0.15f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (hasStoragePermission) Color(0xFF10B981).copy(alpha = 0.4f) else MaterialTheme.colorScheme.error.copy(alpha = 0.4f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (hasStoragePermission) Icons.Default.PhotoLibrary else Icons.Default.Lock,
                                        contentDescription = "Photos",
                                        tint = if (hasStoragePermission) Color(0xFF059669) else MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (hasStoragePermission) "Gallery Active" else "Grant Access",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (hasStoragePermission) Color(0xFF059669) else MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick suggestions horizontal pills
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quickSuggestions) { suggestion ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { processUserMessage(suggestion) },
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(20.dp),
                        shadowElevation = 1.dp,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = if (suggestion.contains("photo")) Icons.Default.PhotoCamera else Icons.Default.Bolt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = suggestion,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Message Stream
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 4.dp, bottom = 12.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    val isAi = msg.sender == "ai"

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isAi) Arrangement.Start else Arrangement.End,
                        verticalAlignment = Alignment.Top
                    ) {
                        if (isAi) {
                            Surface(
                                modifier = Modifier
                                    .size(34.dp)
                                    .padding(top = 2.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.SmartToy,
                                        contentDescription = "AI",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                        }

                        Card(
                            modifier = Modifier
                                .widthIn(max = 320.dp)
                                .shadow(
                                    elevation = if (isAi) 2.dp else 4.dp,
                                    shape = RoundedCornerShape(
                                        topStart = if (isAi) 4.dp else 20.dp,
                                        topEnd = 20.dp,
                                        bottomStart = 20.dp,
                                        bottomEnd = if (isAi) 20.dp else 4.dp
                                    )
                                ),
                            shape = RoundedCornerShape(
                                topStart = if (isAi) 4.dp else 20.dp,
                                topEnd = 20.dp,
                                bottomStart = 20.dp,
                                bottomEnd = if (isAi) 20.dp else 4.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isAi) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary
                            ),
                            border = if (isAi) androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            ) else null
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        lineHeight = 22.sp
                                    ),
                                    color = if (isAi) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary
                                )

                                // Real Gallery Photos Carousel (when user asked to find photos)
                                msg.photosList?.let { photos ->
                                    if (photos.isNotEmpty()) {
                                        Text(
                                            text = "🖼️ Gallery Photos (${photos.size} found):",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            items(photos) { photo ->
                                                Card(
                                                    modifier = Modifier
                                                        .size(width = 110.dp, height = 130.dp)
                                                        .clip(RoundedCornerShape(10.dp)),
                                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                                ) {
                                                    Column {
                                                        AsyncImage(
                                                            model = photo.uri,
                                                            contentDescription = photo.displayName,
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .height(80.dp)
                                                                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)),
                                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                                        )
                                                        Column(modifier = Modifier.padding(4.dp)) {
                                                            Text(
                                                                text = photo.displayName,
                                                                style = MaterialTheme.typography.labelSmall,
                                                                fontWeight = FontWeight.SemiBold,
                                                                maxLines = 1,
                                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                                            )
                                                            Text(
                                                                text = photo.sizeFormatted,
                                                                style = MaterialTheme.typography.bodySmall,
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                fontSize = 10.sp
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                // Action Proposal Card if an executable action was detected
                                msg.actionPayload?.let { action ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isAi) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                            else MaterialTheme.colorScheme.surface
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.FlashOn,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = action.title,
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }

                                            Text(
                                                text = action.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )

                                            Button(
                                                onClick = {
                                                    when (action.actionType) {
                                                        "NAVIGATE_TOOL" -> {
                                                            onNavigateToTool(action.targetRoute)
                                                        }
                                                        "REQUEST_STORAGE_PERMISSION" -> {
                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                                storagePermissionLauncher.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
                                                            } else {
                                                                storagePermissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
                                                            }
                                                        }
                                                        else -> {
                                                            pendingAction = action
                                                        }
                                                    }
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(38.dp),
                                                shape = RoundedCornerShape(10.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.primary
                                                )
                                            ) {
                                                Icon(
                                                    imageVector = if (action.actionType == "NAVIGATE_TOOL") Icons.AutoMirrored.Filled.Launch else Icons.Default.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = if (action.actionType == "NAVIGATE_TOOL") "Open Tool"
                                                    else if (action.actionType == "REQUEST_STORAGE_PERMISSION") "Allow Storage & Photos"
                                                    else "Confirm & Save",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }

                                // Interactive Footer for AI Messages: Voice Speak + Copy
                                if (isAi) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Play AI Voice (ElevenLabs or Android Natural TTS)
                                        TextButton(
                                            onClick = {
                                                if (isSpeaking) {
                                                    voiceService.stop()
                                                } else {
                                                    scope.launch {
                                                        voiceService.speak(msg.text, selectedVoice)
                                                    }
                                                }
                                            },
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                                                contentDescription = "Speak",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                if (isSpeaking) "Stop" else "Speak AI Voice",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        // Copy to clipboard
                                        TextButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(msg.text))
                                                actionSnackbarMessage = "Copied response to clipboard!"
                                            },
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.ContentCopy,
                                                contentDescription = "Copy",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                "Copy",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (!isAi) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Surface(
                                modifier = Modifier
                                    .size(34.dp)
                                    .padding(top = 2.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "User",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                if (isThinking) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 44.dp, top = 4.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "LifeHub AI is analyzing query...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Snackbar notification banner
            AnimatedVisibility(visible = actionSnackbarMessage != null) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = actionSnackbarMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        IconButton(
                            onClick = { actionSnackbarMessage = null },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Dismiss",
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Elevated Modern Input Bar (Handles IME padding & Navigation bar padding)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Voice Dictation Microphone Button
                    IconButton(
                        onClick = {
                            val audioPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                            if (audioPermission == PackageManager.PERMISSION_GRANTED) {
                                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            } else {
                                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (isListening) Color(0xFFEF4444).copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Voice Input",
                            tint = if (isListening) Color(0xFFEF4444) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                "Find my photos, check battery, ask AI...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_input_field"),
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        maxLines = 3
                    )

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                processUserMessage(inputText)
                            }
                        },
                        enabled = inputText.isNotBlank() && !isThinking,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (inputText.isNotBlank() && !isThinking) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .testTag("ai_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (inputText.isNotBlank() && !isThinking) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    // Voice Engine Selection Dialog
    if (showVoiceDialog) {
        AlertDialog(
            onDismissRequest = { showVoiceDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Voice Talking Settings")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Choose which speech synthesis engine talks back to you:",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    VoiceEngine.values().forEach { voice ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedVoice = voice }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedVoice == voice,
                                onClick = { selectedVoice = voice }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = voice.displayName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (selectedVoice == voice) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Auto-speak AI replies aloud", style = MaterialTheme.typography.bodyMedium)
                        Switch(
                            checked = autoSpeakResponses,
                            onCheckedChange = { autoSpeakResponses = it }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showVoiceDialog = false
                        scope.launch {
                            voiceService.speak("Hello, LifeHub AI voice engine is active and ready to assist you!", selectedVoice)
                        }
                    }
                ) {
                    Text("Test & Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showVoiceDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Confirmation dialog before consequential actions (adding real database entries)
    pendingAction?.let { action ->
        AlertDialog(
            onDismissRequest = { pendingAction = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(action.title)
                }
            },
            text = {
                Text(
                    "LifeHub AI is ready to: ${action.description}\n\nWould you like to execute this action?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        when (action.actionType) {
                            "ADD_EXPENSE" -> {
                                val today = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                                scope.launch {
                                    try {
                                        repository.addExpense(
                                            ExpenseEntity(
                                                title = action.note,
                                                amount = action.amount,
                                                category = "General",
                                                date = today
                                            )
                                        )
                                        actionSnackbarMessage = "✅ Logged ₹${action.amount.toInt()} into Expense Tracker!"
                                    } catch (e: Exception) {
                                        actionSnackbarMessage = "Error saving expense: ${e.message}"
                                    }
                                }
                            }
                            "SAVE_NOTE" -> {
                                scope.launch {
                                    try {
                                        repository.addNote(
                                            NoteEntity(
                                                title = "AI Note",
                                                content = action.note,
                                                tag = "AI Assistant"
                                            )
                                        )
                                        actionSnackbarMessage = "✅ Saved to Notes Organizer!"
                                    } catch (e: Exception) {
                                        actionSnackbarMessage = "Error saving note: ${e.message}"
                                    }
                                }
                            }
                        }
                        pendingAction = null
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingAction = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
