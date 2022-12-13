package pl.notatki.activity


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import pl.notatki.adapter.MainAdapter
import pl.notatki.databinding.ActivityMainBinding
import pl.notatki.model.Note
import pl.notatki.model.NoteWithLabels
import pl.notatki.repository.NoteRepository


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val adapter = MainAdapter()
    private val repository by lazy { NoteRepository(applicationContext) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        val buttonAddNote = binding.addNote
        buttonAddNote.setOnClickListener {
            val notePage = Intent(this, NoteActivity::class.java)
            startActivity(notePage)


        }
        val note1 = Note(1,"Tytuł","Treść notatki",null,null,null)
        note1.title = "Tytuł"
        note1.content = "treść"
        val note2 = Note(2,"Tytuł","Treść notatki",null,null,null)
        val arrList = arrayListOf<Note>()



        arrList.add(note1)
        arrList.add(note2)
        //adapter.submitList(arrList)

        loadNotes()
        }

    private fun loadNotes() {

        repository.getNotes { noteList: List<Note> ->
            runOnUiThread {
                adapter.submitList(noteList)

            }
        }
    }


}



