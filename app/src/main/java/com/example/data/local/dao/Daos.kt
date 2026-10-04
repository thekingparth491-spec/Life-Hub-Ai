package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ToolDao {
    @Query("SELECT * FROM tools ORDER BY categoryId ASC, id ASC")
    fun getAllTools(): Flow<List<ToolEntity>>

    @Query("SELECT * FROM tools WHERE categoryId = :categoryId")
    fun getToolsByCategory(categoryId: String): Flow<List<ToolEntity>>

    @Query("SELECT * FROM tools WHERE isFavorite = 1")
    fun getFavoriteTools(): Flow<List<ToolEntity>>

    @Query("SELECT * FROM tools WHERE lastUsed > 0 ORDER BY lastUsed DESC LIMIT 10")
    fun getRecentlyUsedTools(): Flow<List<ToolEntity>>

    @Query("SELECT * FROM tools WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchTools(query: String): Flow<List<ToolEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTools(tools: List<ToolEntity>)

    @Update
    suspend fun updateTool(tool: ToolEntity)

    @Query("UPDATE tools SET isFavorite = :isFav WHERE id = :toolId")
    suspend fun toggleFavorite(toolId: String, isFav: Boolean)

    @Query("UPDATE tools SET lastUsed = :timestamp, usageCount = usageCount + 1 WHERE id = :toolId")
    suspend fun recordToolUsage(toolId: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM tools")
    suspend fun getToolCount(): Int
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY date DESC, id DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT SUM(amount) FROM expenses")
    fun getTotalExpenses(): Flow<Double?>
}

@Dao
interface FinanceDao {
    @Query("SELECT * FROM bills ORDER BY dueDate ASC")
    fun getAllBills(): Flow<List<BillReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: BillReminderEntity)

    @Update
    suspend fun updateBill(bill: BillReminderEntity)

    @Delete
    suspend fun deleteBill(bill: BillReminderEntity)

    @Query("SELECT * FROM savings_goals ORDER BY id DESC")
    fun getAllSavingsGoals(): Flow<List<SavingsGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsGoal(goal: SavingsGoalEntity)

    @Update
    suspend fun updateSavingsGoal(goal: SavingsGoalEntity)

    @Delete
    suspend fun deleteSavingsGoal(goal: SavingsGoalEntity)

    @Query("SELECT * FROM investments ORDER BY id DESC")
    fun getAllInvestments(): Flow<List<InvestmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvestment(investment: InvestmentEntity)

    @Delete
    suspend fun deleteInvestment(investment: InvestmentEntity)
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE isVoiceNote = 1 ORDER BY timestamp DESC")
    fun getVoiceNotes(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Delete
    suspend fun deleteNote(note: NoteEntity)
}

@Dao
interface StudentDao {
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT * FROM attendance ORDER BY subjectName ASC")
    fun getAllAttendance(): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity)

    @Update
    suspend fun updateAttendance(attendance: AttendanceEntity)

    @Delete
    suspend fun deleteAttendance(attendance: AttendanceEntity)

    @Query("SELECT * FROM flashcards WHERE subject = :subject")
    fun getFlashcardsBySubject(subject: String): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards")
    fun getAllFlashcards(): Flow<List<FlashcardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: FlashcardEntity)

    @Update
    suspend fun updateFlashcard(flashcard: FlashcardEntity)

    @Delete
    suspend fun deleteFlashcard(flashcard: FlashcardEntity)
}

@Dao
interface HealthDao {
    @Query("SELECT * FROM habits ORDER BY id ASC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("SELECT * FROM water_logs WHERE date = :date")
    fun getWaterLogsForDate(date: String): Flow<List<WaterLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterLog(waterLog: WaterLogEntity)

    @Query("SELECT * FROM sleep_logs ORDER BY date DESC LIMIT 7")
    fun getRecentSleepLogs(): Flow<List<SleepLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepLog(sleepLog: SleepLogEntity)

    @Query("SELECT * FROM medicines ORDER BY id ASC")
    fun getAllMedicines(): Flow<List<MedicineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: MedicineEntity)

    @Update
    suspend fun updateMedicine(medicine: MedicineEntity)

    @Delete
    suspend fun deleteMedicine(medicine: MedicineEntity)
}

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles ORDER BY id ASC")
    fun getAllVehicles(): Flow<List<VehicleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: VehicleEntity)

    @Update
    suspend fun updateVehicle(vehicle: VehicleEntity)

    @Delete
    suspend fun deleteVehicle(vehicle: VehicleEntity)

    @Query("SELECT * FROM fuel_logs WHERE vehicleId = :vehicleId ORDER BY date DESC")
    fun getFuelLogsForVehicle(vehicleId: Long): Flow<List<FuelLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelLog(fuelLog: FuelLogEntity)
}

@Dao
interface HomeFamilyDao {
    @Query("SELECT * FROM grocery_items ORDER BY isBought ASC, id DESC")
    fun getAllGroceryItems(): Flow<List<GroceryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroceryItem(item: GroceryItemEntity)

    @Update
    suspend fun updateGroceryItem(item: GroceryItemEntity)

    @Delete
    suspend fun deleteGroceryItem(item: GroceryItemEntity)

    @Query("SELECT * FROM warranties ORDER BY purchaseDate DESC")
    fun getAllWarranties(): Flow<List<ApplianceWarrantyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWarranty(warranty: ApplianceWarrantyEntity)

    @Delete
    suspend fun deleteWarranty(warranty: ApplianceWarrantyEntity)
}

@Dao
interface SafetyDao {
    @Query("SELECT * FROM emergency_contacts ORDER BY isPrimary DESC, name ASC")
    fun getAllContacts(): Flow<List<EmergencyContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: EmergencyContactEntity)

    @Delete
    suspend fun deleteContact(contact: EmergencyContactEntity)

    @Query("SELECT * FROM passwords ORDER BY title ASC")
    fun getAllPasswords(): Flow<List<PasswordVaultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPassword(entry: PasswordVaultEntity)

    @Delete
    suspend fun deletePassword(entry: PasswordVaultEntity)
}
