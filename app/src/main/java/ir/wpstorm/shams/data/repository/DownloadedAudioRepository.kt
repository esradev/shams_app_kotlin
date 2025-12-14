package ir.wpstorm.shams.data.repository

import ir.wpstorm.shams.data.db.DownloadedAudioDao
import ir.wpstorm.shams.data.db.DownloadedAudioEntity
import kotlinx.coroutines.flow.Flow

class DownloadedAudioRepository(
    private val downloadedAudioDao: DownloadedAudioDao
) {

    /**
     * Get all downloaded audio files as Flow for real-time updates
     */
    fun getAllDownloadedAudios(): Flow<List<DownloadedAudioEntity>> {
        return downloadedAudioDao.getAllDownloadedAudios()
    }

    /**
     * Insert a new downloaded audio file
     */
    suspend fun insertDownloadedAudio(audio: DownloadedAudioEntity) {
        downloadedAudioDao.insertDownloadedAudio(audio)
    }

    /**
     * Delete a specific downloaded audio file
     */
    suspend fun deleteDownloadedAudio(audio: DownloadedAudioEntity) {
        downloadedAudioDao.deleteDownloadedAudio(audio)
    }

    /**
     * Clear all downloaded audio files
     */
    suspend fun clearAllDownloadedAudios() {
        downloadedAudioDao.clearAllDownloadedAudios()
    }

    /**
     * Update play information for a downloaded audio
     */
    suspend fun updatePlayInfo(lessonId: Int, timestamp: Long) {
        downloadedAudioDao.updatePlayInfo(lessonId, timestamp)
    }

    /**
     * Get total size of downloaded files
     */
    suspend fun getTotalDownloadedSize(): Long {
        return downloadedAudioDao.getTotalDownloadedSize() ?: 0L
    }

    /**
     * Get count of downloaded files
     */
    suspend fun getDownloadedAudioCount(): Int {
        return downloadedAudioDao.getDownloadedAudioCount()
    }

    /**
     * Get downloaded audio by lesson ID
     */
    suspend fun getDownloadedAudioByLessonId(lessonId: Int): DownloadedAudioEntity? {
        return downloadedAudioDao.getDownloadedAudioByLessonId(lessonId)
    }
}
