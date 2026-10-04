package com.example.ui.tools.finance

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.LifeHubApplication
import com.example.data.local.entity.BillReminderEntity
import com.example.data.local.entity.ExpenseEntity
import com.example.data.local.entity.InvestmentEntity
import com.example.data.local.entity.SavingsGoalEntity
import com.example.ui.components.LifeHubTopAppBar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ExpenseTrackerScreen(onBack: () -> Unit) {
    val repository = remember { LifeHubApplication.instance.repository }
    val scope = rememberCoroutineScope()
    val expenses by repository.getAllExpenses().collectAsStateWithLifecycle(initialValue = emptyList())
    val totalExpense by repository.getTotalExpenses().collectAsStateWithLifecycle(initialValue = 0.0)

    var showAddDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Food") }

    val categories = listOf("Food", "Transport", "Shopping", "Bills", "Health", "Other")

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Daily Expense Tracker", canNavigateBack = true, onNavigateBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_expense_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Total Spent", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "₹${"%.2f".format(totalExpense ?: 0.0)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("All Expenses (${expenses.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))

            if (expenses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No expenses logged yet. Tap + to add one!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(expenses, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                                    Text("${item.category} • ${item.date}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text("₹${"%.2f".format(item.amount)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                                IconButton(onClick = { scope.launch { repository.deleteExpense(item) } }) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Log New Expense") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title (e.g. Grocery, Petrol)") },
                        modifier = Modifier.fillMaxWidth().testTag("expense_title_input")
                    )
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth().testTag("expense_amount_input")
                    )
                    Text("Category", style = MaterialTheme.typography.labelMedium)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        categories.take(3).forEach { cat ->
                            FilterChip(selected = category == cat, onClick = { category = cat }, label = { Text(cat) })
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        categories.drop(3).forEach { cat ->
                            FilterChip(selected = category == cat, onClick = { category = cat }, label = { Text(cat) })
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsedAmount = amount.toDoubleOrNull() ?: 0.0
                        if (title.isNotBlank() && parsedAmount > 0) {
                            val today = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                            scope.launch {
                                repository.addExpense(
                                    ExpenseEntity(title = title, amount = parsedAmount, category = category, date = today)
                                )
                            }
                            title = ""
                            amount = ""
                            showAddDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_expense_btn")
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun FamilyBudgetScreen(onBack: () -> Unit) {
    var monthlyBudget by remember { mutableStateOf("45000") }
    var currentSpent by remember { mutableStateOf("26500") }

    val budget = monthlyBudget.toDoubleOrNull() ?: 1.0
    val spent = currentSpent.toDoubleOrNull() ?: 0.0
    val remaining = (budget - spent).coerceAtLeast(0.0)
    val progress = (spent / budget).toFloat().coerceIn(0f, 1f)

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Family Budget Manager", canNavigateBack = true, onNavigateBack = onBack) }
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
                value = monthlyBudget,
                onValueChange = { monthlyBudget = it },
                label = { Text("Monthly Budget (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = currentSpent,
                onValueChange = { currentSpent = it },
                label = { Text("Current Spent (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Budget Health", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                        color = if (progress > 0.85f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Spent: ₹${"%.0f".format(spent)}")
                        Text("Remaining: ₹${"%.0f".format(remaining)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Text("Usage: ${(progress * 100).toInt()}% of budget utilized", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun BillReminderScreen(onBack: () -> Unit) {
    val repository = remember { LifeHubApplication.instance.repository }
    val scope = rememberCoroutineScope()
    val bills by repository.getAllBills().collectAsStateWithLifecycle(initialValue = emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var billName by remember { mutableStateOf("") }
    var billAmount by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("10th of every month") }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Bill Reminder", canNavigateBack = true, onNavigateBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Add Bill")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Upcoming & Active Bills", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            if (bills.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No bills saved yet. Tap + to add electricity, wifi, rent...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(bills, key = { it.id }) { bill ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(bill.title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                                    Text("Due: ${bill.dueDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text("₹${"%.2f".format(bill.amount)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                IconButton(onClick = { scope.launch { repository.deleteBill(bill) } }) {
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
            title = { Text("Add Bill Reminder") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = billName, onValueChange = { billName = it }, label = { Text("Bill Name (e.g. WiFi, Rent)") })
                    OutlinedTextField(value = billAmount, onValueChange = { billAmount = it }, label = { Text("Amount (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    OutlinedTextField(value = dueDate, onValueChange = { dueDate = it }, label = { Text("Due Date / Cycle") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = billAmount.toDoubleOrNull() ?: 0.0
                        if (billName.isNotBlank()) {
                            scope.launch {
                                repository.addBill(BillReminderEntity(title = billName, amount = amt, dueDate = dueDate, category = "Utility"))
                            }
                            billName = ""
                            billAmount = ""
                            showDialog = false
                        }
                    }
                ) { Text("Save Bill") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun SavingsGoalScreen(onBack: () -> Unit) {
    val repository = remember { LifeHubApplication.instance.repository }
    val scope = rememberCoroutineScope()
    val goals by repository.getAllSavingsGoals().collectAsStateWithLifecycle(initialValue = emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var goalTitle by remember { mutableStateOf("") }
    var targetAmt by remember { mutableStateOf("50000") }
    var currentAmt by remember { mutableStateOf("15000") }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Savings Goals", canNavigateBack = true, onNavigateBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Add Goal")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Your Financial Goals", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            if (goals.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No goals added. Tap + to set targets for Emergency Fund, New Laptop, Vacation...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(goals, key = { it.id }) { goal ->
                        val pct = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f) else 0f
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(goal.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text("${(pct * 100).toInt()}%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                                LinearProgressIndicator(
                                    progress = { pct },
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Saved: ₹${"%.0f".format(goal.currentAmount)}", style = MaterialTheme.typography.bodySmall)
                                    Text("Target: ₹${"%.0f".format(goal.targetAmount)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
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
            title = { Text("New Savings Goal") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = goalTitle, onValueChange = { goalTitle = it }, label = { Text("Goal Title (e.g. New Phone)") })
                    OutlinedTextField(value = targetAmt, onValueChange = { targetAmt = it }, label = { Text("Target Amount (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    OutlinedTextField(value = currentAmt, onValueChange = { currentAmt = it }, label = { Text("Current Saved (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val t = targetAmt.toDoubleOrNull() ?: 1.0
                        val c = currentAmt.toDoubleOrNull() ?: 0.0
                        if (goalTitle.isNotBlank()) {
                            scope.launch {
                                repository.addSavingsGoal(SavingsGoalEntity(title = goalTitle, targetAmount = t, currentAmount = c, targetDate = "2026-12-31"))
                            }
                            goalTitle = ""
                            showDialog = false
                        }
                    }
                ) { Text("Save Goal") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun SplitBillScreen(onBack: () -> Unit) {
    var billTotal by remember { mutableStateOf("3600") }
    var numberOfPeople by remember { mutableStateOf("4") }
    var membersNote by remember { mutableStateOf("Raj, Parth, Amit, Priya") }

    val total = billTotal.toDoubleOrNull() ?: 0.0
    val count = (numberOfPeople.toIntOrNull() ?: 1).coerceAtLeast(1)
    val perPerson = total / count

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Split Bill", canNavigateBack = true, onNavigateBack = onBack) }
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
                value = billTotal,
                onValueChange = { billTotal = it },
                label = { Text("Total Bill Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = numberOfPeople,
                onValueChange = { numberOfPeople = it },
                label = { Text("Number of People") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = membersNote,
                onValueChange = { membersNote = it },
                label = { Text("Member Names / Note") },
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Each Person Pays", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("₹${"%.2f".format(perPerson)}", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Split among $count people equally", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
