package ir.wpstorm.shams

import android.app.Application
import android.util.Log
import ir.wpstorm.shams.data.db.AppDatabase
import ir.wpstorm.shams.data.repository.CategoryRepository
import ir.wpstorm.shams.data.repository.LessonRepository

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
        CategoryRepository() 
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("ShamsApplication", "Application created")
    }
}
