package com.example.data.remote

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

enum class VoiceEngine(val displayName: String) {
    ELEVEN_LABS_RACHEL("ElevenLabs AI (Rachel - Warm)"),
    ELEVEN_LABS_ADAM("ElevenLabs AI (Adam - Confident)"),
    ANDROID_TTS_NATURAL("Android Natural TTS"),
    ANDROID_TTS_HINDI("Android Hindi TTS")
}

class VoiceService(private val context: Context) {

    private val TAG = "VoiceService"
    private var textToSpeech: TextToSpeech? = null
    private var isTtsInitialized = false
    private var mediaPlayer: MediaPlayer? = null

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    var isSpeakingCallback: ((Boolean) -> Unit)? = null

    init {
        try {
            textToSpeech = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    isTtsInitialized = true
                    textToSpeech?.language = Locale.ENGLISH
                    textToSpeech?.setSpeechRate(1.0f)
                    textToSpeech?.setPitch(1.0f)
                    textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            isSpeakingCallback?.invoke(true)
                        }

                        override fun onDone(utteranceId: String?) {
                            isSpeakingCallback?.invoke(false)
                        }

                        override fun onError(utteranceId: String?) {
                            isSpeakingCallback?.invoke(false)
                        }
                    })
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Android TTS: ${e.message}")
        }
    }

    suspend fun speak(
        text: String,
        selectedVoice: VoiceEngine = VoiceEngine.ELEVEN_LABS_RACHEL
    ) = withContext(Dispatchers.IO) {
        stop()

        val cleanText = text
            .replace(Regex("\\*\\*"), "")
            .replace(Regex("[•#_`>]"), "")
            .trim()

        if (cleanText.isBlank()) return@withContext

        // Check if ElevenLabs voice is selected and key is present
        val elevenLabsApiKey = try {
            val key = BuildConfig.ELEVENLABS_API_KEY
            if (key == "MY_ELEVENLABS_API_KEY") "" else key
        } catch (e: Throwable) {
            try {
                val key = BuildConfig::class.java.getField("ELEVENLABS_API_KEY").get(null) as? String ?: ""
                if (key == "MY_ELEVENLABS_API_KEY") "" else key
            } catch (e2: Throwable) {
                ""
            }
        }

        val isElevenLabsVoice = selectedVoice == VoiceEngine.ELEVEN_LABS_RACHEL || selectedVoice == VoiceEngine.ELEVEN_LABS_ADAM

        if (isElevenLabsVoice && elevenLabsApiKey.isNotBlank()) {
            val success = synthesizeAndPlayElevenLabs(cleanText, elevenLabsApiKey, selectedVoice)
            if (success) return@withContext
        }

        // Fallback or Native TTS execution
        withContext(Dispatchers.Main) {
            speakWithAndroidTts(cleanText, selectedVoice)
        }
    }

    private suspend fun synthesizeAndPlayElevenLabs(
        text: String,
        apiKey: String,
        voice: VoiceEngine
    ): Boolean = withContext(Dispatchers.IO) {
        val voiceId = when (voice) {
            VoiceEngine.ELEVEN_LABS_ADAM -> "pNInz6obpgDQGcFmaJgB"
            else -> "21m00Tcm4TlvDq8ikWAM" // Rachel
        }

        try {
            val url = "https://api.elevenlabs.io/v1/text-to-speech/$voiceId"

            val json = JSONObject().apply {
                put("text", text.take(1000))
                put("model_id", "eleven_monolingual_v1")
                val voiceSettings = JSONObject().apply {
                    put("stability", 0.5)
                    put("similarity_boost", 0.75)
                }
                put("voice_settings", voiceSettings)
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("xi-api-key", apiKey)
                .addHeader("Content-Type", "application/json")
                .post(json.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val bytes = response.body?.bytes()
                if (bytes != null && bytes.isNotEmpty()) {
                    val tempAudioFile = File(context.cacheDir, "elevenlabs_ai_voice_${System.currentTimeMillis()}.mp3")
                    FileOutputStream(tempAudioFile).use { it.write(bytes) }

                    withContext(Dispatchers.Main) {
                        playAudioFile(tempAudioFile)
                    }
                    return@withContext true
                }
            } else {
                Log.w(TAG, "ElevenLabs HTTP ${response.code}: falling back to native TTS")
            }
        } catch (e: Exception) {
            Log.e(TAG, "ElevenLabs synthesis failed: ${e.message}", e)
        }
        return@withContext false
    }

    private fun playAudioFile(file: File) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                setOnPreparedListener {
                    start()
                    isSpeakingCallback?.invoke(true)
                }
                setOnCompletionListener {
                    isSpeakingCallback?.invoke(false)
                    file.delete()
                }
                setOnErrorListener { _, _, _ ->
                    isSpeakingCallback?.invoke(false)
                    file.delete()
                    false
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing audio file: ${e.message}")
            isSpeakingCallback?.invoke(false)
        }
    }

    private fun speakWithAndroidTts(text: String, voice: VoiceEngine) {
        if (!isTtsInitialized || textToSpeech == null) return

        try {
            if (voice == VoiceEngine.ANDROID_TTS_HINDI) {
                textToSpeech?.language = Locale.forLanguageTag("hi-IN")
            } else {
                textToSpeech?.language = Locale.ENGLISH
            }
            val utteranceId = UUID.randomUUID().toString()
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        } catch (e: Exception) {
            Log.e(TAG, "Error with speakWithAndroidTts: ${e.message}")
        }
    }

    fun stop() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
            }
            textToSpeech?.stop()
            isSpeakingCallback?.invoke(false)
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping audio: ${e.message}")
        }
    }

    fun release() {
        stop()
        textToSpeech?.shutdown()
        textToSpeech = null
    }
}
