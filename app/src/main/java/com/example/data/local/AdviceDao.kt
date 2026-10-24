package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DailyAdviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AdviceDao {

    @Query("SELECT * FROM daily_advice ORDER BY publicationDate DESC, createdAt ASC")
    fun getAllAdviceFlow(): Flow<List<DailyAdviceEntity>>

    @Query("SELECT * FROM daily_advice WHERE publicationDate = :date ORDER BY createdAt ASC")
    fun getAdviceForDateFlow(date: String): Flow<List<DailyAdviceEntity>>

    @Query("SELECT * FROM daily_advice WHERE publicationDate = :date ORDER BY createdAt ASC")
    suspend fun getAdviceForDate(date: String): List<DailyAdviceEntity>

    @Query("SELECT * FROM daily_advice WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoritesFlow(): Flow<List<DailyAdviceEntity>>

    @Query("SELECT * FROM daily_advice WHERE categoryKey = :categoryKey ORDER BY publicationDate DESC")
    fun getAdviceByCategoryFlow(categoryKey: String): Flow<List<DailyAdviceEntity>>

    @Query("SELECT * FROM daily_advice WHERE id = :id LIMIT 1")
    suspend fun getAdviceById(id: String): DailyAdviceEntity?

    @Query("""
        SELECT * FROM daily_advice 
        WHERE title LIKE '%' || :query || '%' 
           OR advice LIKE '%' || :query || '%' 
           OR dailyAction LIKE '%' || :query || '%'
           OR category LIKE '%' || :query || '%'
        ORDER BY publicationDate DESC
    """)
    fun searchAdviceFlow(query: String): Flow<List<DailyAdviceEntity>>

    @Query("SELECT COUNT(*) FROM daily_advice WHERE publicationDate = :date")
    suspend fun countForDate(date: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(adviceList: List<DailyAdviceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(advice: DailyAdviceEntity)

    @Update
    suspend fun update(advice: DailyAdviceEntity)

    @Query("UPDATE daily_advice SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)

    @Query("UPDATE daily_advice SET isActionCompleted = :isCompleted WHERE id = :id")
    suspend fun updateActionCompleted(id: String, isCompleted: Boolean)

    @Delete
    suspend fun delete(advice: DailyAdviceEntity)

    @Query("DELETE FROM daily_advice WHERE publicationDate = :date")
    suspend fun deleteForDate(date: String)
}
