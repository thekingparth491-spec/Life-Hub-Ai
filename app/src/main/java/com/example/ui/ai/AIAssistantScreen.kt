package com.example.ui.ai

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
import com.example.LifeHubApplication
import com.example.data.local.entity.ExpenseEntity
import com.example.data.local.entity.NoteEntity
import com.example.data.remote.GeminiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val actionPayload: ActionProposal? = null
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

    var inputText by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }

    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    sender = "ai",
                    text = "👋 **Welcome to LifeHub AI Assistant!**\n\nI am connected to all 100 tools across Finance, Utilities, Health, Safety, and Studies. How can I help you right now?\n\n• 💸 **Log an expense**: *\"Add ₹450 dinner\"*\n• 🧮 **Solve calculations**: *\"15% of ₹2,400\"* or *\"10 km to miles\"*\n• 📝 **Draft notes & tasks**: *\"Note down project ideas\"*\n• ⚡ **Direct tool control**: Ask me to open any tool!"
                )
            )
        )
    }

    var pendingAction by remember { mutableStateOf<ActionProposal?>(null) }
    var actionSnackbarMessage by remember { mutableStateOf<String?>(null) }

    val quickSuggestions = listOf(
        "Add ₹500 petrol expense",
        "Convert 15 km to miles",
        "18% GST on ₹4,500",
        "Pomodoro study routine",
        "Car tyre care checklist",
        "Daily water intake plan",
        "Emergency SOS shortcuts"
    )

    fun processUserMessage(query: String) {
        val cleanQuery = query.trim()
        if (cleanQuery.isBlank() || isThinking) return

        val userMsg = ChatMessage(sender = "user", text = cleanQuery)
        messages = messages + userMsg
        inputText = ""

        scope.launch {
            isThinking = true
            delay(100)
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size - 1)
            }

            val q = cleanQuery.lowercase()

            // Check for direct actionable intents
            val actionPayload: ActionProposal? = when {
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

                q.contains("note") || q.contains("remind") -> {
                    ActionProposal(
                        title = "Save Note",
                        description = "Store \"$cleanQuery\" in Notes Organizer",
                        actionType = "SAVE_NOTE",
                        note = cleanQuery
                    )
                }

                else -> null
            }

            // Generate AI response with Gemini / Local fallback
            val responseText = GeminiService.generateResponse(
                cleanQuery,
                systemInstruction = "You are LifeHub AI, an intelligent, helpful, and concise assistant embedded in an all-in-one Android super app with 100 tools. Provide clear, direct answers with bullet points and friendly emojis."
            )

            val aiResponse = ChatMessage(
                sender = "ai",
                text = responseText,
                actionPayload = actionPayload
            )

            messages = messages + aiResponse
            isThinking = false
            delay(100)
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size - 1)
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
                                            .background(Color(0xFF10B981))
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "Online • 100 Tools Connected",
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
                        IconButton(
                            onClick = {
                                messages = listOf(
                                    ChatMessage(
                                        sender = "ai",
                                        text = "Chat cleared! How can I assist you with LifeHub's tools today?"
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
            // Quick suggestions horizontal pills
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
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
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
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
                                                    if (action.actionType == "NAVIGATE_TOOL") {
                                                        onNavigateToTool(action.targetRoute)
                                                    } else {
                                                        pendingAction = action
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
                                                    imageVector = if (action.actionType == "NAVIGATE_TOOL") Icons.Default.Launch else Icons.Default.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = if (action.actionType == "NAVIGATE_TOOL") "Open Tool" else "Confirm & Save",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }

                                // Copy Response Tool
                                if (isAi) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
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
                                text = "LifeHub AI is reasoning...",
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
                    // Voice prompt helper icon
                    IconButton(
                        onClick = {
                            inputText = "Log ₹500 petrol expense"
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Dictation",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                "Ask AI, record expense, convert...",
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
