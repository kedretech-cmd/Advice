package com.example.data.remote

import com.example.BuildConfig
import com.example.data.model.AdviceCategory
import com.example.data.model.DailyAdviceEntity
import com.example.util.EthiopianDateHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {

    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun isApiKeyConfigured(): Boolean {
        val key = BuildConfig.GEMINI_API_KEY
        return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
    }

    /**
     * Generates 10 daily advice posts in Amharic using Gemini 3.5 Flash.
     */
    suspend fun generateDailyAdvice(customPromptTopic: String? = null): Result<List<DailyAdviceEntity>> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (!isApiKeyConfigured()) {
            return@withContext Result.failure(
                IllegalStateException("የ Kedre AI API ቁልፍ (GEMINI_API_KEY) አልተዘጋጀም። እባክዎ በSecrets panel ውስጥ ያስገቡ።")
            )
        }

        val promptBuilder = StringBuilder()
        promptBuilder.append("""
            You are an expert Ethiopian motivational writer, life coach, educator, and professional Amharic language writer.
            Generate exactly 10 unique, meaningful, practical, and inspiring daily advice posts in natural Amharic.
            Audience: Ethiopian people, students, professionals, entrepreneurs, young people, and anyone interested in personal growth.

            Requirements:
            - Write everything in Amharic (አማርኛ).
            - Use correct Amharic grammar and natural, culturally respectful expressions.
            - Provide exactly 10 different advice topics, covering these 10 distinct categories:
              1. Motivation (ተነሳሽነት)
              2. Education & Learning (ትምህርትና እውቀት)
              3. Business & Entrepreneurship (ንግድና ሥራ ፈጠራ)
              4. Technology & Digital Skills (ቴክኖሎጂና ዲጂታል)
              5. Personal Development (የግል ዕድገት)
              6. Relationships & Communication (ማህበራዊ ግንኙነት)
              7. Life Wisdom & Positive Thinking (የሕይወት ጥበብ)
              8. Time Management & Productivity (የጊዜ አጠቃቀም)
              9. Healthy Habits & Wellness (ጤናማ ልማዶች)
              10. Financial Responsibility (የፋይናንስ አስተዳደር)
            - Each advice must have a short, engaging title in Amharic (under 6 words).
            - Each advice body must contain 50–100 Amharic words of practical, empowering wisdom.
            - Explain the practical lesson in 1-2 Amharic sentences.
            - Include one specific action the reader can take today in Amharic ('daily_action').
            - Make every advice unique. Avoid repetitive motivational quotes.
            - Keep content positive, respectful, educational, and culturally appropriate.
        """.trimIndent())

        if (!customPromptTopic.isNullOrBlank()) {
            promptBuilder.append("\nSpecial Focus for this generation: $customPromptTopic\n")
        }

        promptBuilder.append("""

            Return valid JSON with an array of exactly 10 objects:
            [
              {
                "title": "የአማርኛ አርዕስት",
                "advice": "ሙሉ የአማርኛ ምክር (ከ50 እስከ 100 ቃላት)",
                "explanation": "አጭር የአማርኛ ማብራሪያ",
                "daily_action": "ዛሬ የሚፈጸም ተግባራዊ እርምጃ",
                "category": "የዘርፉ ስም (ለምሳሌ: ተነሳሽነት፣ ትምህርትና እውቀት፣ ንግድና ሥራ ፈጠራ፣ ቴክኖሎጂና ዲጂታል፣ የግል ዕድገት፣ ማህበራዊ ግንኙነት፣ የሕይወት ጥበብ፣ የጊዜ አጠቃቀም፣ ጤናማ ልማዶች፣ የፋይናንስ አስተዳደር)",
                "image_prompt": "English prompt describing an artistic inspiring visual for this advice"
              }
            ]
            Do not include Markdown blocks. Return raw JSON array only.
        """.trimIndent())

        try {
            val rootJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    put(JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", promptBuilder.toString())
                            })
                        }
                        put("parts", partsArray)
                    })
                }
                put("contents", contentsArray)

                val genConfig = JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.7)
                }
                put("generationConfig", genConfig)
            }

            val requestBody = rootJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                val errBody = response.body?.string().orEmpty()
                return@withContext Result.failure(Exception("Kedre AI API ጥሪ አልተሳካም (ኮድ ${response.code}): $errBody"))
            }

            val responseBodyString = response.body?.string().orEmpty()
            val parsedAdviceList = parseAdviceFromJsonResponse(responseBodyString)
            if (parsedAdviceList.isEmpty()) {
                return@withContext Result.failure(Exception("ምክሮቹን ከAI ምላሽ ማግኘት አልተቻለም።"))
            }

            Result.success(parsedAdviceList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseAdviceFromJsonResponse(jsonResponse: String): List<DailyAdviceEntity> {
        val todayIso = EthiopianDateHelper.getTodayIsoDate()
        val ethDate = EthiopianDateHelper.getTodayEthiopianString()
        val result = mutableListOf<DailyAdviceEntity>()

        try {
            val root = JSONObject(jsonResponse)
            val candidates = root.optJSONArray("candidates") ?: return emptyList()
            if (candidates.length() == 0) return emptyList()

            val candidate = candidates.getJSONObject(0)
            val content = candidate.optJSONObject("content") ?: return emptyList()
            val parts = content.optJSONArray("parts") ?: return emptyList()
            if (parts.length() == 0) return emptyList()

            var text = parts.getJSONObject(0).optString("text", "")
            // Clean markdown code blocks if any
            if (text.startsWith("```json")) {
                text = text.removePrefix("```json").trim()
            }
            if (text.startsWith("```")) {
                text = text.removePrefix("```").trim()
            }
            if (text.endsWith("```")) {
                text = text.removeSuffix("```").trim()
            }

            val jsonArray = if (text.trim().startsWith("[")) {
                JSONArray(text)
            } else if (text.trim().startsWith("{")) {
                val obj = JSONObject(text)
                obj.optJSONArray("advice") ?: obj.optJSONArray("posts") ?: JSONArray()
            } else {
                JSONArray()
            }

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val title = item.optString("title", "የዕለት ምክር #${i + 1}")
                val advice = item.optString("advice", "")
                val explanation = item.optString("explanation", "")
                val dailyAction = item.optString("daily_action", item.optString("dailyAction", ""))
                val categoryName = item.optString("category", "ተነሳሽነት")
                val imagePrompt = item.optString("image_prompt", item.optString("imagePrompt", ""))

                val matchedCat = AdviceCategory.entries.firstOrNull {
                    it.amharicName.equals(categoryName, ignoreCase = true) ||
                    it.key.equals(categoryName, ignoreCase = true) ||
                    categoryName.contains(it.amharicName)
                } ?: AdviceCategory.entries[i % AdviceCategory.entries.size]

                val uniqueId = "${todayIso}_${System.currentTimeMillis()}_$i"

                result.add(
                    DailyAdviceEntity(
                        id = uniqueId,
                        title = title,
                        advice = advice,
                        explanation = explanation,
                        dailyAction = dailyAction,
                        category = matchedCat.amharicName,
                        categoryKey = matchedCat.key,
                        imagePrompt = imagePrompt,
                        imageUrl = null,
                        publicationDate = todayIso,
                        ethiopianDate = ethDate,
                        createdAt = System.currentTimeMillis() + i
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }
}
