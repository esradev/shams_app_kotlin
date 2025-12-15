package ir.wpstorm.shams.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons ORDER BY id ASC")
    fun getAllLessons(): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE category_id = :categoryId ORDER BY id ASC")
    fun getLessonsByCategory(categoryId: Int): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE category_id = :categoryId ORDER BY id ASC")
    suspend fun getLessonsByCategorySync(categoryId: Int): List<LessonEntity>

    @Query("SELECT * FROM lessons WHERE category_id = :categoryId ORDER BY created_at DESC")
    suspend fun getLessonsByCategoryByDateDesc(categoryId: Int): List<LessonEntity>

    @Query("SELECT * FROM lessons WHERE category_id = :categoryId ORDER BY created_at ASC")
    suspend fun getLessonsByCategoryByDateAsc(categoryId: Int): List<LessonEntity>

    @Query("SELECT * FROM lessons WHERE id = :id")
    suspend fun getLessonById(id: Int): LessonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: LessonEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<LessonEntity>)

    @Update
    suspend fun updateLesson(lesson: LessonEntity)

    @Query("UPDATE lessons SET is_favorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean)

    @Query("UPDATE lessons SET play_count = play_count + 1 WHERE id = :id")
    suspend fun incrementPlayCount(id: Int)

    @Query("SELECT * FROM lessons WHERE is_favorite = 1 ORDER BY updated_at DESC")
    fun getFavoriteLessons(): Flow<List<LessonEntity>>

    @Query("DELETE FROM lessons WHERE category_id = :categoryId")
    suspend fun deleteLessonsByCategory(categoryId: Int)

    @Query("DELETE FROM lessons")
    suspend fun clearLessons()

    @Query("SELECT COUNT(*) FROM lessons WHERE category_id = :categoryId")
    suspend fun getLessonCountByCategory(categoryId: Int): Int

    @Query("SELECT * FROM lessons WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY updated_at DESC")
    suspend fun searchLessons(query: String): List<LessonEntity>

    @Query("SELECT * FROM lessons WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY updated_at DESC")
    fun searchLessonsFlow(query: String): Flow<List<LessonEntity>>
}
