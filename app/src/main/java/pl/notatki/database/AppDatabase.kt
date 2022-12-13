package pl.notatki.database

import androidx.room.Database
import androidx.room.RoomDatabase
import pl.notatki.model.Label
import pl.notatki.model.Note

@Database(entities = [Note::class, Label::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDAO
}