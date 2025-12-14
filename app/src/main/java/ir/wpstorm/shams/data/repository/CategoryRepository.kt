package ir.wpstorm.shams.data.repository

import android.util.Log
import ir.wpstorm.shams.data.api.ApiClient
import ir.wpstorm.shams.data.api.CategoryDto
import ir.wpstorm.shams.viewmodel.CategoryItem

class CategoryRepository {

    private val api = ApiClient.wordpressApi

    suspend fun getCategories(): List<CategoryItem> {
        return try {
            Log.d("CategoryRepository", "Fetching categories from API")
            val apiCategories = api.getCategories()
            Log.d("CategoryRepository", "API returned ${apiCategories.size} categories")

            apiCategories.map { dto ->
                CategoryItem(
                    id = dto.id,
                    name = dto.name
                )
            }
        } catch (e: Exception) {
            Log.e("CategoryRepository", "Failed to fetch categories: ${e.message}", e)
            // Return empty list or throw exception based on your error handling strategy
            throw e
        }
    }
}
