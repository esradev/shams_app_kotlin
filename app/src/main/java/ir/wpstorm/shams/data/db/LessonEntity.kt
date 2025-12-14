package ir.wpstorm.shams.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey val id: Int,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "audio_url")
    val audioUrl: String? = null,

    @ColumnInfo(name = "category_id")
    val categoryId: Int? = null
)
