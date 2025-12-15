package ir.wpstorm.shams

import android.app.Application
import android.util.Log
import ir.wpstorm.shams.data.db.AppDatabase
import ir.wpstorm.shams.data.repository.CategoryRepository
import ir.wpstorm.shams.data.repository.DownloadedAudioRepository
import ir.wpstorm.shams.data.repository.LessonRepository
import ir.wpstorm.shams.data.repository.ProgressRepository
import ir.wpstorm.shams.player.AudioPlayer

class ShamsApplication : Application() {

    val database by lazy {
        Log.d("ShamsApplication", "Initializing database")
        AppDatabase.getDatabase(this)
    }

    val lessonRepository by lazy {
        Log.d("ShamsApplication", "Initializing lesson repository")
        LessonRepository(database.lessonDao())
    }

    val categoryRepository by lazy {
        Log.d("ShamsApplication", "Initializing category repository")
        CategoryRepository(database.categoryDao())
    }

    val downloadedAudioRepository by lazy {
        Log.d("ShamsApplication", "Initializing downloaded audio repository")
        DownloadedAudioRepository(database.downloadedAudioDao())
    }

    val progressRepository by lazy {
        Log.d("ShamsApplication", "Initializing progress repository")
        ProgressRepository(
            database.completedLessonDao(),
            database.courseProgressDao(),
            database.lessonDao()
        )
    }

    val globalAudioPlayer by lazy {
        Log.d("ShamsApplication", "Initializing global audio player")
        AudioPlayer(this)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("ShamsApplication", "Application created")
    }
}
