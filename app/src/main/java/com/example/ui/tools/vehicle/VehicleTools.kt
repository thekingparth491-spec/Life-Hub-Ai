package com.example.ui.tools.vehicle

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
import com.example.ui.components.LifeHubTopAppBar

@Composable
fun VehicleServiceScreen(onBack: () -> Unit) {
    val serviceReminders = listOf(
        Triple("Hyundai Creta (DL-08-AB-1234)", "Next Service: 18 Nov 2026", "At 35,000 km (3,200 km remaining)"),
        Triple("Royal Enfield Hunter (DL-03-XY-9876)", "Next Service: 05 Dec 2026", "At 8,000 km (1,150 km remaining)")
    )

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Vehicle Service Reminder", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            serviceReminders.forEach { (vehicle, date, details) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(vehicle, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(date, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                        Text(details, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun FuelTrackerScreen(onBack: () -> Unit) {
    var liters by remember { mutableStateOf("32") }
    var costPerLiter by remember { mutableStateOf("96.72") }
    var odo by remember { mutableStateOf("31840") }

    val l = liters.toDoubleOrNull() ?: 0.0
    val c = costPerLiter.toDoubleOrNull() ?: 0.0
    val totalCost = l * c

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Fuel Expense Tracker", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = liters, onValueChange = { liters = it }, label = { Text("Fuel (Liters)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = costPerLiter, onValueChange = { costPerLiter = it }, label = { Text("Rate per Liter (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = odo, onValueChange = { odo = it }, label = { Text("Odometer Reading (km)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Total Fuel Bill", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("₹${"%.2f".format(totalCost)}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("32 Liters filled at Odometer $odo km", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun MileageCalcScreen(onBack: () -> Unit) {
    var distanceKm by remember { mutableStateOf("480") }
    var fuelConsumedLiters by remember { mutableStateOf("28") }
    var fuelPrice by remember { mutableStateOf("96.72") }

    val dist = distanceKm.toDoubleOrNull() ?: 0.0
    val fuel = fuelConsumedLiters.toDoubleOrNull() ?: 1.0
    val price = fuelPrice.toDoubleOrNull() ?: 0.0

    val mileage = if (fuel > 0) dist / fuel else 0.0
    val costPerKm = if (dist > 0) (fuel * price) / dist else 0.0

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Mileage Calculator", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = distanceKm, onValueChange = { distanceKm = it }, label = { Text("Distance Driven (km)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = fuelConsumedLiters, onValueChange = { fuelConsumedLiters = it }, label = { Text("Fuel Consumed (Liters)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = fuelPrice, onValueChange = { fuelPrice = it }, label = { Text("Fuel Price (₹/L)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Fuel Mileage", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("${"%.2f".format(mileage)} km/L", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Running Cost: ₹${"%.2f".format(costPerKm)} per km", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun TripCostScreen(onBack: () -> Unit) {
    var tripDistance by remember { mutableStateOf("320") }
    var mileageKm by remember { mutableStateOf("16") }
    var fuelRate by remember { mutableStateOf("96.5") }
    var tollTotal by remember { mutableStateOf("450") }
    var passengerCount by remember { mutableStateOf("4") }

    val dist = tripDistance.toDoubleOrNull() ?: 0.0
    val mil = (mileageKm.toDoubleOrNull() ?: 1.0).coerceAtLeast(1.0)
    val rate = fuelRate.toDoubleOrNull() ?: 0.0
    val toll = tollTotal.toDoubleOrNull() ?: 0.0
    val people = (passengerCount.toIntOrNull() ?: 1).coerceAtLeast(1)

    val fuelNeeded = dist / mil
    val totalFuelCost = fuelNeeded * rate
    val grandTripTotal = totalFuelCost + toll
    val perHead = grandTripTotal / people

    Scaffold(
        topBar = { LifeHubTopAppBar(title = "Trip Cost Calculator", canNavigateBack = true, onNavigateBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(value = tripDistance, onValueChange = { tripDistance = it }, label = { Text("Trip Distance (km)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = mileageKm, onValueChange = { mileageKm = it }, label = { Text("Car Mileage (km/L)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = tollTotal, onValueChange = { tollTotal = it }, label = { Text("Tolls & Parking (₹)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = passengerCount, onValueChange = { passengerCount = it }, label = { Text("Number of Travelers") }, modifier = Modifier.fillMaxWidth())

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Per Head Cost", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("₹${"%.0f".format(perHead)} / person", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Total Trip Expenses: ₹${"%.0f".format(grandTripTotal)} (Fuel: ₹${"%.0f".format(totalFuelCost)}, Toll: ₹${"%.0f".format(toll)})", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
