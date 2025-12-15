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
                // Filter to only child categories before emitting
                val filteredCachedCategories = cachedCategories
                    .map { it.toCategoryItem() }
                    .filter { it.parent != 0 }
                emit(filteredCachedCategories)
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
                        parent = dto.parent,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                }
                categoryDao.insertCategories(entities)

                // Convert to CategoryItems and filter to only child categories before emitting
                val categoryItems = apiCategories
                    .map { dto ->
                        CategoryItem(
                            id = dto.id,
                            name = dto.name,
                            description = dto.description,
                            parent = dto.parent,
                            count = dto.count
                        )
                    }
                    .filter { it.parent != 0 } // Only child categories

                emit(categoryItems)

            } catch (e: Exception) {
                Log.e("CategoryRepository", "Failed to fetch from API: ${e.message}")
                // If network fails but we have cached data, just use that
                if (cachedCategories.isEmpty()) {
                    Log.w("CategoryRepository", "No cached data available and network failed")
                    // Don't emit anything here - let the UI handle the empty state
                    throw e
                }
                // Otherwise, silently continue with cached data already emitted above
            }
        } catch (e: Exception) {
            Log.e("CategoryRepository", "Error in getCategories: ${e.message}")
            // If everything fails, throw the exception to let the UI handle error state
            throw e
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
                    parent = dto.parent,
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
        parent = parent,
        count = 0 // We could calculate this from lessons if needed
    )
}
