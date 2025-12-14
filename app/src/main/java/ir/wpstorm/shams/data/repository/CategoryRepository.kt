package ir.wpstorm.shams.data.repository

import android.util.Log
import ir.wpstorm.shams.data.api.ApiClient
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
                    name = dto.name,
                    description = dto.description,
                    parent = dto.parent,
                    count = dto.count
                )
            }
        } catch (e: Exception) {
            Log.e("CategoryRepository", "Failed to fetch categories: ${e.message}", e)
            // Return mock data for testing UI
            Log.d("CategoryRepository", "Returning mock data for testing")
            getMockCategories()
        }
    }

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
