package ir.wpstorm.shams.data.repository

import android.util.Log
import ir.wpstorm.shams.data.api.ApiClient
import ir.wpstorm.shams.data.db.CategoryDao
import ir.wpstorm.shams.data.db.CategoryEntity
import ir.wpstorm.shams.viewmodel.CategoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class CategoryRepository(
    private val categoryDao: CategoryDao
) {

    private val api = ApiClient.wordpressApi

    /**
     * Offline-first approach: Return cached data first, then try to refresh from network
     */
    fun getCategories(): Flow<List<CategoryItem>> = flow {
        try {
            // First emit cached data if available
            val cachedCategories = categoryDao.getAllCategories().first()
            if (cachedCategories.isNotEmpty()) {
                Log.d("CategoryRepository", "Emitting ${cachedCategories.size} cached categories")
                emit(cachedCategories.map { it.toCategoryItem() })
            }

            // Try to fetch fresh data from network
            try {
                Log.d("CategoryRepository", "Fetching categories from API")
                val apiCategories = api.getCategories()
                Log.d("CategoryRepository", "API returned ${apiCategories.size} categories")

                // Convert to entities and save to local database
                val entities = apiCategories.map { dto ->
                    CategoryEntity(
                        id = dto.id,
                        name = dto.name,
                        description = dto.description,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                }
                categoryDao.insertCategories(entities)

                // Convert to CategoryItems and emit fresh data
                val categoryItems = apiCategories.map { dto ->
                    CategoryItem(
                        id = dto.id,
                        name = dto.name,
                        description = dto.description,
                        parent = dto.parent,
                        count = dto.count
                    )
                }
                emit(categoryItems)

            } catch (e: Exception) {
                Log.e("CategoryRepository", "Failed to fetch from API: ${e.message}")
                // If network fails but we have cached data, just use that
                if (cachedCategories.isEmpty()) {
                    Log.w("CategoryRepository", "No cached data available, returning mock data")
                    emit(getMockCategories())
                }
                // Otherwise, silently continue with cached data
            }
        } catch (e: Exception) {
            Log.e("CategoryRepository", "Error in getCategories: ${e.message}")
            // If everything fails, return mock data
            emit(getMockCategories())
        }
    }

    suspend fun refreshCategories() {
        try {
            Log.d("CategoryRepository", "Refreshing categories from API")
            val apiCategories = api.getCategories()
            val entities = apiCategories.map { dto ->
                CategoryEntity(
                    id = dto.id,
                    name = dto.name,
                    description = dto.description,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            }
            categoryDao.insertCategories(entities)
        } catch (e: Exception) {
            Log.e("CategoryRepository", "Failed to refresh categories: ${e.message}")
            // Silently fail - offline mode will continue to work
        }
    }

    suspend fun getCachedCategoriesCount(): Int {
        return categoryDao.getCategoryCount()
    }

    private fun CategoryEntity.toCategoryItem() = CategoryItem(
        id = id,
        name = name,
        description = description ?: "",
        parent = 1, // Default parent
        count = 0 // We could calculate this from lessons if needed
    )

    private fun getMockCategories(): List<CategoryItem> {
        return listOf(
            CategoryItem(
                id = 1,
                name = "درس خارج فقه",
                description = "مطالعه عمیق مسائل فقهی با رویکرد مقایسه‌ای و تحلیلی برای درک بهتر احکام شرعی و کاربرد آن‌ها در زندگی مؤمنان",
                parent = 1,
                count = 45
            ),
            CategoryItem(
                id = 2,
                name = "درس خارج اصول",
                description = "بررسی مبانی استنباط احکام شرعی و قواعد اصولی که فقها در استخراج حکم از منابع معتبر اسلامی به کار می‌برند",
                parent = 1,
                count = 38
            ),
            CategoryItem(
                id = 3,
                name = "درس خارج تفسیر قرآن",
                description = "تفسیر و تبیین آیات قرآن کریم با استفاده از روش‌های علمی و ادبی برای فهم عمیق‌تر مفاهیم قرآنی",
                parent = 1,
                count = 52
            ),
            CategoryItem(
                id = 4,
                name = "درس خارج فلسفه",
                description = "مطالعه مباحث فلسفی از منظر اسلامی شامل معرفت‌شناسی، هستی‌شناسی و اخلاق با رویکردی عقلانی",
                parent = 1,
                count = 29
            ),
            CategoryItem(
                id = 5,
                name = "درس خارج کلام",
                description = "بحث و بررسی مسائل اعتقادی و کلامی از دیدگاه شیعه با پاسخ به شبهات و تقویت بنیان‌های ایمانی",
                parent = 1,
                count = 33
            )
        )
    }
}
