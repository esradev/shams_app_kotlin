package ir.wpstorm.shams.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_audios")
data class DownloadedAudioEntity(
    @PrimaryKey val id: String, // File path or unique identifier

    @ColumnInfo(name = "lesson_id")
    val lessonId: Int,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "file_path")
    val filePath: String,

    @ColumnInfo(name = "file_size")
    val fileSize: Long, // in bytes

    @ColumnInfo(name = "download_date")
    val downloadDate: Long,

    @ColumnInfo(name = "last_played")
    val lastPlayed: Long? = null,

    @ColumnInfo(name = "play_count")
    val playCount: Int = 0
)
