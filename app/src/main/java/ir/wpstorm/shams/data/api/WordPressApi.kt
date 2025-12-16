package ir.wpstorm.shams.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WordPressApi {

    // 🔹 Categories = Courses (only child categories with posts)
    @GET("wp-json/wp/v2/categories")
    suspend fun getCategories(
        @Query("per_page") perPage: Int = 20,
        @Query("hide_empty") hideEmpty: Boolean = true  // Only categories with posts
    ): List<CategoryDto>

    // 🔹 Lessons by Category (with headers for pagination)
    @GET("wp-json/wp/v2/posts")
    suspend fun getPostsByCategoryWithHeaders(
        @Query("categories") categoryId: Int,
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int = 1,
        @Query("orderby") orderBy: String = "date",
        @Query("order") order: String = "desc"
    ): Response<List<PostDto>>

    // 🔹 Lessons by Category (backward compatibility)
    @GET("wp-json/wp/v2/posts")
    suspend fun getPostsByCategory(
        @Query("categories") categoryId: Int,
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int = 1,
        @Query("orderby") orderBy: String = "date",
        @Query("order") order: String = "desc"
    ): List<PostDto>

    // 🔹 Single Lesson
    @GET("wp-json/wp/v2/posts/{id}")
    suspend fun getPostById(
        @Path("id") postId: Int
    ): PostDto

    // 🔹 Search Lessons
    @GET("wp-json/wp/v2/posts")
    suspend fun searchPosts(
        @Query("search") query: String,
        @Query("per_page") perPage: Int = 20
    ): List<PostDto>
}

data class CategoryDto(
    val id: Int,
    val name: String,
    val description: String = "",
    val parent: Int = 0,
    val count: Int = 0
)

data class PostDto(
    val id: Int,
    val title: RenderedText,
    val content: RenderedText,
    val meta: PostMeta?,
    val categories: List<Int>?
)

data class RenderedText(
    val rendered: String
)

data class PostMeta(
    val `the-audio-of-the-lesson`: String?,
    val `date-of-the-lesson`: String?
)
