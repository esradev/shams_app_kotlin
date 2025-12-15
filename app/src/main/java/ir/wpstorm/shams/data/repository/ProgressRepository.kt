package ir.wpstorm.shams.data.repository

import android.util.Log
import ir.wpstorm.shams.data.db.CompletedLessonDao
import ir.wpstorm.shams.data.db.CompletedLessonEntity
import ir.wpstorm.shams.data.db.CourseProgressDao
import ir.wpstorm.shams.data.db.CourseProgressEntity
import ir.wpstorm.shams.data.db.LessonDao
import kotlinx.coroutines.flow.Flow

class ProgressRepository(
    private val completedLessonDao: CompletedLessonDao,
    private val courseProgressDao: CourseProgressDao,
    private val lessonDao: LessonDao
) {

    // ========== Completed Lessons ==========

    fun getAllCompletedLessons(): Flow<List<CompletedLessonEntity>> {
        return completedLessonDao.getAllCompletedLessons()
    }

    fun getCompletedLessonsByCategory(categoryId: Int): Flow<List<CompletedLessonEntity>> {
        return completedLessonDao.getCompletedLessonsByCategory(categoryId)
    }

    suspend fun isLessonCompleted(lessonId: Int): Boolean {
        return completedLessonDao.isLessonCompleted(lessonId)
    }

    fun isLessonCompletedFlow(lessonId: Int): Flow<Boolean> {
        return completedLessonDao.isLessonCompletedFlow(lessonId)
    }

    suspend fun markLessonAsCompleted(lessonId: Int, categoryId: Int) {
        try {
            Log.d("ProgressRepository", "Marking lesson $lessonId as completed")

            // Insert completed lesson
            val completedLesson = CompletedLessonEntity(
                lessonId = lessonId,
                categoryId = categoryId,
                completedAt = System.currentTimeMillis()
            )
            completedLessonDao.insertCompletedLesson(completedLesson)

            // Update course progress
            updateCourseProgressAfterCompletion(categoryId, lessonId)

            Log.d("ProgressRepository", "Lesson $lessonId marked as completed")
        } catch (e: Exception) {
            Log.e("ProgressRepository", "Failed to mark lesson as completed: ${e.message}", e)
            throw e
        }
    }

    suspend fun unmarkLessonAsCompleted(lessonId: Int, categoryId: Int) {
        try {
            Log.d("ProgressRepository", "Unmarking lesson $lessonId as completed")

            completedLessonDao.deleteCompletedLesson(lessonId)

            // Update course progress
            updateCourseProgressAfterCompletion(categoryId, lessonId)

            Log.d("ProgressRepository", "Lesson $lessonId unmarked as completed")
        } catch (e: Exception) {
            Log.e("ProgressRepository", "Failed to unmark lesson: ${e.message}", e)
            throw e
        }
    }

    suspend fun getCompletedLessonCount(categoryId: Int): Int {
        return completedLessonDao.getCompletedLessonCount(categoryId)
    }

    // ========== Course Progress ==========

    fun getAllCourseProgress(): Flow<List<CourseProgressEntity>> {
        return courseProgressDao.getAllCourseProgress()
    }

    suspend fun getCourseProgress(categoryId: Int): CourseProgressEntity? {
        return courseProgressDao.getCourseProgress(categoryId)
    }

    fun getCourseProgressFlow(categoryId: Int): Flow<CourseProgressEntity?> {
        return courseProgressDao.getCourseProgressFlow(categoryId)
    }

    suspend fun updateLastAccessedLesson(categoryId: Int, lessonId: Int) {
        try {
            Log.d("ProgressRepository", "Updating last accessed lesson for category $categoryId")

            val existingProgress = courseProgressDao.getCourseProgress(categoryId)
            val totalLessons = lessonDao.getLessonCountByCategory(categoryId)
            val completedCount = completedLessonDao.getCompletedLessonCount(categoryId)

            if (existingProgress != null) {
                // Update existing progress
                courseProgressDao.updateLastLesson(
                    categoryId = categoryId,
                    lastLessonId = lessonId,
                    timestamp = System.currentTimeMillis()
                )
                courseProgressDao.updateCompletedLessonCount(categoryId, completedCount)
            } else {
                // Create new progress entry
                val newProgress = CourseProgressEntity(
                    categoryId = categoryId,
                    lastLessonId = lessonId,
                    totalLessons = totalLessons,
                    completedLessons = completedCount,
                    lastAccessedAt = System.currentTimeMillis(),
                    startedAt = System.currentTimeMillis()
                )
                courseProgressDao.insertCourseProgress(newProgress)
            }

            Log.d("ProgressRepository", "Last accessed lesson updated successfully")
        } catch (e: Exception) {
            Log.e("ProgressRepository", "Failed to update last accessed lesson: ${e.message}", e)
            throw e
        }
    }

    private suspend fun updateCourseProgressAfterCompletion(categoryId: Int, lessonId: Int) {
        val totalLessons = lessonDao.getLessonCountByCategory(categoryId)
        val completedCount = completedLessonDao.getCompletedLessonCount(categoryId)

        val existingProgress = courseProgressDao.getCourseProgress(categoryId)
        if (existingProgress != null) {
            val updatedProgress = existingProgress.copy(
                totalLessons = totalLessons,
                completedLessons = completedCount,
                lastAccessedAt = System.currentTimeMillis()
            )
            courseProgressDao.updateCourseProgress(updatedProgress)
        } else {
            val newProgress = CourseProgressEntity(
                categoryId = categoryId,
                lastLessonId = lessonId,
                totalLessons = totalLessons,
                completedLessons = completedCount,
                lastAccessedAt = System.currentTimeMillis(),
                startedAt = System.currentTimeMillis()
            )
            courseProgressDao.insertCourseProgress(newProgress)
        }
    }

    suspend fun clearAllProgress() {
        completedLessonDao.clearAllCompletedLessons()
        courseProgressDao.clearAllProgress()
    }

    suspend fun getFirstUncompletedLesson(categoryId: Int): Int? {
        val firstUncompleted = lessonDao.getFirstUncompletedLesson(categoryId)
        return firstUncompleted?.id
    }
}
