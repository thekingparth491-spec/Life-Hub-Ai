package com.example.ui.tools.daily

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.domain.model.Tool
import com.example.ui.components.IconHelper
import com.example.ui.components.LifeHubTopAppBar
import java.util.*
import kotlin.math.abs

@Composable
fun DailyHubScreen(
    onNavigateToTool: (String) -> Unit,
    onBack: () -> Unit
) {
    val quickTools = listOf(
        "tool_calculator" to ("Smart Calculator" to "calculate"),
        "tool_unit_converter" to ("Unit Converter" to "sync_alt"),
        "tool_age_calc" to ("Age Calculator" to "cake"),
        "tool_date_diff" to ("Date Difference" to "date_range"),
        "tool_gst_calc" to ("GST Calculator" to "receipt_long"),
        "tool_emi_calc" to ("EMI Calculator" to "account_balance"),
        "tool_percentage_calc" to ("Percentage" to "percent"),
        "tool_tip_calc" to ("Tip Calculator" to "local_dining"),
        "tool_timezone_calc" to ("Time Zone" to "schedule")
    )

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "All-in-One Utility Hub", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Essential Daily Utilities",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Access all fast calculation and conversion utilities instantly without leaving the hub.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            quickTools.forEach { (route, pair) ->
                val (title, iconName) = pair
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onNavigateToTool(route) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = IconHelper.getIcon(iconName),
                                    contentDescription = title,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DateDifferenceScreen(onBack: () -> Unit) {
    var startDay by remember { mutableStateOf("1") }
    var startMonth by remember { mutableStateOf("1") }
    var startYear by remember { mutableStateOf("2026") }

    var endDay by remember { mutableStateOf("15") }
    var endMonth by remember { mutableStateOf("8") }
    var endYear by remember { mutableStateOf("2026") }

    var totalDays by remember { mutableStateOf(0L) }
    var diffSummary by remember { mutableStateOf("") }

    fun calculate() {
        val sD = startDay.toIntOrNull() ?: 1
        val sM = startMonth.toIntOrNull() ?: 1
        val sY = startYear.toIntOrNull() ?: 2026

        val eD = endDay.toIntOrNull() ?: 1
        val eM = endMonth.toIntOrNull() ?: 1
        val eY = endYear.toIntOrNull() ?: 2026

        val cal1 = Calendar.getInstance().apply { set(sY, sM - 1, sD, 0, 0, 0) }
        val cal2 = Calendar.getInstance().apply { set(eY, eM - 1, eD, 0, 0, 0) }

        val diffMs = abs(cal2.timeInMillis - cal1.timeInMillis)
        totalDays = diffMs / (1000 * 60 * 60 * 24)
        val weeks = totalDays / 7
        val remDays = totalDays % 7
        val approxMonths = totalDays / 30

        diffSummary = "$totalDays total days ($weeks weeks & $remDays days, ~${approxMonths} months)"
    }

    LaunchedEffect(Unit) { calculate() }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Date Difference Calculator", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Start Date", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = startDay, onValueChange = { startDay = it; calculate() }, label = { Text("Day") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = startMonth, onValueChange = { startMonth = it; calculate() }, label = { Text("Month") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = startYear, onValueChange = { startYear = it; calculate() }, label = { Text("Year") }, modifier = Modifier.weight(1.5f))
            }

            Text("End Date", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = endDay, onValueChange = { endDay = it; calculate() }, label = { Text("Day") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = endMonth, onValueChange = { endMonth = it; calculate() }, label = { Text("Month") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = endYear, onValueChange = { endYear = it; calculate() }, label = { Text("Year") }, modifier = Modifier.weight(1.5f))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Calculated Difference", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(diffSummary, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun PercentageCalculatorScreen(onBack: () -> Unit) {
    var mode by remember { mutableStateOf(0) } // 0: X% of Y, 1: X is what % of Y, 2: % Increase/Decrease
    var numA by remember { mutableStateOf("15") }
    var numB by remember { mutableStateOf("250") }

    val a = numA.toDoubleOrNull() ?: 0.0
    val b = numB.toDoubleOrNull() ?: 0.0

    val result = when (mode) {
        0 -> (a * b) / 100.0
        1 -> if (b != 0.0) (a / b) * 100.0 else 0.0
        else -> if (a != 0.0) ((b - a) / a) * 100.0 else 0.0
    }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Percentage Calculator", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = mode == 0, onClick = { mode = 0 }, label = { Text("X% of Y") })
                FilterChip(selected = mode == 1, onClick = { mode = 1 }, label = { Text("X is what % of Y") })
                FilterChip(selected = mode == 2, onClick = { mode = 2 }, label = { Text("% Change") })
            }

            OutlinedTextField(
                value = numA,
                onValueChange = { numA = it },
                label = { Text(if (mode == 0) "Percentage (X%)" else if (mode == 1) "Value (X)" else "Original Value (A)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = numB,
                onValueChange = { numB = it },
                label = { Text(if (mode == 0) "Of Value (Y)" else if (mode == 1) "Total Value (Y)" else "New Value (B)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Result", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    val resStr = when (mode) {
                        0 -> "$a% of $b = ${"%.2f".format(result)}"
                        1 -> "$a is ${"%.2f".format(result)}% of $b"
                        else -> "Change from $a to $b: ${"%.2f".format(result)}%"
                    }
                    Text(resStr, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
