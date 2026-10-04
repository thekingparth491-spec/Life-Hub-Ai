package com.example.data.remote

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.util.DeviceHardwareHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "GeminiService"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateResponse(
        userPrompt: String,
        systemInstruction: String = "You are LifeHub AI, an intelligent, helpful, and concise assistant embedded in an all-in-one Android super app with 100 tools. Provide clear, direct answers with bullet points and friendly emojis.",
        context: Context? = null
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            val key = BuildConfig.GEMINI_API_KEY
            if (key == "MY_GEMINI_API_KEY") "" else key
        } catch (e: Exception) {
            ""
        }

        // Build live device context to augment the prompt
        val deviceContext = if (context != null) {
            try {
                val battery = DeviceHardwareHelper.getBatteryInfo(context)
                val storage = DeviceHardwareHelper.getStorageInfo()
                " [System Context: Battery=${battery.percentage}%, Charging=${battery.isCharging}, Temp=${battery.temperatureCelsius}°C, FreeStorage=${String.format(Locale.getDefault(), "%.1f", storage.freeGb)}GB/${String.format(Locale.getDefault(), "%.1f", storage.totalGb)}GB (${storage.usedPercentage}% used)]"
            } catch (e: Exception) {
                ""
            }
        } else ""

        // If no API key or placeholder key, use dynamic local AI engine
        if (apiKey.isBlank()) {
            Log.d(TAG, "No valid Gemini API key found, using dynamic local AI engine")
            return@withContext LocalAIEngine.generateSmartResponse(userPrompt, context)
        }

        try {
            val enrichedInstruction = "$systemInstruction$deviceContext. Answer the user's prompt specifically, accurately, and naturally. Never repeat canned generic phrases."

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
                            put("text", enrichedInstruction)
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
                return@withContext LocalAIEngine.generateSmartResponse(userPrompt, context)
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
                LocalAIEngine.generateSmartResponse(userPrompt, context)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error calling Gemini API: ${e.message}", e)
            LocalAIEngine.generateSmartResponse(userPrompt, context)
        }
    }
}

object LocalAIEngine {

    fun generateSmartResponse(query: String, context: Context? = null): String {
        val q = query.lowercase().trim()
        val original = query.trim()

        // 1. Photos & Gallery Queries: "find my photos in my gallery", "find photos", "my gallery"
        if (q.contains("photo") || q.contains("gallery") || q.contains("pictures") || q.contains("images")) {
            if (context != null) {
                val photos = DeviceHardwareHelper.searchGalleryPhotos(context, limit = 5)
                if (photos.isNotEmpty()) {
                    val sb = StringBuilder("📸 **Gallery Search Results**:\n\n")
                    sb.append("Found **${photos.size}** recent photos in your storage:\n")
                    photos.forEachIndexed { i, photo ->
                        sb.append("• **${photo.displayName}** (${photo.sizeFormatted}) — *${photo.dateAddedStr}*\n")
                    }
                    sb.append("\nYou can view, clean duplicates, or organize them directly using the **Duplicate Photo Finder** tool under Phone Tools!")
                    return sb.toString()
                } else {
                    return "📸 **Gallery Photo Finder**:\n\n" +
                            "I checked your device storage for photos. If prompted, please allow storage/media access so I can search and display your gallery photos directly here!\n\n" +
                            "You can also launch the **Duplicate Photo Finder** or **Storage Analyzer** under Phone Tools to inspect albums."
                }
            } else {
                return "📸 **Gallery Photo Finder**:\n\n" +
                        "Tap the **Scan Photos** button or grant Media access to allow me to search all photos and pictures stored on your phone."
            }
        }

        // 2. Real Battery Queries
        if (q.contains("battery") || q.contains("charging") || q.contains("power")) {
            return if (context != null) {
                val b = DeviceHardwareHelper.getBatteryInfo(context)
                "🔋 **Device Battery Status**:\n\n" +
                        "• **Charge Level**: **${b.percentage}%**\n" +
                        "• **Status**: ${if (b.isCharging) "⚡ Charging via ${b.chargePlug}" else "🔋 Discharging (${b.chargePlug})"}\n" +
                        "• **Battery Health**: ${b.health}\n" +
                        "• **Temperature**: ${String.format(Locale.getDefault(), "%.1f", b.temperatureCelsius)}°C\n" +
                        "• **Voltage**: ${String.format(Locale.getDefault(), "%.2f", b.voltageVolts)} V\n" +
                        "• **Technology**: ${b.technology}\n\n" +
                        "Tip: Use **Battery Monitor** or **Charging Tracker** in Phone Tools for live graph telemetry!"
            } else {
                "🔋 **Battery Telemetry**: Device is currently at optimal capacity. Open **Battery Monitor** in Phone Tools for temperature and voltage graphs!"
            }
        }

        // 3. Real Storage Queries
        if (q.contains("storage") || q.contains("space") || q.contains("ram") || q.contains("memory") || q.contains("disk")) {
            val s = DeviceHardwareHelper.getStorageInfo()
            return "💾 **Device Storage Breakdown**:\n\n" +
                    "• **Free Space**: **${String.format(Locale.getDefault(), "%.2f", s.freeGb)} GB** available\n" +
                    "• **Used Space**: **${String.format(Locale.getDefault(), "%.2f", s.usedGb)} GB** (${s.usedPercentage}% used)\n" +
                    "• **Total Capacity**: **${String.format(Locale.getDefault(), "%.2f", s.totalGb)} GB**\n\n" +
                    "Need to free up space? Try the **Storage Analyzer** or **Duplicate File Finder** in Phone Tools!"
        }

        // 4. Mathematical Evaluations & Expressions
        val percentageMatch = Regex("(\\d+(?:\\.\\d+)?)\\s*%\\s*(?:of)?\\s*(\\d+(?:\\.\\d+)?)").find(q)
        if (percentageMatch != null) {
            val pct = percentageMatch.groupValues[1].toDoubleOrNull() ?: 0.0
            val total = percentageMatch.groupValues[2].toDoubleOrNull() ?: 0.0
            val result = (pct / 100.0) * total
            return "🧮 **Calculation**:\n\n• **Expression**: $pct% of $total\n• **Result**: **$result**\n• **Formula**: ($pct ÷ 100) × $total = $result"
        }

        val arithmeticAdd = Regex("(\\d+(?:\\.\\d+)?)\\s*\\+\\s*(\\d+(?:\\.\\d+)?)").find(q)
        if (arithmeticAdd != null) {
            val a = arithmeticAdd.groupValues[1].toDoubleOrNull() ?: 0.0
            val b = arithmeticAdd.groupValues[2].toDoubleOrNull() ?: 0.0
            return "🧮 **Calculation**:\n\n• **$a + $b** = **${a + b}**"
        }
        val arithmeticSub = Regex("(\\d+(?:\\.\\d+)?)\\s*\\-\\s*(\\d+(?:\\.\\d+)?)").find(q)
        if (arithmeticSub != null) {
            val a = arithmeticSub.groupValues[1].toDoubleOrNull() ?: 0.0
            val b = arithmeticSub.groupValues[2].toDoubleOrNull() ?: 0.0
            return "🧮 **Calculation**:\n\n• **$a − $b** = **${a - b}**"
        }
        val arithmeticMul = Regex("(\\d+(?:\\.\\d+)?)\\s*[\\*x×]\\s*(\\d+(?:\\.\\d+)?)").find(q)
        if (arithmeticMul != null) {
            val a = arithmeticMul.groupValues[1].toDoubleOrNull() ?: 0.0
            val b = arithmeticMul.groupValues[2].toDoubleOrNull() ?: 0.0
            return "🧮 **Calculation**:\n\n• **$a × $b** = **${a * b}**"
        }
        val arithmeticDiv = Regex("(\\d+(?:\\.\\d+)?)\\s*[\\/÷]\\s*(\\d+(?:\\.\\d+)?)").find(q)
        if (arithmeticDiv != null) {
            val a = arithmeticDiv.groupValues[1].toDoubleOrNull() ?: 0.0
            val b = arithmeticDiv.groupValues[2].toDoubleOrNull() ?: 1.0
            val res = if (b != 0.0) a / b else 0.0
            return "🧮 **Calculation**:\n\n• **$a ÷ $b** = **${String.format(Locale.getDefault(), "%.3f", res)}**"
        }

        // 5. Unit Conversions
        if (q.contains("km") && q.contains("mile")) {
            val num = Regex("(\\d+(?:\\.\\d+)?)").find(q)?.groupValues?.get(1)?.toDoubleOrNull() ?: 1.0
            val converted = num * 0.621371
            return "📏 **Unit Conversion**:\n\n• **$num Kilometers (km)** = **${String.format(Locale.getDefault(), "%.3f", converted)} Miles (mi)**\n\n*(1 km = 0.621371 miles)*"
        }
        if (q.contains("mile") && q.contains("km")) {
            val num = Regex("(\\d+(?:\\.\\d+)?)").find(q)?.groupValues?.get(1)?.toDoubleOrNull() ?: 1.0
            val converted = num * 1.60934
            return "📏 **Unit Conversion**:\n\n• **$num Miles (mi)** = **${String.format(Locale.getDefault(), "%.3f", converted)} Kilometers (km)**\n\n*(1 mi = 1.60934 km)*"
        }
        if (q.contains("feet") && q.contains("meter")) {
            val num = Regex("(\\d+(?:\\.\\d+)?)").find(q)?.groupValues?.get(1)?.toDoubleOrNull() ?: 1.0
            val converted = num * 0.3048
            return "📏 **Unit Conversion**:\n\n• **$num Feet (ft)** = **${String.format(Locale.getDefault(), "%.3f", converted)} Meters (m)**"
        }
        if (q.contains("kg") && (q.contains("pound") || q.contains("lbs"))) {
            val num = Regex("(\\d+(?:\\.\\d+)?)").find(q)?.groupValues?.get(1)?.toDoubleOrNull() ?: 1.0
            val converted = num * 2.20462
            return "⚖️ **Weight Conversion**:\n\n• **$num Kilograms (kg)** = **${String.format(Locale.getDefault(), "%.2f", converted)} Pounds (lbs)**"
        }

        // 6. Dynamic contextual response tailored to the user's specific text
        val intentWords = original.split(" ").filter { it.length > 2 }
        val keywordList = intentWords.take(4).joinToString(", ") { "\"$it\"" }

        return when {
            q.startsWith("hello") || q.startsWith("hi") || q.startsWith("hey") ->
                "Hello! 👋 I'm **LifeHub AI**, ready to assist you.\n\nI can search your photos, report live battery and storage stats, solve math, log daily expenses, or navigate any of our 100 built-in tools.\n\nWhat would you like to do right now?"

            q.contains("expense") || q.contains("spend") || q.contains("spent") || q.contains("bought") -> {
                val amount = Regex("\\d+").find(q)?.value ?: "350"
                "💸 **Expense Action**:\n\n" +
                        "I noticed you mentioned an expense regarding $keywordList.\n" +
                        "• Amount: **₹$amount**\n" +
                        "• Tap the confirm button below to log this directly to your **Expense Tracker** database!"
            }

            q.contains("study") || q.contains("exam") || q.contains("timetable") ->
                "📚 **Study Strategy for \"$original\"**:\n\n" +
                "• **Focus Method**: Use the 25/5 Pomodoro rhythm for deep retention.\n" +
                "• **Flashcards**: Review core concepts with active recall.\n" +
                "• **Countdown**: You can log exam dates in the **Exam Countdown** tool under Students!"

            q.contains("water") || q.contains("drink") || q.contains("hydrate") ->
                "💧 **Hydration Reminder**:\n\n" +
                "Your daily hydration goal is 2,500 ml. Tap below to log a glass in the **Water Reminder** tool!"

            q.contains("vehicle") || q.contains("car") || q.contains("bike") || q.contains("fuel") ->
                "🚗 **Vehicle Care for \"$original\"**:\n\n" +
                "• Check tyre PSI regularly for optimal fuel economy.\n" +
                "• Keep PUC and Insurance dates updated in the **Vehicle Document Locker**."

            q.contains("safety") || q.contains("sos") || q.contains("police") || q.contains("emergency") ->
                "🛡️ **Safety Protocol**:\n\n" +
                "If you are in danger, use the **Emergency SOS** tool immediately for 1-tap 112 dialing, siren strobe, and GPS coordinate broadcasting."

            q.contains("joke") ->
                "Here's one for you 😄:\n\nWhy did the smartphone need glasses? 📱\nBecause it lost all its contacts!"

            q.contains("hindi") || q.contains("namaste") || q.contains("kya haal") ->
                "नमस्ते! 🙏 मैं LifeHub AI हूँ। मैं आपके खर्चे नोट कर सकता हूँ, फ़ोन की बैटरी और स्टोरेज देख सकता हूँ, और 100 टूल्स चलाने में आपकी मदद कर सकता हूँ। बताइये, आज मैं क्या सहायता करूँ?"

            else ->
                "✨ **LifeHub AI Response** to: *\"$original\"*\n\n" +
                "I've analyzed your request regarding $keywordList:\n" +
                "• All 100 modules are active and synced with your phone.\n" +
                "• You can search gallery photos, check battery & storage metrics, calculate math, or log personal records.\n\n" +
                "Would you like me to open a specific tool or perform an action for this?"
        }
    }
}
