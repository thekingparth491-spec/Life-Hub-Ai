package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "GeminiService"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateResponse(userPrompt: String, systemInstruction: String = "You are LifeHub AI, a helpful, polite, and intelligent assistant inside an all-in-one Android super app."): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        // If no API key or placeholder key, use local fallback
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d(TAG, "No valid Gemini API key found in BuildConfig, using local AI engine")
            return@withContext LocalAIEngine.generateSmartResponse(userPrompt)
        }

        try {
            val root = JSONObject().apply {
                val contents = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        val parts = JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", userPrompt)
                            })
                        }
                        put("parts", parts)
                    })
                }
                put("contents", contents)

                put("systemInstruction", JSONObject().apply {
                    val parts = JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", systemInstruction)
                        })
                    }
                    put("parts", parts)
                })

                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("topP", 0.95)
                    put("maxOutputTokens", 800)
                })
            }

            val requestBody = root.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.w(TAG, "Gemini API call returned code: ${response.code}, falling back to local AI")
                return@withContext LocalAIEngine.generateSmartResponse(userPrompt)
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            if (!text.isNullOrBlank()) {
                text.trim()
            } else {
                LocalAIEngine.generateSmartResponse(userPrompt)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error calling Gemini API: ${e.message}", e)
            LocalAIEngine.generateSmartResponse(userPrompt)
        }
    }
}

object LocalAIEngine {
    fun generateSmartResponse(query: String): String {
        val q = query.lowercase().trim()

        // 1. Math evaluation: e.g. 500 * 12, 1200 + 450, 15% of 2000
        val percentageMatch = Regex("(\\d+(?:\\.\\d+)?)\\s*%\\s*(?:of)?\\s*(\\d+(?:\\.\\d+)?)").find(q)
        if (percentageMatch != null) {
            val pct = percentageMatch.groupValues[1].toDoubleOrNull() ?: 0.0
            val total = percentageMatch.groupValues[2].toDoubleOrNull() ?: 0.0
            val result = (pct / 100.0) * total
            return "🧮 **Calculation Result**:\n\n• **Formula**: $pct% of $total\n• **Answer**: **$result**\n\nNeed to calculate GST or split costs? Check out the **GST Calculator** or **Split Bill** tool in Utilities!"
        }

        // 2. Unit conversion evaluation: e.g. 15 km to miles, 10 feet to meters
        if (q.contains("km") && q.contains("mile")) {
            val num = Regex("(\\d+(?:\\.\\d+)?)").find(q)?.groupValues?.get(1)?.toDoubleOrNull() ?: 1.0
            val converted = num * 0.621371
            return "📏 **Unit Conversion**:\n\n• **$num Kilometers (km)** = **${String.format("%.3f", converted)} Miles (mi)**\n\n*1 km ≈ 0.621371 miles.* Open **Unit Converter** for 40+ units!"
        }
        if (q.contains("mile") && q.contains("km")) {
            val num = Regex("(\\d+(?:\\.\\d+)?)").find(q)?.groupValues?.get(1)?.toDoubleOrNull() ?: 1.0
            val converted = num * 1.60934
            return "📏 **Unit Conversion**:\n\n• **$num Miles (mi)** = **${String.format("%.3f", converted)} Kilometers (km)**\n\n*1 mile ≈ 1.60934 km.*"
        }
        if (q.contains("feet") && q.contains("meter")) {
            val num = Regex("(\\d+(?:\\.\\d+)?)").find(q)?.groupValues?.get(1)?.toDoubleOrNull() ?: 1.0
            val converted = num * 0.3048
            return "📏 **Unit Conversion**:\n\n• **$num Feet (ft)** = **${String.format("%.3f", converted)} Meters (m)**\n\n*1 foot = 0.3048 meters.*"
        }
        if (q.contains("kg") && (q.contains("pound") || q.contains("lbs"))) {
            val num = Regex("(\\d+(?:\\.\\d+)?)").find(q)?.groupValues?.get(1)?.toDoubleOrNull() ?: 1.0
            val converted = num * 2.20462
            return "⚖️ **Weight Conversion**:\n\n• **$num Kilograms (kg)** = **${String.format("%.2f", converted)} Pounds (lbs)**\n\n*1 kg ≈ 2.20462 lbs.*"
        }

        // 3. Simple basic arithmetic: "calculate 450 + 230"
        val addMatch = Regex("(\\d+(?:\\.\\d+)?)\\s*\\+\\s*(\\d+(?:\\.\\d+)?)").find(q)
        if (addMatch != null) {
            val a = addMatch.groupValues[1].toDoubleOrNull() ?: 0.0
            val b = addMatch.groupValues[2].toDoubleOrNull() ?: 0.0
            return "🧮 **Calculation**:\n\n• **$a + $b** = **${a + b}**"
        }
        val multMatch = Regex("(\\d+(?:\\.\\d+)?)\\s*\\*\\s*(\\d+(?:\\.\\d+)?)").find(q)
        if (multMatch != null) {
            val a = multMatch.groupValues[1].toDoubleOrNull() ?: 0.0
            val b = multMatch.groupValues[2].toDoubleOrNull() ?: 0.0
            return "🧮 **Calculation**:\n\n• **$a × $b** = **${a * b}**"
        }

        // 4. Intent & Topic handlers
        return when {
            q.contains("hello") || q.contains("hi") || q.contains("hey") ->
                "Hello! 👋 I'm **LifeHub AI**, your all-in-one assistant.\n\nI can help you manage your daily routine, calculate expenses, organize study habits, monitor vehicles, and navigate our 100 built-in tools.\n\nTry asking:\n• *\"Add ₹350 lunch expense\"*\n• *\"Convert 15 km to miles\"*\n• *\"Calculate 18% GST on ₹4,000\"*\n• *\"Pomodoro study technique\"*"

            q.contains("how are you") ->
                "I'm feeling great and all 100 LifeHub tools are operating at peak efficiency! 🚀 How can I assist you right now?"

            q.contains("who are you") || q.contains("what can you do") || q.contains("features") || q.contains("help") ->
                "I am **LifeHub AI**, the central intelligence of this Android super app. Here are just a few things I can do for you:\n\n" +
                "• 💸 **Money & Finance**: Log expenses, calculate EMIs, track investments and family budgets.\n" +
                "• 🧮 **Utilities**: Instant unit conversions, GST, time zones, age calculator, and math history.\n" +
                "• 🎓 **Students**: Study planning, flashcards, GPA calculation, and attendance goals.\n" +
                "• 🚗 **Vehicles**: Fuel tracking, mileage analysis, and service reminders.\n" +
                "• 💧 **Health**: Water intake logging, sleep journal, and mindful meditation.\n" +
                "• 🛡️ **Safety**: Emergency SOS strobe & siren, encrypted vault, and 112 quick dial.\n\n" +
                "Just type what you need or tap the action button!"

            q.contains("joke") ->
                "Why do programmers prefer dark mode? 🕶️\nBecause light attracts bugs! 😄"

            q.contains("thank") ->
                "You're very welcome! Always here whenever you need a calculation, note, or quick tool. Have a productive day! 🌟"

            q.contains("study") || q.contains("exam") || q.contains("learn") ->
                "📚 **Study Strategy Plan**:\n\n" +
                "1. **Use the 25/5 Pomodoro technique**: 25 minutes of high-intensity focus with zero distractions, then a 5-minute breather.\n" +
                "2. **Active Recall**: Test your memory with our built-in **Flashcards** tool.\n" +
                "3. **Spaced Repetition**: Review difficult concepts after 1 day, 3 days, and 1 week.\n" +
                "4. **Class Schedule**: Keep your semester organized using the **Class Timetable** tool."

            q.contains("water") || q.contains("drink") || q.contains("hydrate") ->
                "💧 **Daily Hydration Guide**:\n\n" +
                "• **Recommended intake**: 2,500 ml – 3,000 ml per day.\n" +
                "• Drinking a glass right after waking up jumpstarts your metabolism and alertness.\n" +
                "• Track each glass in the **Water Reminder** tool under Health & Lifestyle!"

            q.contains("sleep") || q.contains("tired") || q.contains("rest") ->
                "😴 **Rest & Sleep Tips**:\n\n" +
                "• Aim for 7–8 hours of consistent sleep each night.\n" +
                "• Avoid screens 30 minutes before bed to allow natural melatonin production.\n" +
                "• Track your sleep duration and sleep quality score in the **Sleep Journal** tool."

            q.contains("vehicle") || q.contains("car") || q.contains("bike") || q.contains("mileage") || q.contains("tyre") ->
                "🚗 **Vehicle Optimization Tip**:\n\n" +
                "• Keeping tyres at manufacturer-recommended PSI can boost fuel efficiency by up to 3%.\n" +
                "• Change engine oil on schedule (typically every 5,000–10,000 km).\n" +
                "• Store your RC, Insurance, and PUC in the **Vehicle Document Locker** so you never get caught without them."

            q.contains("safety") || q.contains("sos") || q.contains("emergency") ->
                "🛡️ **Safety First**:\n\n" +
                "• Use the **Emergency SOS** tool for 1-tap dialing to 112 (National Emergency), 108 (Ambulance), and 100 (Police).\n" +
                "• You can also trigger an emergency siren strobe and broadcast your live GPS coordinates to loved ones."

            q.contains("expense") || q.contains("spend") || q.contains("money") || q.contains("budget") ->
                "💸 **Expense Tracking**:\n\n" +
                "I detected you want to manage your finances. You can tell me *\"Add ₹500 petrol expense\"* and I will log it directly into your Expense Tracker! You can also view your monthly budget in the **Family Budget** tool."

            q.contains("emi") || q.contains("loan") ->
                "🏦 **Loan & EMI Insights**:\n\n" +
                "• EMI formula: E = P × r × (1 + r)^n / ((1 + r)^n - 1)\n" +
                "• Always verify the interest rate type (reducing balance vs flat rate).\n" +
                "• Tap below to open the full **EMI Calculator** for monthly repayment and amortization charts."

            else ->
                "💡 **LifeHub AI Insight**:\n\n" +
                "I've processed: *\"$query\"*.\n\n" +
                "You can ask me to log expenses, perform math conversions, plan your study routine, or open any of LifeHub's 100 specialized tools. How else can I assist you?"
        }
    }
}
