package com.example.gemini

import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.data.StanceAssessment
import com.example.data.StanceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiLiveTranslateService {

    companion object {
        private const val TAG = "GeminiTranslateService"
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/"
        // Supported model from gemini-api skill for real-time translation & text tasks
        private const val MODEL_PRIMARY = "gemini-3.5-flash"
        private const val MODEL_LIVE_AUDIO = "gemini-2.5-flash-native-audio-preview-12-2025"
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Translates English Fed Speech / audio transcript directly to financial Hindi ("hi")
     * with Hawkish/Dovish stance classification and market impact insight.
     */
    suspend fun translateFedSpeechToHindi(
        englishSpeechChunk: String
    ): Result<LiveTranslationResponse> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                // Fallback for demo when key isn't configured in secrets panel
                return@withContext Result.success(
                    LiveTranslationResponse(
                        hindiTranslation = "फेड अध्यक्ष ने कहा: $englishSpeechChunk",
                        stance = StanceType.DOVISH,
                        stanceScore = 75,
                        keyTakeaway = "ब्याज दरों में कटौती और मौद्रिक ढील की दिशा में स्पष्ट संकेत।",
                        rawAudioBytes = null
                    )
                )
            }

            val systemInstruction = """
                You are an elite financial translator & market strategist specialized in real-time Live Federal Reserve / FOMC speech translation from English to Hindi (targetLanguageCode: 'hi').
                Task:
                1. Translate the English financial speech text accurately, fluently, and naturally into Hindi (Devanagari script).
                2. Assess the monetary policy stance: HAWKISH (tightening/rate hikes), DOVISH (easing/rate cuts), or NEUTRAL.
                3. Score the stance from 0 (Extremely Hawkish) to 100 (Extremely Dovish), with 50 as Neutral.
                4. Provide 1 concise bullet point in Hindi summarizing the core takeaway for Indian and global investors.
                
                Respond ONLY with a valid JSON object in this format:
                {
                    "hindiTranslation": "...",
                    "stance": "DOVISH" | "HAWKISH" | "NEUTRAL",
                    "stanceScore": 75,
                    "keyTakeawayHi": "..."
                }
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().put("text", "Translate and analyze this Fed speech chunk: \"$englishSpeechChunk\""))
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", systemInstruction))
                    })
                })

                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.2)
                    put("responseMimeType", "application/json")
                })
            }

            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            val url = "${BASE_URL}${MODEL_PRIMARY}:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "Gemini API error ${response.code}: $responseBody")
                return@withContext Result.failure(Exception("API returned ${response.code}"))
            }

            val rootJson = JSONObject(responseBody)
            val candidates = rootJson.optJSONArray("candidates")
            val candidate = candidates?.optJSONObject(0)
            val content = candidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val textPart = parts?.optJSONObject(0)?.optString("text") ?: ""

            val parsedOutput = JSONObject(textPart)
            val hindiText = parsedOutput.optString("hindiTranslation", "")
            val stanceStr = parsedOutput.optString("stance", "NEUTRAL")
            val stanceScore = parsedOutput.optInt("stanceScore", 50)
            val takeaway = parsedOutput.optString("keyTakeawayHi", "")

            val stance = when (stanceStr.uppercase()) {
                "HAWKISH" -> StanceType.HAWKISH
                "DOVISH" -> StanceType.DOVISH
                else -> StanceType.NEUTRAL
            }

            Result.success(
                LiveTranslationResponse(
                    hindiTranslation = hindiText,
                    stance = stance,
                    stanceScore = stanceScore,
                    keyTakeaway = takeaway,
                    rawAudioBytes = null
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Translation error: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Answers user voice or text queries about Fed policy & market prices in fluent Hindi.
     */
    suspend fun answerMarketVoiceQuery(
        userQuery: String,
        currentMarketContext: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext Result.success(
                    "फेडरल रिजर्व की ब्याज दर नीति से बाजार में लिक्विडिटी बढ़ती है। जब भी दरें घटती हैं, बिटकॉइन और गोल्ड में तेजी आने की संभावना बढ़ जाती है।"
                )
            }

            val prompt = """
                Context: The user is listening to a live Federal Reserve speech stream with live market tickers.
                Live Market Context: $currentMarketContext
                User's question: "$userQuery"
                
                Respond in fluent, clear, and easy-to-understand Hindi (Devanagari script).
                Keep the answer concise (2-3 sentences), explaining the exact impact on Bitcoin, Gold, US100 Stocks, or Inflation.
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.4)
                })
            }

            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            val url = "${BASE_URL}${MODEL_PRIMARY}:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Gemini error ${response.code}"))
            }

            val rootJson = JSONObject(responseBody)
            val candidates = rootJson.optJSONArray("candidates")
            val candidate = candidates?.optJSONObject(0)
            val text = candidate?.optJSONObject("content")?.optJSONArray("parts")?.optJSONObject(0)?.optString("text") ?: ""

            Result.success(text.trim())
        } catch (e: Exception) {
            Log.e(TAG, "Query error: ${e.message}", e)
            Result.failure(e)
        }
    }
}

data class LiveTranslationResponse(
    val hindiTranslation: String,
    val stance: StanceType,
    val stanceScore: Int,
    val keyTakeaway: String,
    val rawAudioBytes: ByteArray? = null
)
