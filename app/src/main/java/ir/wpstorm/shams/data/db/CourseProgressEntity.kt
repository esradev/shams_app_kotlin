package ir.wpstorm.shams.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "course_progress")
data class CourseProgressEntity(
    @PrimaryKey
    @ColumnInfo(name = "category_id")
    val categoryId: Int,

    @ColumnInfo(name = "last_lesson_id")
    val lastLessonId: Int,

    @ColumnInfo(name = "total_lessons")
    val totalLessons: Int,

    @ColumnInfo(name = "completed_lessons")
    val completedLessons: Int,

    @ColumnInfo(name = "last_accessed_at")
    val lastAccessedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "started_at")
    val startedAt: Long = System.currentTimeMillis()
)
