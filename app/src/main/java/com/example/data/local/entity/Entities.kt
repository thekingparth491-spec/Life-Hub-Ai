package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tools")
data class ToolEntity(
    @PrimaryKey val id: String,
    val categoryId: String,
    val name: String,
    val description: String,
    val iconName: String,
    val route: String,
    val isFavorite: Boolean = false,
    val lastUsed: Long = 0L,
    val usageCount: Int = 0
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val date: String,
    val notes: String = ""
)

@Entity(tableName = "bills")
data class BillReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val dueDate: String,
    val category: String,
    val isPaid: Boolean = false
)

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val targetDate: String
)

@Entity(tableName = "investments")
data class InvestmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val assetName: String,
    val assetType: String,
    val investedAmount: Double,
    val currentValue: Double
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val tag: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isVoiceNote: Boolean = false
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val dueDate: String,
    val priority: String = "Normal",
    val isCompleted: Boolean = false,
    val category: String = "General"
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val targetDays: Int = 30,
    val currentStreak: Int = 0,
    val completedToday: Boolean = false,
    val lastCompletedDate: String = ""
)

@Entity(tableName = "water_logs")
data class WaterLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val amountMl: Int
)

@Entity(tableName = "sleep_logs")
data class SleepLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val hoursSlept: Double,
    val qualityRating: Int, // 1 to 5
    val notes: String = ""
)

@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val dosage: String,
    val timeSchedule: String, // e.g. "Morning, Night"
    val beforeFood: Boolean = false,
    val takenToday: Boolean = false
)

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val regNumber: String,
    val fuelType: String,
    val currentOdo: Double,
    val insuranceExpiry: String,
    val pucExpiry: String
)

@Entity(tableName = "fuel_logs")
data class FuelLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long,
    val date: String,
    val liters: Double,
    val totalCost: Double,
    val odoReading: Double
)

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectName: String,
    val attended: Int,
    val total: Int
)

@Entity(tableName = "flashcards")
data class FlashcardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val question: String,
    val answer: String,
    val isMastered: Boolean = false
)

@Entity(tableName = "grocery_items")
data class GroceryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String = "Pantry",
    val quantity: String = "1",
    val isBought: Boolean = false
)

@Entity(tableName = "emergency_contacts")
data class EmergencyContactEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val relationship: String,
    val isPrimary: Boolean = false
)

@Entity(tableName = "passwords")
data class PasswordVaultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val username: String,
    val encryptedPassword: String,
    val category: String = "General"
)

@Entity(tableName = "warranties")
data class ApplianceWarrantyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val applianceName: String,
    val brand: String,
    val purchaseDate: String,
    val warrantyMonths: Int,
    val notes: String = ""
)
