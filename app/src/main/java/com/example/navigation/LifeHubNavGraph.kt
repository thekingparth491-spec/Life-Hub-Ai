package com.example.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.domain.model.Category
import com.example.ui.ai.AIAssistantScreen
import com.example.ui.categories.AllToolsScreen
import com.example.ui.categories.CategoriesScreen
import com.example.ui.categories.CategoryDetailScreen
import com.example.ui.favorites.FavoritesScreen
import com.example.ui.home.HomeScreen
import com.example.ui.settings.BackupRestoreScreen
import com.example.ui.settings.PermissionCenterScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.tools.aitools.*
import com.example.ui.tools.creative.*
import com.example.ui.tools.daily.*
import com.example.ui.tools.finance.*
import com.example.ui.tools.health.*
import com.example.ui.tools.homefamily.*
import com.example.ui.tools.phone.*
import com.example.ui.tools.safety.*
import com.example.ui.tools.student.*
import com.example.ui.tools.vehicle.*

@Composable
fun LifeHubNavGraph(
    navController: NavHostController,
    currentTheme: String,
    onThemeChange: (String) -> Unit,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        // Main tabs
        composable("home") {
            HomeScreen(
                onNavigateToCategory = { cat -> navController.navigate("category/${cat.id}") },
                onNavigateToTool = { tool -> navController.navigate(tool.route) },
                onNavigateToToolRoute = { route -> navController.navigate(route) },
                onNavigateToAllTools = { navController.navigate("all_tools") },
                onNavigateToAI = { navController.navigate("ai_assistant") },
                onNavigateToFavorites = { navController.navigate("favorites") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }

        composable("categories") {
            CategoriesScreen(
                onCategoryClick = { cat -> navController.navigate("category/${cat.id}") },
                onNavigateToAllTools = { navController.navigate("all_tools") }
            )
        }

        composable("ai_assistant") {
            AIAssistantScreen(
                onNavigateToTool = { route -> navController.navigate(route) },
                onBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable("favorites") {
            FavoritesScreen(
                onToolClick = { tool -> navController.navigate(tool.route) },
                onExploreTools = { navController.navigate("all_tools") }
            )
        }

        composable("settings") {
            SettingsScreen(
                currentTheme = currentTheme,
                onThemeChange = onThemeChange,
                currentLanguage = currentLanguage,
                onLanguageChange = onLanguageChange,
                onNavigateToPermissions = { navController.navigate("settings/permissions") },
                onNavigateToBackup = { navController.navigate("settings/backup") }
            )
        }

        composable("settings/permissions") {
            PermissionCenterScreen(onBack = { navController.popBackStack() })
        }

        composable("settings/backup") {
            BackupRestoreScreen(onBack = { navController.popBackStack() })
        }

        composable("all_tools") {
            AllToolsScreen(
                onToolClick = { tool -> navController.navigate(tool.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "category/{categoryId}",
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val catId = backStackEntry.arguments?.getString("categoryId") ?: "daily_utilities"
            CategoryDetailScreen(
                categoryId = catId,
                onToolClick = { tool -> navController.navigate(tool.route) },
                onBack = { navController.popBackStack() }
            )
        }

        // --- 1. DAILY UTILITIES (1-10) ---
        composable("tool_daily_hub") {
            DailyHubScreen(onNavigateToTool = { r -> navController.navigate(r) }, onBack = { navController.popBackStack() })
        }
        composable("tool_calculator") { SmartCalculatorScreen(onBack = { navController.popBackStack() }) }
        composable("tool_unit_converter") { UnitConverterScreen(onBack = { navController.popBackStack() }) }
        composable("tool_age_calc") { AgeCalculatorScreen(onBack = { navController.popBackStack() }) }
        composable("tool_date_diff") { DateDifferenceScreen(onBack = { navController.popBackStack() }) }
        composable("tool_gst_calc") { GSTCalculatorScreen(onBack = { navController.popBackStack() }) }
        composable("tool_emi_calc") { EMICalculatorScreen(onBack = { navController.popBackStack() }) }
        composable("tool_percentage_calc") { PercentageCalculatorScreen(onBack = { navController.popBackStack() }) }
        composable("tool_tip_calc") { TipCalculatorScreen(onBack = { navController.popBackStack() }) }
        composable("tool_timezone_calc") { TimeZoneConverterScreen(onBack = { navController.popBackStack() }) }

        // --- 2. MONEY & FINANCE (11-20) ---
        composable("tool_expense_tracker") { ExpenseTrackerScreen(onBack = { navController.popBackStack() }) }
        composable("tool_budget_manager") { FamilyBudgetScreen(onBack = { navController.popBackStack() }) }
        composable("tool_bill_reminder") { BillReminderScreen(onBack = { navController.popBackStack() }) }
        composable("tool_emi_reminder") { EMIReminderScreen(onBack = { navController.popBackStack() }) }
        composable("tool_subscription_tracker") { SubscriptionTrackerScreen(onBack = { navController.popBackStack() }) }
        composable("tool_savings_goal") { SavingsGoalScreen(onBack = { navController.popBackStack() }) }
        composable("tool_split_bill") { SplitBillScreen(onBack = { navController.popBackStack() }) }
        composable("tool_loan_calc") { LoanCalculatorScreen(onBack = { navController.popBackStack() }) }
        composable("tool_investment_tracker") { InvestmentTrackerScreen(onBack = { navController.popBackStack() }) }
        composable("tool_networth_tracker") { NetWorthTrackerScreen(onBack = { navController.popBackStack() }) }

        // --- 3. PHONE TOOLS (21-30) ---
        composable("tool_storage_analyzer") { StorageAnalyzerScreen(onBack = { navController.popBackStack() }) }
        composable("tool_dup_photos") { DuplicatePhotoFinderScreen(onBack = { navController.popBackStack() }) }
        composable("tool_dup_files") { DuplicateFileFinderScreen(onBack = { navController.popBackStack() }) }
        composable("tool_battery_monitor") { BatteryMonitorScreen(onBack = { navController.popBackStack() }) }
        composable("tool_charging_tracker") { ChargingTrackerScreen(onBack = { navController.popBackStack() }) }
        composable("tool_app_usage") { AppUsageDashboardScreen(onBack = { navController.popBackStack() }) }
        composable("tool_speed_monitor") { InternetSpeedMonitorScreen(onBack = { navController.popBackStack() }) }
        composable("tool_wifi_info") { WiFiInfoScreen(onBack = { navController.popBackStack() }) }
        composable("tool_qr_scanner") { QRScannerScreen(onBack = { navController.popBackStack() }) }
        composable("tool_clipboard_manager") { ClipboardManagerScreen(onBack = { navController.popBackStack() }) }

        // --- 4. AI TOOLS (31-40) ---
        composable("tool_ai_voicenotes") { AIVoiceNotesScreen(onBack = { navController.popBackStack() }) }
        composable("tool_meeting_summarizer") { MeetingSummarizerScreen(onBack = { navController.popBackStack() }) }
        composable("tool_pdf_summarizer") { PDFSummarizerScreen(onBack = { navController.popBackStack() }) }
        composable("tool_homework_helper") { HomeworkHelperScreen(onBack = { navController.popBackStack() }) }
        composable("tool_email_writer") { EmailWriterScreen(onBack = { navController.popBackStack() }) }
        composable("tool_whatsapp_reply") { WhatsAppReplyScreen(onBack = { navController.popBackStack() }) }
        composable("tool_grammar_checker") { GrammarCheckerScreen(onBack = { navController.popBackStack() }) }
        composable("tool_resume_builder") { ResumeBuilderScreen(onBack = { navController.popBackStack() }) }
        composable("tool_image_caption") { ImageCaptionGeneratorScreen(onBack = { navController.popBackStack() }) }
        composable("tool_personal_ai") { AIAssistantScreen(onBack = { navController.popBackStack() }) }

        // --- 5. STUDENTS (41-50) ---
        composable("tool_study_planner") { StudyPlannerScreen(onBack = { navController.popBackStack() }) }
        composable("tool_exam_countdown") { ExamCountdownScreen(onBack = { navController.popBackStack() }) }
        composable("tool_homework_tracker") { HomeworkTrackerScreen(onBack = { navController.popBackStack() }) }
        composable("tool_flashcards") { FlashcardsScreen(onBack = { navController.popBackStack() }) }
        composable("tool_pomodoro_timer") { PomodoroTimerScreen(onBack = { navController.popBackStack() }) }
        composable("tool_class_timetable") { ClassTimetableScreen(onBack = { navController.popBackStack() }) }
        composable("tool_attendance_calc") { AttendanceCalculatorScreen(onBack = { navController.popBackStack() }) }
        composable("tool_gpa_calc") { GPACalculatorScreen(onBack = { navController.popBackStack() }) }
        composable("tool_notes_organizer") { NotesOrganizerScreen(onBack = { navController.popBackStack() }) }
        composable("tool_paper_organizer") { QuestionPaperOrganizerScreen(onBack = { navController.popBackStack() }) }

        // --- 6. HOME & FAMILY (51-60) ---
        composable("tool_family_expense") { FamilyExpenseScreen(onBack = { navController.popBackStack() }) }
        composable("tool_grocery_list") { GroceryListScreen(onBack = { navController.popBackStack() }) }
        composable("tool_household_inventory") { HouseholdInventoryScreen(onBack = { navController.popBackStack() }) }
        composable("tool_home_maint") { HomeMaintenanceScreen(onBack = { navController.popBackStack() }) }
        composable("tool_warranty_tracker") { WarrantyTrackerScreen(onBack = { navController.popBackStack() }) }
        composable("tool_electricity_tracker") { ElectricityTrackerScreen(onBack = { navController.popBackStack() }) }
        composable("tool_water_usage") { WaterUsageScreen(onBack = { navController.popBackStack() }) }
        composable("tool_family_calendar") { FamilyCalendarScreen(onBack = { navController.popBackStack() }) }
        composable("tool_family_tasks") { FamilyTasksScreen(onBack = { navController.popBackStack() }) }
        composable("tool_doc_organizer") { ImportantDocsScreen(onBack = { navController.popBackStack() }) }

        // --- 7. VEHICLE (61-70) ---
        composable("tool_vehicle_service") { VehicleServiceScreen(onBack = { navController.popBackStack() }) }
        composable("tool_fuel_tracker") { FuelTrackerScreen(onBack = { navController.popBackStack() }) }
        composable("tool_insurance_reminder") { InsuranceReminderScreen(onBack = { navController.popBackStack() }) }
        composable("tool_puc_reminder") { PUCReminderScreen(onBack = { navController.popBackStack() }) }
        composable("tool_vehicle_locker") { VehicleDocumentLockerScreen(onBack = { navController.popBackStack() }) }
        composable("tool_mileage_calc") { MileageCalcScreen(onBack = { navController.popBackStack() }) }
        composable("tool_car_maint") { MaintenanceLogScreen(isCar = true, onBack = { navController.popBackStack() }) }
        composable("tool_bike_maint") { MaintenanceLogScreen(isCar = false, onBack = { navController.popBackStack() }) }
        composable("tool_trip_cost") { TripCostScreen(onBack = { navController.popBackStack() }) }
        composable("tool_vehicle_dashboard") { VehicleDashboardScreen(onBack = { navController.popBackStack() }) }

        // --- 8. HEALTH & LIFESTYLE (71-80) ---
        composable("tool_water_reminder") { WaterReminderScreen(onBack = { navController.popBackStack() }) }
        composable("tool_sleep_journal") { SleepJournalScreen(onBack = { navController.popBackStack() }) }
        composable("tool_walking_tracker") { WalkingTrackerScreen(onBack = { navController.popBackStack() }) }
        composable("tool_habit_tracker") { HabitTrackerScreen(onBack = { navController.popBackStack() }) }
        composable("tool_meditation_timer") { MeditationTimerScreen(onBack = { navController.popBackStack() }) }
        composable("tool_workout_planner") { WorkoutPlannerScreen(onBack = { navController.popBackStack() }) }
        composable("tool_calorie_journal") { CalorieJournalScreen(onBack = { navController.popBackStack() }) }
        composable("tool_medicine_reminder") { MedicineReminderScreen(onBack = { navController.popBackStack() }) }
        composable("tool_health_appointment") { HealthAppointmentScreen(onBack = { navController.popBackStack() }) }
        composable("tool_health_record") { HealthRecordScreen(onBack = { navController.popBackStack() }) }

        // --- 9. SAFETY & PRIVACY (81-90) ---
        composable("tool_emergency_sos") { EmergencySOSScreen(onBack = { navController.popBackStack() }) }
        composable("tool_location_sharing") { LocationSharingScreen(onBack = { navController.popBackStack() }) }
        composable("tool_emergency_contacts") { EmergencyContactsScreen(onBack = { navController.popBackStack() }) }
        composable("tool_private_vault") { PrivateVaultScreen(onBack = { navController.popBackStack() }) }
        composable("tool_password_manager") { PasswordManagerScreen(onBack = { navController.popBackStack() }) }
        composable("tool_safety_timer") { SafetyTimerScreen(onBack = { navController.popBackStack() }) }
        composable("tool_lost_phone") { LostPhoneScreen(onBack = { navController.popBackStack() }) }
        composable("tool_medical_card") { MedicalCardScreen(onBack = { navController.popBackStack() }) }
        composable("tool_important_numbers") { ImportantNumbersScreen(onBack = { navController.popBackStack() }) }
        composable("tool_spam_organizer") { SpamMessageOrganizerScreen(onBack = { navController.popBackStack() }) }

        // --- 10. CREATIVE & SOCIAL (91-100) ---
        composable("tool_status_caption") { StatusCaptionScreen(onBack = { navController.popBackStack() }) }
        composable("tool_insta_caption") { InstaCaptionScreen(onBack = { navController.popBackStack() }) }
        composable("tool_ai_story") { AIStoryGeneratorScreen(onBack = { navController.popBackStack() }) }
        composable("tool_quote_maker") { QuoteMakerScreen(onBack = { navController.popBackStack() }) }
        composable("tool_birthday_invitation") { BirthdayInvitationScreen(onBack = { navController.popBackStack() }) }
        composable("tool_festival_poster") { FestivalPosterScreen(onBack = { navController.popBackStack() }) }
        composable("tool_photo_collage") { PhotoCollageScreen(onBack = { navController.popBackStack() }) }
        composable("tool_script_generator") { VideoScriptGeneratorScreen(onBack = { navController.popBackStack() }) }
        composable("tool_voice_journal") { VoiceJournalScreen(onBack = { navController.popBackStack() }) }
        composable("tool_life_organizer") { LifeOrganizerScreen(onBack = { navController.popBackStack() }) }
    }
}
