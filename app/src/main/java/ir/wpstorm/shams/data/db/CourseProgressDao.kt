package ir.wpstorm.shams.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseProgressDao {
    @Query("SELECT * FROM course_progress ORDER BY last_accessed_at DESC")
    fun getAllCourseProgress(): Flow<List<CourseProgressEntity>>

    @Query("SELECT * FROM course_progress WHERE category_id = :categoryId")
    suspend fun getCourseProgress(categoryId: Int): CourseProgressEntity?

    @Query("SELECT * FROM course_progress WHERE category_id = :categoryId")
    fun getCourseProgressFlow(categoryId: Int): Flow<CourseProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourseProgress(courseProgress: CourseProgressEntity)

    @Update
    suspend fun updateCourseProgress(courseProgress: CourseProgressEntity)

    @Query("UPDATE course_progress SET last_lesson_id = :lastLessonId, last_accessed_at = :timestamp WHERE category_id = :categoryId")
    suspend fun updateLastLesson(categoryId: Int, lastLessonId: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE course_progress SET completed_lessons = :completedCount WHERE category_id = :categoryId")
    suspend fun updateCompletedLessonCount(categoryId: Int, completedCount: Int)

    @Query("DELETE FROM course_progress WHERE category_id = :categoryId")
    suspend fun deleteCourseProgress(categoryId: Int)

    @Query("DELETE FROM course_progress")
    suspend fun clearAllProgress()
}
