package pl.notatki.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import pl.notatki.model.*

@Dao
interface NoteDAO {

    @Transaction
    @Query("SELECT * FROM Note")
    fun getAll(): List<NoteWithLabels>


    @Query("SELECT * FROM Note")
    fun getNotes(): List<Note>

    @Insert
    fun insertNote(note: Note)

    @Delete
    fun delete(note: Note)

    @Update
    fun update(note: Note)

    @Query("SELECT * FROM Label")
    fun getLabels(): List<Label>


    @Insert
    fun insertLabel(label: Label)

    @Delete
    fun deleteLabel(label: Label)

    @Update
    fun updateLabel(label: Label)

    @Query("SELECT * FROM label")
    fun getLabelForDialog() : List<Label>

    @Insert
    fun insertNoteWithLabels(note: NoteLabelCrossRef)



}