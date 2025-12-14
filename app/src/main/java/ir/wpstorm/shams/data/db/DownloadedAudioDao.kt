package ir.wpstorm.shams.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadedAudioDao {
    @Query("SELECT * FROM downloaded_audios ORDER BY download_date DESC")
    fun getAllDownloadedAudios(): Flow<List<DownloadedAudioEntity>>

    @Query("SELECT * FROM downloaded_audios WHERE id = :id")
    suspend fun getDownloadedAudioById(id: String): DownloadedAudioEntity?

    @Query("SELECT * FROM downloaded_audios WHERE lesson_id = :lessonId")
    suspend fun getDownloadedAudioByLessonId(lessonId: Int): DownloadedAudioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownloadedAudio(audio: DownloadedAudioEntity)

    @Update
    suspend fun updateDownloadedAudio(audio: DownloadedAudioEntity)

    @Delete
    suspend fun deleteDownloadedAudio(audio: DownloadedAudioEntity)

    @Query("DELETE FROM downloaded_audios WHERE id = :id")
    suspend fun deleteDownloadedAudioById(id: String)

    @Query("SELECT SUM(file_size) FROM downloaded_audios")
    suspend fun getTotalDownloadedSize(): Long?

    @Query("SELECT COUNT(*) FROM downloaded_audios")
    suspend fun getDownloadedAudioCount(): Int

    @Query("UPDATE downloaded_audios SET last_played = :timestamp, play_count = play_count + 1 WHERE lesson_id = :lessonId")
    suspend fun updatePlayInfo(lessonId: Int, timestamp: Long)

    @Query("DELETE FROM downloaded_audios")
    suspend fun clearAllDownloadedAudios()
}
