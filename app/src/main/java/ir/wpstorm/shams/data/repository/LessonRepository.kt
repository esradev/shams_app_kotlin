package ir.wpstorm.shams.data.repository

import android.util.Log
import ir.wpstorm.shams.data.api.ApiClient
import ir.wpstorm.shams.data.db.CategoryDao
import ir.wpstorm.shams.data.db.LessonDao
import ir.wpstorm.shams.data.db.LessonEntity

class LessonRepository(
    private val lessonDao: LessonDao,
    private val categoryDao: CategoryDao
) {

    private val api = ApiClient.wordpressApi

    /**
     * Selects the preferred category ID from a list of categories.
     * Prefers child categories (those with a parent) over parent categories.
     */
    private suspend fun selectPreferredCategoryId(categoryIds: List<Int>?): Int? {
        if (categoryIds.isNullOrEmpty()) return null
        if (categoryIds.size == 1) return categoryIds.first()

        // Check each category to find if any is a child (has a parent)
        for (categoryId in categoryIds) {
            val category = categoryDao.getCategoryById(categoryId)
            if (category != null && category.parent != 0) {
                // This is a child category, prefer it
                return categoryId
            }
        }

        // If no child category found, return the first one
        return categoryIds.firstOrNull()
    }

    // 🔹 Fetch lessons by category - Offline-first approach with Flow
    suspend fun getLessonsByCategory(
        categoryId: Int,
        page: Int = 1,
        perPage: Int = 20,
        orderBy: String = "date",
        order: String = "desc"
    ): List<LessonEntity> {
        return try {
            Log.d("LessonRepository", "Fetching lessons for category: $categoryId, page: $page")

            // 1️⃣ Check for cached data first
            val cachedLessons = lessonDao.getLessonsByCategorySync(categoryId)

            // 2️⃣ Try API to get fresh data
            try {
                val response = api.getPostsByCategoryWithHeaders(categoryId, perPage, page, orderBy, order)
                val apiLessons = response.body() ?: emptyList()
                Log.d("LessonRepository", "API returned ${apiLessons.size} lessons")

                val entities = apiLessons.map { dto ->
                    LessonEntity(
                        id = dto.id,
                        title = dto.title.rendered,
                        content = dto.content.rendered,
                        audioUrl = dto.meta?.`the-audio-of-the-lesson`,
                        categoryId = selectPreferredCategoryId(dto.categories) ?: categoryId,
                        dateOfLesson = dto.meta?.`date-of-the-lesson`,
                        isDownloaded = false,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis(),
                        isFavorite = false,
                        playCount = 0
                    )
                }

                // 3️⃣ Save to local DB
                // For pagination, we'll cache all pages but replace existing lessons with same IDs
                lessonDao.insertLessons(entities)
                Log.d("LessonRepository", "Saved ${entities.size} lessons to database")

                entities
            } catch (networkException: Exception) {
                Log.e("LessonRepository", "API call failed: ${networkException.message}")

                // 4️⃣ If network fails, return cached data if available (only for first page)
                if (cachedLessons.isNotEmpty() && page == 1) {
                    Log.d("LessonRepository", "Returning ${cachedLessons.size} cached lessons")
                    cachedLessons
                } else if (page == 1) {
                    Log.d("LessonRepository", "No cached lessons, returning mock data")
                    val mockLessons = getMockLessonsForCategory(categoryId)
                    lessonDao.insertLessons(mockLessons) // Cache mock data too
                    mockLessons
                } else {
                    // For pages other than 1, just return empty list if no network
                    emptyList()
                }
            }
        } catch (e: Exception) {
            Log.e("LessonRepository", "General error: ${e.message}", e)
            // 5️⃣ Fallback to mock data only for first page
            if (page == 1) {
                val mockLessons = getMockLessonsForCategory(categoryId)
                try {
                    lessonDao.insertLessons(mockLessons)
                } catch (dbException: Exception) {
                    Log.e("LessonRepository", "Failed to save mock data: ${dbException.message}")
                }
                mockLessons
            } else {
                emptyList()
            }
        }
    }

    private fun getMockLessonsForCategory(categoryId: Int): List<LessonEntity> {
        return listOf(
            LessonEntity(
                id = (categoryId * 1000) + 1,
                title = "جلسه اول - مقدمات و اصول کلی",
                content = """
                    <div dir="rtl">
                        <h3>بسم الله الرحمن الرحیم</h3>
                        <p>در این جلسه به بررسی مقدمات و اصول کلی می‌پردازیم که پایه و اساس درک صحیح مباحث بعدی است.</p>

                        <h4>سرفصل‌های مطرح شده:</h4>
                        <ul>
                            <li>تعریف و مفهوم‌شناسی اصطلاحات کلیدی</li>
                            <li>پیش‌نیازهای علمی و فرهنگی</li>
                            <li>روش‌شناسی مطالعه و تحقیق</li>
                            <li>منابع و مراجع اصلی</li>
                        </ul>

                        <p>ان‌شاء‌الله در جلسات آینده وارد مباحث تخصصی‌تر خواهیم شد.</p>
                    </div>
                """.trimIndent(),
                audioUrl = "https://example.com/audio/lesson${categoryId}_1.mp3",
                categoryId = categoryId,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                isFavorite = false,
                playCount = 0
            ),
            LessonEntity(
                id = (categoryId * 1000) + 2,
                title = "جلسه دوم - تاریخچه و پیشینه",
                content = """
                    <div dir="rtl">
                        <p>در این جلسه به بررسی تاریخچه و پیشینه مبحث می‌پردازیم.</p>

                        <h4>نکات مهم این جلسه:</h4>
                        <p>مطالعه تطبیقی دیدگاه‌های مختلف علمای گذشته و معاصر در این زمینه ضروری است.</p>

                        <blockquote>
                            <p>همان‌طور که علمای بزرگ ما فرموده‌اند، فهم درست مسائل نیازمند دقت و تأمل است.</p>
                        </blockquote>
                    </div>
                """.trimIndent(),
                audioUrl = "https://example.com/audio/lesson${categoryId}_2.mp3",
                categoryId = categoryId,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                isFavorite = false,
                playCount = 0
            ),
            LessonEntity(
                id = (categoryId * 1000) + 3,
                title = "جلسه سوم - مباحث کاربردی",
                content = """
                    <div dir="rtl">
                        <h3>مباحث عملی و کاربردی</h3>
                        <p>در این بخش به جنبه‌های عملی و کاربردی مطالب پرداخته‌ایم.</p>

                        <h4>مثال‌های عملی:</h4>
                        <ol>
                            <li>مسئله اول و راه‌حل آن</li>
                            <li>مسئله دوم و تحلیل جامع</li>
                            <li>نتیجه‌گیری و ارائه راهکار</li>
                        </ol>
                    </div>
                """.trimIndent(),
                audioUrl = "https://example.com/audio/lesson${categoryId}_3.mp3",
                categoryId = categoryId,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                isFavorite = false,
                playCount = 0
            )
        )
    }

    // 🔹 Get total lessons count for pagination
    suspend fun getTotalLessonsCount(categoryId: Int): Int {
        return try {
            Log.d("LessonRepository", "Getting total lessons count for category: $categoryId")

            // Make an API call to get pagination headers
            val response = api.getPostsByCategoryWithHeaders(categoryId, 1, 1, "date", "desc")

            // Extract X-WP-Total header for total count
            val totalHeader = response.headers()["X-WP-Total"]
            val total = totalHeader?.toIntOrNull()

            if (total != null && total > 0) {
                Log.d("LessonRepository", "Got total count from API header: $total")
                total
            } else {
                // Fallback: get from database
                val dbCount = lessonDao.getLessonCountByCategory(categoryId)
                Log.d("LessonRepository", "Fallback to database count: $dbCount")
                if (dbCount > 0) dbCount else 30 // Default fallback
            }
        } catch (e: Exception) {
            Log.e("LessonRepository", "Failed to get total count: ${e.message}")
            // Fallback to database count
            val dbCount = lessonDao.getLessonCountByCategory(categoryId)
            if (dbCount > 0) dbCount else 30
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
                categoryId = selectPreferredCategoryId(dto.categories)
            )

            // Save / update in DB
            lessonDao.insertLesson(entity)
            Log.d("LessonRepository", "Successfully fetched lesson: ${entity.title}")
            entity
        } catch (e: Exception) {
            Log.e("LessonRepository", "Failed to fetch lesson $lessonId: ${e.message}", e)
            // Fallback to local DB or mock data
            lessonDao.getLessonById(lessonId) ?: getMockLessonById(lessonId)
        }
    }

    private fun getMockLessonById(lessonId: Int): LessonEntity {
        val categoryId = lessonId / 1000
        val lessonNumber = lessonId % 1000

        return LessonEntity(
            id = lessonId,
            title = "جلسه $lessonNumber - نمونه درس شماره $lessonNumber",
            content = """
                <div dir="rtl">
                    <h3>بسم الله الرحمن الرحیم</h3>
                    <p>این محتوای نمونه برای درس شماره $lessonNumber است که جهت آزمایش UI طراحی شده است.</p>

                    <h4>مطالب این جلسه:</h4>
                    <ul>
                        <li>بحث و بررسی مسائل اساسی</li>
                        <li>تحلیل دیدگاه‌های مختلف</li>
                        <li>ارائه راهکارهای عملی</li>
                    </ul>

                    <p>ان‌شاء‌الله این مطالب مفید واقع شود.</p>

                    <blockquote>
                        <p>علم طلب کردن بر هر مسلمان فرض است.</p>
                    </blockquote>
                </div>
            """.trimIndent(),
            audioUrl = "https://example.com/audio/lesson$lessonId.mp3",
            categoryId = categoryId,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            isFavorite = false,
            playCount = 0
        )
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
                    categoryId = selectPreferredCategoryId(dto.categories)
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
