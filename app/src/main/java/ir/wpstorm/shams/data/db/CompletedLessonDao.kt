package ir.wpstorm.shams.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CompletedLessonDao {
    @Query("SELECT * FROM completed_lessons ORDER BY completed_at DESC")
    fun getAllCompletedLessons(): Flow<List<CompletedLessonEntity>>

    @Query("SELECT * FROM completed_lessons WHERE category_id = :categoryId ORDER BY completed_at DESC")
    fun getCompletedLessonsByCategory(categoryId: Int): Flow<List<CompletedLessonEntity>>

    @Query("SELECT * FROM completed_lessons WHERE lesson_id = :lessonId LIMIT 1")
    suspend fun getCompletedLesson(lessonId: Int): CompletedLessonEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM completed_lessons WHERE lesson_id = :lessonId)")
    suspend fun isLessonCompleted(lessonId: Int): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM completed_lessons WHERE lesson_id = :lessonId)")
    fun isLessonCompletedFlow(lessonId: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedLesson(completedLesson: CompletedLessonEntity)

    @Query("DELETE FROM completed_lessons WHERE lesson_id = :lessonId")
    suspend fun deleteCompletedLesson(lessonId: Int)

    @Query("SELECT COUNT(*) FROM completed_lessons WHERE category_id = :categoryId")
    suspend fun getCompletedLessonCount(categoryId: Int): Int

    @Query("DELETE FROM completed_lessons")
    suspend fun clearAllCompletedLessons()
}
