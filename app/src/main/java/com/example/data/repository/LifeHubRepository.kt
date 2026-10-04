package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.entity.*
import com.example.domain.model.Category
import com.example.domain.model.Tool
import com.example.domain.model.ToolRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class LifeHubRepository(
    private val database: AppDatabase,
    private val context: Context
) {
    private val toolDao = database.toolDao()
    private val expenseDao = database.expenseDao()
    private val financeDao = database.financeDao()
    private val noteDao = database.noteDao()
    private val studentDao = database.studentDao()
    private val healthDao = database.healthDao()
    private val vehicleDao = database.vehicleDao()
    private val homeFamilyDao = database.homeFamilyDao()
    private val safetyDao = database.safetyDao()

    init {
        // Pre-populate tools into database if empty
        CoroutineScope(Dispatchers.IO).launch {
            if (toolDao.getToolCount() == 0) {
                val entities = ToolRegistry.allTools.map { tool ->
                    ToolEntity(
                        id = tool.id,
                        categoryId = tool.categoryId,
                        name = tool.name,
                        description = tool.description,
                        iconName = tool.iconName,
                        route = tool.route,
                        isFavorite = tool.id in listOf("tool_2", "tool_11", "tool_29", "tool_40", "tool_45", "tool_71", "tool_81"),
                        lastUsed = if (tool.id in listOf("tool_2", "tool_11", "tool_40")) System.currentTimeMillis() else 0L,
                        usageCount = if (tool.id in listOf("tool_2", "tool_11", "tool_40")) 3 else 0
                    )
                }
                toolDao.insertTools(entities)
            }
        }
    }

    // Tools
    fun getAllTools(): Flow<List<Tool>> = toolDao.getAllTools().map { list -> list.map { it.toDomain() } }
    fun getToolsByCategory(categoryId: String): Flow<List<Tool>> = toolDao.getToolsByCategory(categoryId).map { list -> list.map { it.toDomain() } }
    fun getFavoriteTools(): Flow<List<Tool>> = toolDao.getFavoriteTools().map { list -> list.map { it.toDomain() } }
    fun getRecentlyUsedTools(): Flow<List<Tool>> = toolDao.getRecentlyUsedTools().map { list -> list.map { it.toDomain() } }
    fun searchTools(query: String): Flow<List<Tool>> = toolDao.searchTools(query).map { list -> list.map { it.toDomain() } }

    suspend fun toggleFavorite(toolId: String, isFav: Boolean) = toolDao.toggleFavorite(toolId, isFav)
    suspend fun recordToolUsage(toolId: String) = toolDao.recordToolUsage(toolId, System.currentTimeMillis())

    // Categories
    fun getCategories(): List<Category> = ToolRegistry.categories

    // Expenses
    fun getAllExpenses(): Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()
    fun getTotalExpenses(): Flow<Double?> = expenseDao.getTotalExpenses()
    suspend fun addExpense(expense: ExpenseEntity) = expenseDao.insertExpense(expense)
    suspend fun deleteExpense(expense: ExpenseEntity) = expenseDao.deleteExpense(expense)

    // Finance (Bills, Savings, Investments)
    fun getAllBills(): Flow<List<BillReminderEntity>> = financeDao.getAllBills()
    suspend fun addBill(bill: BillReminderEntity) = financeDao.insertBill(bill)
    suspend fun updateBill(bill: BillReminderEntity) = financeDao.updateBill(bill)
    suspend fun deleteBill(bill: BillReminderEntity) = financeDao.deleteBill(bill)

    fun getAllSavingsGoals(): Flow<List<SavingsGoalEntity>> = financeDao.getAllSavingsGoals()
    suspend fun addSavingsGoal(goal: SavingsGoalEntity) = financeDao.insertSavingsGoal(goal)
    suspend fun updateSavingsGoal(goal: SavingsGoalEntity) = financeDao.updateSavingsGoal(goal)

    fun getAllInvestments(): Flow<List<InvestmentEntity>> = financeDao.getAllInvestments()
    suspend fun addInvestment(inv: InvestmentEntity) = financeDao.insertInvestment(inv)

    // Notes
    fun getAllNotes(): Flow<List<NoteEntity>> = noteDao.getAllNotes()
    fun getVoiceNotes(): Flow<List<NoteEntity>> = noteDao.getVoiceNotes()
    suspend fun addNote(note: NoteEntity): Long = noteDao.insertNote(note)
    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)

    // Student (Tasks, Attendance, Flashcards)
    fun getAllTasks(): Flow<List<TaskEntity>> = studentDao.getAllTasks()
    suspend fun addTask(task: TaskEntity) = studentDao.insertTask(task)
    suspend fun updateTask(task: TaskEntity) = studentDao.updateTask(task)
    suspend fun deleteTask(task: TaskEntity) = studentDao.deleteTask(task)

    fun getAllAttendance(): Flow<List<AttendanceEntity>> = studentDao.getAllAttendance()
    suspend fun addAttendance(att: AttendanceEntity) = studentDao.insertAttendance(att)
    suspend fun updateAttendance(att: AttendanceEntity) = studentDao.updateAttendance(att)
    suspend fun deleteAttendance(att: AttendanceEntity) = studentDao.deleteAttendance(att)

    fun getAllFlashcards(): Flow<List<FlashcardEntity>> = studentDao.getAllFlashcards()
    suspend fun addFlashcard(fc: FlashcardEntity) = studentDao.insertFlashcard(fc)
    suspend fun updateFlashcard(fc: FlashcardEntity) = studentDao.updateFlashcard(fc)
    suspend fun deleteFlashcard(fc: FlashcardEntity) = studentDao.deleteFlashcard(fc)

    // Health (Habits, Water, Sleep, Medicine)
    fun getAllHabits(): Flow<List<HabitEntity>> = healthDao.getAllHabits()
    suspend fun addHabit(habit: HabitEntity) = healthDao.insertHabit(habit)
    suspend fun updateHabit(habit: HabitEntity) = healthDao.updateHabit(habit)
    suspend fun deleteHabit(habit: HabitEntity) = healthDao.deleteHabit(habit)

    fun getWaterLogs(date: String): Flow<List<WaterLogEntity>> = healthDao.getWaterLogsForDate(date)
    suspend fun logWater(waterLog: WaterLogEntity) = healthDao.insertWaterLog(waterLog)

    fun getRecentSleepLogs(): Flow<List<SleepLogEntity>> = healthDao.getRecentSleepLogs()
    suspend fun logSleep(sleepLog: SleepLogEntity) = healthDao.insertSleepLog(sleepLog)

    fun getAllMedicines(): Flow<List<MedicineEntity>> = healthDao.getAllMedicines()
    suspend fun addMedicine(med: MedicineEntity) = healthDao.insertMedicine(med)
    suspend fun updateMedicine(med: MedicineEntity) = healthDao.updateMedicine(med)
    suspend fun deleteMedicine(med: MedicineEntity) = healthDao.deleteMedicine(med)

    // Vehicle
    fun getAllVehicles(): Flow<List<VehicleEntity>> = vehicleDao.getAllVehicles()
    suspend fun addVehicle(v: VehicleEntity) = vehicleDao.insertVehicle(v)
    suspend fun deleteVehicle(v: VehicleEntity) = vehicleDao.deleteVehicle(v)

    fun getFuelLogs(vehicleId: Long): Flow<List<FuelLogEntity>> = vehicleDao.getFuelLogsForVehicle(vehicleId)
    suspend fun addFuelLog(log: FuelLogEntity) = vehicleDao.insertFuelLog(log)

    // Home & Family
    fun getAllGroceries(): Flow<List<GroceryItemEntity>> = homeFamilyDao.getAllGroceryItems()
    suspend fun addGrocery(item: GroceryItemEntity) = homeFamilyDao.insertGroceryItem(item)
    suspend fun updateGrocery(item: GroceryItemEntity) = homeFamilyDao.updateGroceryItem(item)
    suspend fun deleteGrocery(item: GroceryItemEntity) = homeFamilyDao.deleteGroceryItem(item)

    fun getAllWarranties(): Flow<List<ApplianceWarrantyEntity>> = homeFamilyDao.getAllWarranties()
    suspend fun addWarranty(w: ApplianceWarrantyEntity) = homeFamilyDao.insertWarranty(w)

    // Safety
    fun getAllEmergencyContacts(): Flow<List<EmergencyContactEntity>> = safetyDao.getAllContacts()
    suspend fun addEmergencyContact(c: EmergencyContactEntity) = safetyDao.insertContact(c)
    suspend fun deleteEmergencyContact(c: EmergencyContactEntity) = safetyDao.deleteContact(c)

    fun getAllPasswords(): Flow<List<PasswordVaultEntity>> = safetyDao.getAllPasswords()
    suspend fun addPassword(p: PasswordVaultEntity) = safetyDao.insertPassword(p)
    suspend fun deletePassword(p: PasswordVaultEntity) = safetyDao.deletePassword(p)

    private fun ToolEntity.toDomain() = Tool(
        id = id,
        categoryId = categoryId,
        name = name,
        description = description,
        iconName = iconName,
        route = route,
        isFavorite = isFavorite,
        lastUsed = lastUsed,
        usageCount = usageCount
    )
}
