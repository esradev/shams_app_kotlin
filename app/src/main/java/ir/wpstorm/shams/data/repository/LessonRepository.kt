package ir.wpstorm.shams.data.repository

import android.util.Log
import ir.wpstorm.shams.data.api.ApiClient
import ir.wpstorm.shams.data.db.LessonDao
import ir.wpstorm.shams.data.db.LessonEntity

class LessonRepository(private val lessonDao: LessonDao) {

    private val api = ApiClient.wordpressApi

    // 🔹 Fetch lessons by category
    suspend fun getLessonsByCategory(categoryId: Int): List<LessonEntity> {
        return try {
            Log.d("LessonRepository", "Fetching lessons for category: $categoryId")
            // 1️⃣ Try API first
            val apiLessons = api.getPostsByCategory(categoryId)
            Log.d("LessonRepository", "API returned ${apiLessons.size} lessons")

            val entities = apiLessons.map { dto ->
                LessonEntity(
                    id = dto.id,
                    title = dto.title.rendered,
                    content = dto.content.rendered,
                    audioUrl = dto.meta?.`the-audio-of-the-lesson`,
                    categoryId = categoryId
                )
            }

            // 2️⃣ Save to local DB
            lessonDao.insertLessons(entities)
            Log.d("LessonRepository", "Saved ${entities.size} lessons to database")

            entities
        } catch (e: Exception) {
            Log.e("LessonRepository", "API call failed: ${e.message}", e)
            // 3️⃣ On error, return cached lessons
            val cachedLessons = lessonDao.getLessonsByCategory(categoryId)
            Log.d("LessonRepository", "Returning ${cachedLessons.size} cached lessons")
            cachedLessons
        }
    }

    // 🔹 Fetch single lesson by ID
    suspend fun getLessonById(lessonId: Int): LessonEntity? {
        return try {
            Log.d("LessonRepository", "Fetching lesson by ID: $lessonId")
            val dto = api.getPostById(lessonId)
            val entity = LessonEntity(
                id = dto.id,
                title = dto.title.rendered,
                content = dto.content.rendered,
                audioUrl = dto.meta?.`the-audio-of-the-lesson`,
                categoryId = dto.categories?.firstOrNull()
            )

            // Save / update in DB
            lessonDao.insertLesson(entity)
            Log.d("LessonRepository", "Successfully fetched lesson: ${entity.title}")
            entity
        } catch (e: Exception) {
            Log.e("LessonRepository", "Failed to fetch lesson $lessonId: ${e.message}", e)
            // Fallback to local DB
            lessonDao.getLessonById(lessonId)
        }
    }

    // 🔹 Search lessons
    suspend fun searchLessons(query: String): List<LessonEntity> {
        return try {
            Log.d("LessonRepository", "Searching lessons with query: $query")
            val apiResults = ApiClient.wordpressApi.searchPosts(query)
            val entities = apiResults.map { dto ->
                LessonEntity(
                    id = dto.id,
                    title = dto.title.rendered,
                    content = dto.content.rendered,
                    audioUrl = dto.meta?.`the-audio-of-the-lesson`,
                    categoryId = dto.categories?.firstOrNull()
                )
            }
            // Optional: update DB
            lessonDao.insertLessons(entities)
            Log.d("LessonRepository", "Search returned ${entities.size} results")
            entities
        } catch (e: Exception) {
            Log.e("LessonRepository", "Search failed: ${e.message}", e)
            lessonDao.searchLessons(query)
        }
    }
}
