package pl.notatki.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import pl.notatki.databinding.ActivityNoteBinding
import pl.notatki.model.Note
import pl.notatki.repository.NoteRepository


class NoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteBinding
    private val repository: NoteRepository by lazy { NoteRepository(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoteBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val buttonNoteActivity = binding.returnButton
        buttonNoteActivity.setOnClickListener {
            val notePage = Intent(this, MainActivity::class.java)
            val message: String
            if(addNote()){
                 message = "Notatka została zapisana"
                 startActivity(notePage)
            }else{
                  message =  "Notatka nie została zapisana"

            }
            notePage.putExtra("info", message)
            startActivity(notePage)

        }

//        binding.deleteButton.setOnClickListener{ deleteNote() }

        binding.noteImg.visibility = View.GONE



    }

    private fun addNote() : Boolean{
        val title = binding.inputTitle.text.toString()
        val desc = binding.inputDesc.text.toString()
        val img = binding.noteImg.context.toString()
        val label = null
        val notification = null

        if (validateNote(title, desc)){
            val note = Note( null,title,desc, " ",notification,"13 gru, 2022 21:00")
            runOnUiThread { repository.insertNoteToDabase(note) }
            return true
        }
       return false
    }

    private fun validateNote(title: String, desc: String): Boolean {
        if (title.isEmpty() || title == " " && desc.isEmpty() || desc == "" ){
            return false
        }
        return true
    }
    private fun deleteNote(note: Note) {
        repository.delete(note)
    }
}

