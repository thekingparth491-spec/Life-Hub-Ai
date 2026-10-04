package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.*
import com.example.data.local.entity.*

@Database(
    entities = [
        ToolEntity::class,
        ExpenseEntity::class,
        BillReminderEntity::class,
        SavingsGoalEntity::class,
        InvestmentEntity::class,
        NoteEntity::class,
        TaskEntity::class,
        HabitEntity::class,
        WaterLogEntity::class,
        SleepLogEntity::class,
        MedicineEntity::class,
        VehicleEntity::class,
        FuelLogEntity::class,
        AttendanceEntity::class,
        FlashcardEntity::class,
        GroceryItemEntity::class,
        EmergencyContactEntity::class,
        PasswordVaultEntity::class,
        ApplianceWarrantyEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun toolDao(): ToolDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun financeDao(): FinanceDao
    abstract fun noteDao(): NoteDao
    abstract fun studentDao(): StudentDao
    abstract fun healthDao(): HealthDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun homeFamilyDao(): HomeFamilyDao
    abstract fun safetyDao(): SafetyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lifehub_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
