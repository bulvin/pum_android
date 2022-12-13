package pl.notatki.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var instance: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase = instance ?: synchronized(this) {
        instance ?: buildDatabase(context).also { instance = it }
    }

    private fun buildDatabase(context: Context) = Room
        .databaseBuilder(context, AppDatabase::class.java, "notes-database")
        .build()

}