package com.example.domain.model

data class Category(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val toolCount: Int = 10,
    val order: Int = 0,
    val isVisible: Boolean = true
)

data class Tool(
    val id: String,
    val categoryId: String,
    val name: String,
    val description: String,
    val iconName: String,
    val route: String,
    val isFavorite: Boolean = false,
    val lastUsed: Long = 0L,
    val usageCount: Int = 0
)

object ToolRegistry {
    val categories = listOf(
        Category("daily_utilities", "Daily Utilities", "Calculators, converters & time tools", "calculate", 10, 1),
        Category("money_finance", "Money & Finance", "Budget, expenses, loans & savings", "payments", 10, 2),
        Category("phone_tools", "Phone Tools", "Storage, battery, speed & QR scanner", "smartphone", 10, 3),
        Category("ai_tools", "AI Tools", "Voice notes, summarizer & smart writer", "auto_awesome", 10, 4),
        Category("students", "Students", "Study planner, pomodoro, exams & GPA", "school", 10, 5),
        Category("home_family", "Home & Family", "Groceries, tasks, bills & inventory", "home", 10, 6),
        Category("vehicle", "Vehicle", "Service, fuel, mileage & documents", "directions_car", 10, 7),
        Category("health_lifestyle", "Health & Lifestyle", "Water, habits, sleep, meds & workout", "favorite", 10, 8),
        Category("safety_privacy", "Safety & Privacy", "SOS alert, vault, passwords & contacts", "security", 10, 9),
        Category("creative_social", "Creative & Social", "Captions, quotes, posters & scripts", "palette", 10, 10)
    )

    val allTools = listOf(
        // 1. DAILY UTILITIES (1-10)
        Tool("tool_1", "daily_utilities", "All-in-One Utility", "Quick shortcuts hub to all top daily utility tools", "widgets", "tool_daily_hub"),
        Tool("tool_2", "daily_utilities", "Smart Calculator", "Arithmetic calculations with history and percentages", "calculate", "tool_calculator"),
        Tool("tool_3", "daily_utilities", "Unit Converter", "Convert length, weight, area, temp and speed", "sync_alt", "tool_unit_converter"),
        Tool("tool_4", "daily_utilities", "Age Calculator", "Precise age in years, months, days & next birthday", "cake", "tool_age_calc"),
        Tool("tool_5", "daily_utilities", "Date Difference Calculator", "Calculate total days, weeks & months between dates", "date_range", "tool_date_diff"),
        Tool("tool_6", "daily_utilities", "GST Calculator", "Calculate GST amounts with CGST/SGST breakdown", "receipt_long", "tool_gst_calc"),
        Tool("tool_7", "daily_utilities", "EMI Calculator", "Calculate loan monthly EMIs and interest payable", "account_balance", "tool_emi_calc"),
        Tool("tool_8", "daily_utilities", "Percentage Calculator", "Compute discounts, changes and percentage splits", "percent", "tool_percentage_calc"),
        Tool("tool_9", "daily_utilities", "Tip Calculator", "Split bill with custom tip percentage per person", "local_dining", "tool_tip_calc"),
        Tool("tool_10", "daily_utilities", "Time Zone Converter", "Compare global times across major world cities", "schedule", "tool_timezone_calc"),

        // 2. MONEY & FINANCE (11-20)
        Tool("tool_11", "money_finance", "Daily Expense Tracker", "Log your everyday expenses with categories and totals", "receipt", "tool_expense_tracker"),
        Tool("tool_12", "money_finance", "Family Budget Manager", "Set monthly targets and track category limits", "pie_chart", "tool_budget_manager"),
        Tool("tool_13", "money_finance", "Bill Reminder", "Track upcoming utility bills, rent and dues", "notifications_active", "tool_bill_reminder"),
        Tool("tool_14", "money_finance", "EMI Reminder", "Manage loan repayment schedules and dates", "credit_card", "tool_emi_reminder"),
        Tool("tool_15", "money_finance", "Subscription Tracker", "Keep track of active subscriptions and monthly costs", "autorenew", "tool_subscription_tracker"),
        Tool("tool_16", "money_finance", "Savings Goal", "Set savings targets and monitor progress visually", "savings", "tool_savings_goal"),
        Tool("tool_17", "money_finance", "Split Bill", "Divide group dinners, outings and trip expenses", "call_split", "tool_split_bill"),
        Tool("tool_18", "money_finance", "Loan Calculator", "Compute principal, interest breakdown and schedule", "monetization_on", "tool_loan_calc"),
        Tool("tool_19", "money_finance", "Investment Portfolio Tracker", "Monitor stocks, mutual funds, gold and returns", "trending_up", "tool_investment_tracker"),
        Tool("tool_20", "money_finance", "Net Worth Tracker", "Calculate total assets vs liabilities over time", "account_balance_wallet", "tool_networth_tracker"),

        // 3. PHONE TOOLS (21-30)
        Tool("tool_21", "phone_tools", "Storage Analyzer", "Inspect internal storage used, free space and stats", "sd_storage", "tool_storage_analyzer"),
        Tool("tool_22", "phone_tools", "Duplicate Photo Finder", "Detect similar and duplicate media to free up space", "photo_library", "tool_dup_photos"),
        Tool("tool_23", "phone_tools", "Duplicate File Finder", "Scan large and duplicate downloads for cleanup", "folder_copy", "tool_dup_files"),
        Tool("tool_24", "phone_tools", "Battery Monitor", "Real-time battery percentage, temperature and health", "battery_charging_full", "tool_battery_monitor"),
        Tool("tool_25", "phone_tools", "Charging Tracker", "Estimate full charge time and charging power rate", "bolt", "tool_charging_tracker"),
        Tool("tool_26", "phone_tools", "App Usage Dashboard", "Overview of installed apps, permissions and usage", "apps", "tool_app_usage"),
        Tool("tool_27", "phone_tools", "Internet Speed Monitor", "Measure live download throughput and network latency", "speed", "tool_speed_monitor"),
        Tool("tool_28", "phone_tools", "Wi-Fi Signal Information", "Check Wi-Fi network strength, frequency & IP status", "wifi", "tool_wifi_info"),
        Tool("tool_29", "phone_tools", "QR/Barcode Scanner", "Scan and generate QR codes and barcodes quickly", "qr_code_scanner", "tool_qr_scanner"),
        Tool("tool_30", "phone_tools", "Clipboard Manager", "Save, organize and restore copied text snippets", "content_paste", "tool_clipboard_manager"),

        // 4. AI TOOLS (31-40)
        Tool("tool_31", "ai_tools", "AI Voice Notes", "Dictate speech to text with AI key insights extraction", "mic", "tool_ai_voicenotes"),
        Tool("tool_32", "ai_tools", "Meeting Summarizer", "Turn meeting transcripts into decisions & action items", "groups", "tool_meeting_summarizer"),
        Tool("tool_33", "ai_tools", "PDF Summarizer", "Extract executive summary and bullet takeaways from text", "picture_as_pdf", "tool_pdf_summarizer"),
        Tool("tool_34", "ai_tools", "Homework Helper", "Get step-by-step problem explanations and formulas", "menu_book", "tool_homework_helper"),
        Tool("tool_35", "ai_tools", "Email Writer", "Draft polished professional, formal or friendly emails", "email", "tool_email_writer"),
        Tool("tool_36", "ai_tools", "WhatsApp Reply Generator", "Generate smart, polite or witty message replies", "chat", "tool_whatsapp_reply"),
        Tool("tool_37", "ai_tools", "Grammar Checker", "Improve sentence structure, clarity and tone", "spellcheck", "tool_grammar_checker"),
        Tool("tool_38", "ai_tools", "Resume Builder", "Create modern professional CV profiles ready to copy", "badge", "tool_resume_builder"),
        Tool("tool_39", "ai_tools", "Image Caption Generator", "Generate engaging social captions from mood and vibe", "photo_camera", "tool_image_caption"),
        Tool("tool_40", "ai_tools", "Personal AI Assistant", "Contextual conversational assistant for questions & tasks", "smart_toy", "tool_personal_ai"),

        // 5. STUDENTS (41-50)
        Tool("tool_41", "students", "Study Planner", "Plan revision sessions, subjects and daily study targets", "assignment", "tool_study_planner"),
        Tool("tool_42", "students", "Exam Countdown", "Live countdown timer to upcoming tests and exams", "alarm", "tool_exam_countdown"),
        Tool("tool_43", "students", "Homework Tracker", "Track assignments with due dates and priorities", "task_alt", "tool_homework_tracker"),
        Tool("tool_44", "students", "Flashcards", "Create question-answer flashcard decks for quick review", "style", "tool_flashcards"),
        Tool("tool_45", "students", "Pomodoro Timer", "Focus study timer with 25-minute intervals and breaks", "timer", "tool_pomodoro_timer"),
        Tool("tool_46", "students", "Class Timetable", "Weekly schedule for classes, professors and rooms", "calendar_view_week", "tool_class_timetable"),
        Tool("tool_47", "students", "Attendance Calculator", "Calculate attendance % and needed classes for 75%", "fact_check", "tool_attendance_calc"),
        Tool("tool_48", "students", "GPA Calculator", "Calculate semester GPA and cumulative CGPA score", "grade", "tool_gpa_calc"),
        Tool("tool_49", "students", "Notes Organizer", "Organize study notes by subject and keywords", "note_alt", "tool_notes_organizer"),
        Tool("tool_50", "students", "Question Paper Organizer", "Catalog past exam papers, year and subject keys", "description", "tool_paper_organizer"),

        // 6. HOME & FAMILY (51-60)
        Tool("tool_51", "home_family", "Family Expense Manager", "Track shared household expenses and member splits", "family_restroom", "tool_family_expense"),
        Tool("tool_52", "home_family", "Grocery List", "Interactive checklist with categorized pantry items", "shopping_cart", "tool_grocery_list"),
        Tool("tool_53", "home_family", "Household Inventory", "Catalog appliances, furniture and home items", "inventory_2", "tool_household_inventory"),
        Tool("tool_54", "home_family", "Home Maintenance Reminder", "Service alerts for AC, water filter and deep cleaning", "build", "tool_home_maint"),
        Tool("tool_55", "home_family", "Appliance Warranty Tracker", "Store purchase dates and warranty expiry alerts", "verified", "tool_warranty_tracker"),
        Tool("tool_56", "home_family", "Electricity Bill Tracker", "Log monthly meter readings, units (kWh) and costs", "electric_bolt", "tool_electricity_tracker"),
        Tool("tool_57", "home_family", "Water Usage Tracker", "Monitor daily household water consumption in liters", "water_drop", "tool_water_usage"),
        Tool("tool_58", "home_family", "Family Calendar", "Centralized calendar for birthdays, rituals and trips", "event", "tool_family_calendar"),
        Tool("tool_59", "home_family", "Family Task Manager", "Assign chores and tasks to family members", "checklist", "tool_family_tasks"),
        Tool("tool_60", "home_family", "Important Documents Organizer", "Secure index for Aadhaar, PAN, passports & certificates", "folder_shared", "tool_doc_organizer"),

        // 7. VEHICLE (61-70)
        Tool("tool_61", "vehicle", "Vehicle Service Reminder", "Track upcoming oil change and periodic servicing", "car_repair", "tool_vehicle_service"),
        Tool("tool_62", "vehicle", "Fuel Expense Tracker", "Log fuel purchases, liters, rates and odometer", "local_gas_station", "tool_fuel_tracker"),
        Tool("tool_63", "vehicle", "Insurance Reminder", "Track policy expiry dates and renewal reminders", "policy", "tool_insurance_reminder"),
        Tool("tool_64", "vehicle", "PUC Reminder", "Monitor Pollution Under Control certificate renewal", "eco", "tool_puc_reminder"),
        Tool("tool_65", "vehicle", "Vehicle Document Locker", "Keep RC, Insurance, DL and PUC details handy", "security", "tool_vehicle_locker"),
        Tool("tool_66", "vehicle", "Mileage Calculator", "Calculate vehicle km/L mileage and cost per km", "speed", "tool_mileage_calc"),
        Tool("tool_67", "vehicle", "Car Maintenance Log", "Record car oil, brake pads, battery & tyre changes", "directions_car", "tool_car_maint"),
        Tool("tool_68", "vehicle", "Bike Maintenance Log", "Record bike chain lube, spark plug & engine oil logs", "two_wheeler", "tool_bike_maint"),
        Tool("tool_69", "vehicle", "Trip Cost Calculator", "Estimate fuel cost, tolls and per-person split for road trips", "commute", "tool_trip_cost"),
        Tool("tool_70", "vehicle", "Vehicle Expense Dashboard", "Comprehensive summary of all vehicle related spending", "insights", "tool_vehicle_dashboard"),

        // 8. HEALTH & LIFESTYLE (71-80)
        Tool("tool_71", "health_lifestyle", "Water Reminder", "Track daily water consumption with visual intake goal", "local_drink", "tool_water_reminder"),
        Tool("tool_72", "health_lifestyle", "Sleep Journal", "Log sleep hours, wake times and restful sleep quality", "bedtime", "tool_sleep_journal"),
        Tool("tool_73", "health_lifestyle", "Walking Tracker", "Record daily steps, walking distance and calories burned", "directions_walk", "tool_walking_tracker"),
        Tool("tool_74", "health_lifestyle", "Habit Tracker", "Build consistent daily habits with streaks and calendar", "check_circle", "tool_habit_tracker"),
        Tool("tool_75", "health_lifestyle", "Meditation Timer", "Guided breathing mindfulness timer with pacing cues", "self_improvement", "tool_meditation_timer"),
        Tool("tool_76", "health_lifestyle", "Workout Planner", "Schedule weekly gym and home exercise routines", "fitness_center", "tool_workout_planner"),
        Tool("tool_77", "health_lifestyle", "Calorie Journal", "Log meals and track calorie intake vs target", "restaurant", "tool_calorie_journal"),
        Tool("tool_78", "health_lifestyle", "Medicine Reminder", "Timely medication reminders with food schedule", "medication", "tool_medicine_reminder"),
        Tool("tool_79", "health_lifestyle", "Health Appointment Reminder", "Track upcoming doctor visits and checkups", "medical_services", "tool_health_appointment"),
        Tool("tool_80", "health_lifestyle", "Personal Health Record Organizer", "Blood group, allergies, medications and health history", "health_and_safety", "tool_health_record"),

        // 9. SAFETY & PRIVACY (81-90)
        Tool("tool_81", "safety_privacy", "Emergency SOS", "One-tap emergency siren, quick call and GPS sharing", "sos", "tool_emergency_sos"),
        Tool("tool_82", "safety_privacy", "Family Location Sharing", "Get instant GPS coordinates and quick Maps link", "location_on", "tool_location_sharing"),
        Tool("tool_83", "safety_privacy", "Emergency Contacts", "Quick-dial cards for family, police, and doctor", "contact_phone", "tool_emergency_contacts"),
        Tool("tool_84", "safety_privacy", "Private Document Locker", "Protected metadata vault for sensitive IDs", "lock", "tool_private_vault"),
        Tool("tool_85", "safety_privacy", "Offline Password Manager", "Secure offline encrypted password vault with strength meter", "key", "tool_password_manager"),
        Tool("tool_86", "safety_privacy", "Personal Safety Timer", "Safety countdown: auto-triggers SOS if not checked in", "hourglass_top", "tool_safety_timer"),
        Tool("tool_87", "safety_privacy", "Lost Phone Information", "Generate 'If Found Contact' owner lockscreen info card", "phonelink_lock", "tool_lost_phone"),
        Tool("tool_88", "safety_privacy", "Emergency Medical Card", "ICE emergency medical profile for first responders", "badge", "tool_medical_card"),
        Tool("tool_89", "safety_privacy", "Important Numbers", "Official Indian national emergency & helpline directory", "phone_in_talk", "tool_important_numbers"),
        Tool("tool_90", "safety_privacy", "Spam/Scam Message Organizer", "Identify phishing patterns and report fraud instructions", "report_problem", "tool_spam_organizer"),

        // 10. CREATIVE & SOCIAL (91-100)
        Tool("tool_91", "creative_social", "Status Caption Generator", "Motivational, attitude and life status lines to copy", "format_quote", "tool_status_caption"),
        Tool("tool_92", "creative_social", "Instagram Caption Generator", "Aesthetic captions with popular trending hashtags", "tag", "tool_insta_caption"),
        Tool("tool_93", "creative_social", "AI Story Generator", "Craft imaginative stories from themes and characters", "auto_stories", "tool_ai_story"),
        Tool("tool_94", "creative_social", "Quote Maker", "Generate styled inspirational quotes ready to share", "format_paint", "tool_quote_maker"),
        Tool("tool_95", "creative_social", "Birthday Invitation Maker", "Generate beautiful birthday party invitation cards", "celebration", "tool_birthday_invitation"),
        Tool("tool_96", "creative_social", "Festival Poster Maker", "Create festival greeting cards (Diwali, Eid, Holi...)", "festival", "tool_festival_poster"),
        Tool("tool_97", "creative_social", "Photo Collage Maker", "Layout designer for organizing photo memories", "grid_view", "tool_photo_collage"),
        Tool("tool_98", "creative_social", "Short Video Script Generator", "Hook, body, and CTA script template for reels/shorts", "videocam", "tool_script_generator"),
        Tool("tool_99", "creative_social", "Voice Journal", "Record spoken thoughts with mood and reflection notes", "record_voice_over", "tool_voice_journal"),
        Tool("tool_100", "creative_social", "AI Life Organizer", "Holistic day overview unifying tasks, budget & habits", "stars", "tool_life_organizer")
    )
}
