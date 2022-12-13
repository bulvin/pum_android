package pl.notatki.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import pl.notatki.model.Note

@Dao
interface NoteDAO {

    @Query("SELECT * FROM Note")
    fun getAll(): List<Note>

    @Insert
    fun insertNote(note: Note)

    @Delete
    fun delete(note: Note)

    @Update
    fun update(note: Note)
}