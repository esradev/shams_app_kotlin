package ir.wpstorm.shams.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LessonDao {

    // 🔹 Insert or replace multiple lessons
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<LessonEntity>)

    // 🔹 Insert or replace a single lesson
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: LessonEntity)

    // 🔹 Get lessons by category
    @Query("SELECT * FROM lessons WHERE category_id = :categoryId ORDER BY id ASC")
    suspend fun getLessonsByCategory(categoryId: Int): List<LessonEntity>

    // 🔹 Get a single lesson by ID
    @Query("SELECT * FROM lessons WHERE id = :lessonId LIMIT 1")
    suspend fun getLessonById(lessonId: Int): LessonEntity?

    // 🔹 Search lessons by title (offline)
    @Query("SELECT * FROM lessons WHERE title LIKE '%' || :query || '%' ORDER BY id ASC")
    suspend fun searchLessons(query: String): List<LessonEntity>
}
