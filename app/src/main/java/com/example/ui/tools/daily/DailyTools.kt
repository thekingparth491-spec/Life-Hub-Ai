package com.example.ui.tools.daily

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ui.components.LifeHubTopAppBar
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

@Composable
fun SmartCalculatorScreen(onBack: () -> Unit) {
    var display by remember { mutableStateOf("0") }
    var expression by remember { mutableStateOf("") }
    var firstOperand by remember { mutableStateOf<Double?>(null) }
    var pendingOp by remember { mutableStateOf<String?>(null) }
    var isNewNumber by remember { mutableStateOf(true) }

    fun onDigit(d: String) {
        if (isNewNumber || display == "0") {
            display = d
            isNewNumber = false
        } else {
            display += d
        }
    }

    fun onOp(op: String) {
        val curr = display.toDoubleOrNull() ?: 0.0
        firstOperand = curr
        pendingOp = op
        expression = "$curr $op"
        isNewNumber = true
    }

    fun onEqual() {
        val first = firstOperand ?: return
        val op = pendingOp ?: return
        val second = display.toDoubleOrNull() ?: return
        val result = when (op) {
            "+" -> first + second
            "-" -> first - second
            "×" -> first * second
            "÷" -> if (second != 0.0) first / second else Double.NaN
            "%" -> (first * second) / 100.0
            else -> second
        }
        expression = "$first $op $second ="
        display = if (result.isNaN()) "Error" else if (result % 1.0 == 0.0) result.toLong().toString() else "%.4f".format(result).trimEnd('0').trimEnd('.')
        firstOperand = null
        pendingOp = null
        isNewNumber = true
    }

    fun onClear() {
        display = "0"
        expression = ""
        firstOperand = null
        pendingOp = null
        isNewNumber = true
    }

    Scaffold(
        topBar = {
            LifeHubTopAppBar(title = "Smart Calculator", canNavigateBack = true, onNavigateBack = onBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Display Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = expression,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = display,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Keypad
            val buttons = listOf(
                listOf("C", "%", "÷", "DEL"),
                listOf("7", "8", "9", "×"),
                listOf("4", "5", "6", "-"),
                listOf("1", "2", "3", "+"),
                listOf("00", "0", ".", "=")
            )

            buttons.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { btn ->
                        val isOp = btn in listOf("÷", "×", "-", "+", "=")
                        val isAction = btn in listOf("C", "DEL", "%")
                        Button(
                            onClick = {
                                when (btn) {
                                    "C" -> onClear()
                                    "DEL" -> {
                                        if (display.length > 1) display = display.dropLast(1) else display = "0"
                                    }
                                    "=" -> onEqual()
                                    in listOf("+", "-", "×", "÷", "%") -> onOp(btn)
                                    "." -> if (!display.contains(".")) display += "."
                                    else -> onDigit(btn)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(58.dp)
                                .testTag("calc_btn_$btn"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (btn == "=") MaterialTheme.colorScheme.primary
                                else if (isOp) MaterialTheme.colorScheme.primaryContainer
                                else if (isAction) MaterialTheme.colorScheme.errorContainer
                                else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (btn == "=") MaterialTheme.colorScheme.onPrimary
                                else if (isOp) MaterialTheme.colorScheme.onPrimaryContainer
                                else if (isAction) MaterialTheme.colorScheme.onErrorContainer
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text(text = btn, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UnitConverterScreen(onBack: () -> Unit) {
    var category by remember { mutableStateOf("Length") }
    var inputValue by remember { mutableStateOf("1") }
    var fromUnit by remember { mutableStateOf("Meter") }
    var toUnit by remember { mutableStateOf("Foot") }

    val categories = listOf("Length", "Weight", "Temperature", "Area", "Speed")

    val unitsMap = mapOf(
        "Length" to listOf("Meter", "Kilometer", "Centimeter", "Millimeter", "Mile", "Foot", "Inch"),
        "Weight" to listOf("Kilogram", "Gram", "Pound", "Ounce", "Tonne"),
        "Temperature" to listOf("Celsius", "Fahrenheit", "Kelvin"),
        "Area" to listOf("Square Meter", "Square Foot", "Acre", "Hectare"),
        "Speed" to listOf("km/h", "m/s", "mph", "Knot")
    )

    fun convert(): String {
        val v = inputValue.toDoubleOrNull() ?: return "0"
        return when (category) {
            "Length" -> {
                val inMeters = when (fromUnit) {
                    "Meter" -> v
                    "Kilometer" -> v * 1000
                    "Centimeter" -> v / 100
                    "Millimeter" -> v / 1000
                    "Mile" -> v * 1609.34
                    "Foot" -> v * 0.3048
                    "Inch" -> v * 0.0254
                    else -> v
                }
                val res = when (toUnit) {
                    "Meter" -> inMeters
                    "Kilometer" -> inMeters / 1000
                    "Centimeter" -> inMeters * 100
                    "Millimeter" -> inMeters * 1000
                    "Mile" -> inMeters / 1609.34
                    "Foot" -> inMeters / 0.3048
                    "Inch" -> inMeters / 0.0254
                    else -> inMeters
                }
                "%.4f".format(res).trimEnd('0').trimEnd('.')
            }
            "Weight" -> {
                val inKg = when (fromUnit) {
                    "Kilogram" -> v
                    "Gram" -> v / 1000
                    "Pound" -> v * 0.453592
                    "Ounce" -> v * 0.0283495
                    "Tonne" -> v * 1000
                    else -> v
                }
                val res = when (toUnit) {
                    "Kilogram" -> inKg
                    "Gram" -> inKg * 1000
                    "Pound" -> inKg / 0.453592
                    "Ounce" -> inKg / 0.0283495
                    "Tonne" -> inKg / 1000
                    else -> inKg
                }
                "%.4f".format(res).trimEnd('0').trimEnd('.')
            }
            "Temperature" -> {
                val inC = when (fromUnit) {
                    "Celsius" -> v
                    "Fahrenheit" -> (v - 32) * 5 / 9
                    "Kelvin" -> v - 273.15
                    else -> v
                }
                val res = when (toUnit) {
                    "Celsius" -> inC
                    "Fahrenheit" -> (inC * 9 / 5) + 32
                    "Kelvin" -> inC + 273.15
                    else -> inC
                }
                "%.2f".format(res)
            }
            "Speed" -> {
                val inKmh = when (fromUnit) {
                    "km/h" -> v
                    "m/s" -> v * 3.6
                    "mph" -> v * 1.60934
                    "Knot" -> v * 1.852
                    else -> v
                }
                val res = when (toUnit) {
                    "km/h" -> inKmh
                    "m/s" -> inKmh / 3.6
                    "mph" -> inKmh / 1.60934
                    "Knot" -> inKmh / 1.852
                    else -> inKmh
                }
                "%.3f".format(res).trimEnd('0').trimEnd('.')
            }
            else -> "%.2f".format(v)
        }
    }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Unit Converter", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category Selector Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = {
                            category = cat
                            fromUnit = unitsMap[cat]?.firstOrNull() ?: ""
                            toUnit = unitsMap[cat]?.getOrNull(1) ?: fromUnit
                        },
                        label = { Text(cat) }
                    )
                }
            }

            OutlinedTextField(
                value = inputValue,
                onValueChange = { inputValue = it },
                label = { Text("Enter Value") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth().testTag("converter_input")
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("From Unit", style = MaterialTheme.typography.labelMedium)
                    unitsMap[category]?.forEach { u ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = fromUnit == u, onClick = { fromUnit = u })
                            Text(u, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("To Unit", style = MaterialTheme.typography.labelMedium)
                    unitsMap[category]?.forEach { u ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = toUnit == u, onClick = { toUnit = u })
                            Text(u, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Result", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${convert()} $toUnit",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun AgeCalculatorScreen(onBack: () -> Unit) {
    var birthYear by remember { mutableStateOf("1998") }
    var birthMonth by remember { mutableStateOf("8") }
    var birthDay by remember { mutableStateOf("15") }
    var resultText by remember { mutableStateOf("") }
    var nextBirthdayDays by remember { mutableStateOf("") }

    fun calculateAge() {
        val y = birthYear.toIntOrNull() ?: return
        val m = birthMonth.toIntOrNull() ?: return
        val d = birthDay.toIntOrNull() ?: return

        val birthCal = Calendar.getInstance().apply { set(y, m - 1, d) }
        val now = Calendar.getInstance()

        var years = now.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR)
        var months = now.get(Calendar.MONTH) - birthCal.get(Calendar.MONTH)
        var days = now.get(Calendar.DAY_OF_MONTH) - birthCal.get(Calendar.DAY_OF_MONTH)

        if (days < 0) {
            months--
            days += 30
        }
        if (months < 0) {
            years--
            months += 12
        }

        resultText = "$years Years, $months Months, $days Days"

        val nextBday = Calendar.getInstance().apply {
            set(Calendar.MONTH, m - 1)
            set(Calendar.DAY_OF_MONTH, d)
            if (before(now)) add(Calendar.YEAR, 1)
        }
        val diffMs = nextBday.timeInMillis - now.timeInMillis
        val diffDays = (diffMs / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
        nextBirthdayDays = "$diffDays days until your next birthday! 🎂"
    }

    LaunchedEffect(Unit) { calculateAge() }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Age Calculator", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Enter Date of Birth", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = birthDay,
                    onValueChange = { birthDay = it; calculateAge() },
                    label = { Text("Day") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = birthMonth,
                    onValueChange = { birthMonth = it; calculateAge() },
                    label = { Text("Month") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = birthYear,
                    onValueChange = { birthYear = it; calculateAge() },
                    label = { Text("Year") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1.5f)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Your Exact Age", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(resultText, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(nextBirthdayDays, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun GSTCalculatorScreen(onBack: () -> Unit) {
    var amountText by remember { mutableStateOf("1000") }
    var gstRate by remember { mutableStateOf(18.0) }
    var isAddingGst by remember { mutableStateOf(true) }

    val rates = listOf(5.0, 12.0, 18.0, 28.0)
    val amount = amountText.toDoubleOrNull() ?: 0.0

    val gstAmount = if (isAddingGst) {
        (amount * gstRate) / 100.0
    } else {
        amount - (amount / (1 + gstRate / 100.0))
    }

    val totalAmount = if (isAddingGst) amount + gstAmount else amount - gstAmount
    val cgst = gstAmount / 2.0
    val sgst = gstAmount / 2.0

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "GST Calculator", canNavigateBack = true, onNavigateBack = onBack) }
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
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Initial Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { isAddingGst = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAddingGst) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isAddingGst) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+ Add GST")
                }
                Button(
                    onClick = { isAddingGst = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isAddingGst) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (!isAddingGst) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("- Remove GST")
                }
            }

            Text("GST Rate", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rates.forEach { rate ->
                    FilterChip(
                        selected = gstRate == rate,
                        onClick = { gstRate = rate },
                        label = { Text("${rate.toInt()}%") }
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Total Amount: ₹${"%.2f".format(totalAmount)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("Total GST: ₹${"%.2f".format(gstAmount)}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("CGST (${gstRate/2}%): ₹${"%.2f".format(cgst)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                    Text("SGST (${gstRate/2}%): ₹${"%.2f".format(sgst)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                }
            }
        }
    }
}

@Composable
fun EMICalculatorScreen(onBack: () -> Unit) {
    var loanAmount by remember { mutableStateOf("500000") }
    var interestRate by remember { mutableStateOf("8.5") }
    var tenureYears by remember { mutableStateOf("5") }

    val p = loanAmount.toDoubleOrNull() ?: 0.0
    val annualRate = interestRate.toDoubleOrNull() ?: 0.0
    val n = (tenureYears.toDoubleOrNull() ?: 0.0) * 12

    val r = (annualRate / 12) / 100
    val emi = if (p > 0 && r > 0 && n > 0) {
        (p * r * (1 + r).pow(n)) / ((1 + r).pow(n) - 1)
    } else 0.0

    val totalPayment = emi * n
    val totalInterest = totalPayment - p

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "EMI Calculator", canNavigateBack = true, onNavigateBack = onBack) }
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
                value = loanAmount,
                onValueChange = { loanAmount = it },
                label = { Text("Loan Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = interestRate,
                onValueChange = { interestRate = it },
                label = { Text("Interest Rate (% per annum)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = tenureYears,
                onValueChange = { tenureYears = it },
                label = { Text("Tenure (Years)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Monthly EMI", style = MaterialTheme.typography.titleMedium)
                    Text("₹${"%.2f".format(emi)}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Divider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Interest:")
                        Text("₹${"%.2f".format(totalInterest.coerceAtLeast(0.0))}", fontWeight = FontWeight.SemiBold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Payable:")
                        Text("₹${"%.2f".format(totalPayment.coerceAtLeast(0.0))}", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun TipCalculatorScreen(onBack: () -> Unit) {
    var billAmount by remember { mutableStateOf("1200") }
    var tipPercent by remember { mutableStateOf("10") }
    var splitCount by remember { mutableStateOf("2") }

    val bill = billAmount.toDoubleOrNull() ?: 0.0
    val tip = tipPercent.toDoubleOrNull() ?: 0.0
    val people = (splitCount.toIntOrNull() ?: 1).coerceAtLeast(1)

    val tipTotal = (bill * tip) / 100.0
    val grandTotal = bill + tipTotal
    val perPerson = grandTotal / people

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Tip Calculator", canNavigateBack = true, onNavigateBack = onBack) }
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
                value = billAmount,
                onValueChange = { billAmount = it },
                label = { Text("Bill Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = tipPercent,
                onValueChange = { tipPercent = it },
                label = { Text("Tip Percentage (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = splitCount,
                onValueChange = { splitCount = it },
                label = { Text("Split between (People)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Per Person: ₹${"%.2f".format(perPerson)}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Total Tip: ₹${"%.2f".format(tipTotal)}")
                    Text("Grand Total: ₹${"%.2f".format(grandTotal)}")
                }
            }
        }
    }
}

@Composable
fun TimeZoneConverterScreen(onBack: () -> Unit) {
    val zones = listOf(
        "UTC" to "Coordinated Universal Time",
        "Asia/Kolkata" to "India Standard Time (IST)",
        "America/New_York" to "Eastern Time (EST/EDT)",
        "America/Los_Angeles" to "Pacific Time (PST/PDT)",
        "Europe/London" to "Greenwich / BST",
        "Asia/Tokyo" to "Japan Standard Time (JST)",
        "Australia/Sydney" to "Australian Eastern Time"
    )

    val sdf = remember {
        SimpleDateFormat("EEE, dd MMM yyyy, hh:mm a", Locale.getDefault())
    }

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Time Zone Converter", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Global Times (Live Comparison)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            zones.forEach { (zoneId, label) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        val tz = TimeZone.getTimeZone(zoneId)
                        sdf.timeZone = tz
                        Text(sdf.format(Date()), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
