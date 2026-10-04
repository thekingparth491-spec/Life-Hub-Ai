package com.example.ui.tools.finance

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
import com.example.data.local.entity.InvestmentEntity
import com.example.ui.components.LifeHubTopAppBar
import kotlinx.coroutines.launch
import kotlin.math.pow

@Composable
fun EMIReminderScreen(onBack: () -> Unit) {
    var emis by remember {
        mutableStateOf(
            listOf(
                Triple("HDFC Home Loan", 24500.0, "5th of each month"),
                Triple("Car Loan (SBI)", 8200.0, "10th of each month"),
                Triple("Personal Gadget EMI", 2150.0, "15th of each month")
            )
        )
    }
    var showDialog by remember { mutableStateOf(false) }
    var loanName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }

    val totalEmi = emis.sumOf { it.second }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "EMI Reminder", canNavigateBack = true, onNavigateBack = onBack) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                text = { Text("Add EMI") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
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
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Total Monthly EMI Commitment", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("₹${"%.0f".format(totalEmi)}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Active Loan EMIs (${emis.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                FilledTonalButton(onClick = { showDialog = true }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add New", style = MaterialTheme.typography.labelSmall)
                }
            }

            if (emis.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text("No EMIs scheduled. Tap '+ Add New' to track loan repayments.", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                }
            }

            emis.forEachIndexed { index, (name, amt, due) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("Due: $due", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("₹${"%.0f".format(amt)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        IconButton(onClick = {
                            emis = emis.filterIndexed { i, _ -> i != index }
                        }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Loan EMI Reminder") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = loanName,
                        onValueChange = { loanName = it },
                        label = { Text("Loan Title & Bank") },
                        placeholder = { Text("e.g. Education Loan (SBI)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Monthly EMI (₹)") },
                        placeholder = { Text("e.g. 6500") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        label = { Text("Due Day / Schedule") },
                        placeholder = { Text("e.g. 7th of each month") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsedAmt = amount.toDoubleOrNull() ?: 0.0
                        if (loanName.isNotBlank()) {
                            val due = if (dueDate.isBlank()) "Due on month end" else dueDate.trim()
                            emis = emis + Triple(loanName.trim(), parsedAmt, due)
                            loanName = ""
                            amount = ""
                            dueDate = ""
                            showDialog = false
                        }
                    },
                    enabled = loanName.isNotBlank()
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun SubscriptionTrackerScreen(onBack: () -> Unit) {
    var subs by remember {
        mutableStateOf(
            listOf(
                Triple("Netflix Premium", 649.0, "Renews on 12th Oct"),
                Triple("Spotify Duo", 149.0, "Renews on 18th Oct"),
                Triple("Amazon Prime", 1499.0, "Annual • Renews 24 Jan"),
                Triple("Google One Cloud (100GB)", 130.0, "Renews on 5th Oct")
            )
        )
    }
    var showDialog by remember { mutableStateOf(false) }
    var serviceName by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var renewalCycle by remember { mutableStateOf("") }

    val monthlyTotal = subs.sumOf { (name, price, cycle) ->
        if (cycle.contains("Annual", ignoreCase = true)) price / 12 else price
    }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Subscription Tracker", canNavigateBack = true, onNavigateBack = onBack) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                text = { Text("Add Subscription") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
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
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Monthly Subscription Cost", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("₹${"%.0f".format(monthlyTotal)}/mo", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Active Subscriptions (${subs.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                FilledTonalButton(onClick = { showDialog = true }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add New", style = MaterialTheme.typography.labelSmall)
                }
            }

            if (subs.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text("No recurring subscriptions tracked. Tap '+ Add New' to track renewals.", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                }
            }

            subs.forEachIndexed { index, (name, price, cycle) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(name, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                            Text(cycle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("₹${"%.0f".format(price)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        IconButton(onClick = {
                            subs = subs.filterIndexed { i, _ -> i != index }
                        }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Subscription Reminder") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = serviceName,
                        onValueChange = { serviceName = it },
                        label = { Text("Platform / Service Name") },
                        placeholder = { Text("e.g. Disney+ Hotstar, YouTube Premium") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = cost,
                        onValueChange = { cost = it },
                        label = { Text("Recurring Cost (₹)") },
                        placeholder = { Text("e.g. 299") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = renewalCycle,
                        onValueChange = { renewalCycle = it },
                        label = { Text("Renewal Date & Schedule") },
                        placeholder = { Text("e.g. Renews on 28th each month") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsedCost = cost.toDoubleOrNull() ?: 0.0
                        if (serviceName.isNotBlank()) {
                            val cycle = if (renewalCycle.isBlank()) "Monthly renewal" else renewalCycle.trim()
                            subs = subs + Triple(serviceName.trim(), parsedCost, cycle)
                            serviceName = ""
                            cost = ""
                            renewalCycle = ""
                            showDialog = false
                        }
                    },
                    enabled = serviceName.isNotBlank()
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun InvestmentTrackerScreen(onBack: () -> Unit) {
    val repository = remember { LifeHubApplication.instance.repository }
    val scope = rememberCoroutineScope()
    val investments by repository.getAllInvestments().collectAsStateWithLifecycle(initialValue = emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var assetName by remember { mutableStateOf("") }
    var assetType by remember { mutableStateOf("Mutual Fund") }
    var investedAmt by remember { mutableStateOf("") }
    var currentAmt by remember { mutableStateOf("") }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Investment Portfolio", canNavigateBack = true, onNavigateBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Add Asset")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            val totalInvested = investments.sumOf { it.investedAmount }
            val totalCurrent = investments.sumOf { it.currentValue }
            val profitLoss = totalCurrent - totalInvested
            val profitPercent = if (totalInvested > 0) (profitLoss / totalInvested) * 100 else 0.0

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Portfolio Current Value", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("₹${"%.2f".format(totalCurrent)}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Invested: ₹${"%.0f".format(totalInvested)}", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "Returns: ${if (profitLoss >= 0) "+" else ""}₹${"%.2f".format(profitLoss)} (${"%.2f".format(profitPercent)}%)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (profitLoss >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Holdings (${investments.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            if (investments.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No assets logged yet. Tap + to record Stocks, Mutual Funds, Gold...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(investments, key = { it.id }) { inv ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(inv.assetName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text("${inv.assetType} • Invested: ₹${"%.0f".format(inv.investedAmount)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("₹${"%.0f".format(inv.currentValue)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    val ret = inv.currentValue - inv.investedAmount
                                    Text("${if (ret >= 0) "+" else ""}₹${"%.0f".format(ret)}", style = MaterialTheme.typography.bodySmall, color = if (ret >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
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
            title = { Text("Add Investment Asset") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = assetName, onValueChange = { assetName = it }, label = { Text("Asset Name (e.g. Nifty 50 Index)") })
                    OutlinedTextField(value = assetType, onValueChange = { assetType = it }, label = { Text("Type (Mutual Fund, Stock, Gold, Crypto)") })
                    OutlinedTextField(value = investedAmt, onValueChange = { investedAmt = it }, label = { Text("Invested Amount (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    OutlinedTextField(value = currentAmt, onValueChange = { currentAmt = it }, label = { Text("Current Value (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val inv = investedAmt.toDoubleOrNull() ?: 0.0
                        val cur = currentAmt.toDoubleOrNull() ?: inv
                        if (assetName.isNotBlank() && inv > 0) {
                            scope.launch {
                                repository.addInvestment(InvestmentEntity(assetName = assetName, assetType = assetType, investedAmount = inv, currentValue = cur))
                            }
                            assetName = ""
                            investedAmt = ""
                            currentAmt = ""
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
fun NetWorthTrackerScreen(onBack: () -> Unit) {
    var totalAssets by remember { mutableStateOf("2850000") }
    var totalLiabilities by remember { mutableStateOf("640000") }

    val assets = totalAssets.toDoubleOrNull() ?: 0.0
    val liabilities = totalLiabilities.toDoubleOrNull() ?: 0.0
    val netWorth = assets - liabilities

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Net Worth Tracker", canNavigateBack = true, onNavigateBack = onBack) }
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
                value = totalAssets,
                onValueChange = { totalAssets = it },
                label = { Text("Total Assets (Savings, House, Stocks, Gold) ₹") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = totalLiabilities,
                onValueChange = { totalLiabilities = it },
                label = { Text("Total Liabilities (Loans, Credit Card dues) ₹") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Total Net Worth", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("₹${"%.2f".format(netWorth)}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Divider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Assets: ₹${"%.0f".format(assets)}")
                        Text("Liabilities: ₹${"%.0f".format(liabilities)}")
                    }
                }
            }
        }
    }
}

@Composable
fun LoanCalculatorScreen(onBack: () -> Unit) {
    var principal by remember { mutableStateOf("1000000") }
    var interestRate by remember { mutableStateOf("9.5") }
    var tenureYears by remember { mutableStateOf("10") }
    var downPayment by remember { mutableStateOf("150000") }

    val p = (principal.toDoubleOrNull() ?: 0.0) - (downPayment.toDoubleOrNull() ?: 0.0)
    val effectiveP = p.coerceAtLeast(0.0)
    val annualRate = interestRate.toDoubleOrNull() ?: 0.0
    val n = (tenureYears.toDoubleOrNull() ?: 0.0) * 12
    val r = (annualRate / 12) / 100

    val emi = if (effectiveP > 0 && r > 0 && n > 0) {
        (effectiveP * r * (1 + r).pow(n)) / ((1 + r).pow(n) - 1)
    } else 0.0

    val totalRepayment = emi * n
    val totalInterest = totalRepayment - effectiveP

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Loan Calculator", canNavigateBack = true, onNavigateBack = onBack) }
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
                value = principal,
                onValueChange = { principal = it },
                label = { Text("Total Property / Vehicle Price (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = downPayment,
                onValueChange = { downPayment = it },
                label = { Text("Down Payment (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = interestRate,
                    onValueChange = { interestRate = it },
                    label = { Text("Interest Rate (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = tenureYears,
                    onValueChange = { tenureYears = it },
                    label = { Text("Tenure (Years)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Loan Amount Financed: ₹${"%.0f".format(effectiveP)}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("Monthly EMI: ₹${"%.2f".format(emi)}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Divider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Interest:")
                        Text("₹${"%.0f".format(totalInterest.coerceAtLeast(0.0))}", fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Payment (P + I):")
                        Text("₹${"%.0f".format(totalRepayment.coerceAtLeast(0.0))}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
