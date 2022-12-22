package pl.notatki.repository

import android.content.Context
import pl.notatki.database.DatabaseProvider
import pl.notatki.model.Note
import pl.notatki.model.NoteWithLabels
import java.util.concurrent.Executors

class NoteRepository(context: Context) {
    private val database = DatabaseProvider.getInstance(context.applicationContext)
    private  val  executor = Executors.newSingleThreadExecutor()

    fun insertNoteToDabase(note: Note){
        executor.execute { database.noteDao().insertNote(note) }

    }
    fun loadNotes(callback: (List<NoteWithLabels>) -> Unit){
        executor.execute {
            callback(database.noteDao().getAll())
        }
    }
    fun getNotes(callback: (List<Note>) -> Unit){
        executor.execute {
            callback(database.noteDao().getNotes())
        }
    }
    fun delete(note: Note){
        executor.execute { database.noteDao().delete(note) }
    }

}