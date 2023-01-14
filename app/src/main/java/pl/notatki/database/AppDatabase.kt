package pl.notatki.database

import androidx.room.Database
import androidx.room.RoomDatabase
import pl.notatki.model.Label
import pl.notatki.model.Note
import pl.notatki.model.NoteLabelCrossRef

@Database(entities = [Note::class, Label::class, NoteLabelCrossRef::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDAO
}