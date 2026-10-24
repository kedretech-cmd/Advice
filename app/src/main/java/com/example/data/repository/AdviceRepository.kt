package com.example.data.repository

import com.example.data.local.AdviceDao
import com.example.data.local.InitialSeedData
import com.example.data.model.DailyAdviceEntity
import com.example.data.remote.GeminiClient
import com.example.util.EthiopianDateHelper
import kotlinx.coroutines.flow.Flow

class AdviceRepository(private val dao: AdviceDao) {

    fun getAllAdvice(): Flow<List<DailyAdviceEntity>> = dao.getAllAdviceFlow()

    fun getTodayAdvice(): Flow<List<DailyAdviceEntity>> {
        val today = EthiopianDateHelper.getTodayIsoDate()
        return dao.getAdviceForDateFlow(today)
    }

    fun getFavorites(): Flow<List<DailyAdviceEntity>> = dao.getFavoritesFlow()

    fun getAdviceByCategory(categoryKey: String): Flow<List<DailyAdviceEntity>> =
        dao.getAdviceByCategoryFlow(categoryKey)

    fun searchAdvice(query: String): Flow<List<DailyAdviceEntity>> =
        dao.searchAdviceFlow(query)

    suspend fun getAdviceById(id: String): DailyAdviceEntity? = dao.getAdviceById(id)

    /**
     * Ensures today has 10 pieces of advice in the database.
     * If empty:
     * - If Gemini API key is configured, calls Gemini to generate.
     * - If Gemini API call fails or key not configured, seeds curated Day-1 posts.
     */
    suspend fun ensureTodayAdviceAvailable(): Result<List<DailyAdviceEntity>> {
        val today = EthiopianDateHelper.getTodayIsoDate()
        val existingCount = dao.countForDate(today)

        if (existingCount >= 10) {
            val existing = dao.getAdviceForDate(today)
            return Result.success(existing)
        }

        // Try Gemini if key is ready
        if (GeminiClient.isApiKeyConfigured()) {
            val apiResult = GeminiClient.generateDailyAdvice()
            if (apiResult.isSuccess) {
                val list = apiResult.getOrThrow()
                dao.insertAll(list)
                return Result.success(list)
            }
        }

        // Fallback / Initial Seed: provide 10 meaningful daily advice posts
        val seeds = InitialSeedData.getInitialTenAdvice(today)
        dao.insertAll(seeds)
        return Result.success(seeds)
    }

    /**
     * Force generates new advice for today using Gemini API, replacing or appending today's batch.
     */
    suspend fun forceGenerateTodayAdvice(topic: String? = null): Result<List<DailyAdviceEntity>> {
        val apiResult = GeminiClient.generateDailyAdvice(topic)
        return if (apiResult.isSuccess) {
            val list = apiResult.getOrThrow()
            val today = EthiopianDateHelper.getTodayIsoDate()
            dao.deleteForDate(today)
            dao.insertAll(list)
            Result.success(list)
        } else {
            apiResult
        }
    }

    suspend fun toggleFavorite(id: String, currentStatus: Boolean) {
        dao.updateFavorite(id, !currentStatus)
    }

    suspend fun toggleActionCompleted(id: String, currentStatus: Boolean) {
        dao.updateActionCompleted(id, !currentStatus)
    }

    suspend fun updateAdvice(advice: DailyAdviceEntity) {
        dao.update(advice)
    }

    suspend fun deleteAdvice(advice: DailyAdviceEntity) {
        dao.delete(advice)
    }

    suspend fun insertAdvice(advice: DailyAdviceEntity) {
        dao.insert(advice)
    }
}
