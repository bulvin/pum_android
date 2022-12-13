package pl.notatki.activity


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import pl.notatki.adapter.MainAdapter
import pl.notatki.databinding.ActivityMainBinding
import pl.notatki.model.Note


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val adapter = MainAdapter()

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
        val note1 = Note(1,"Tytuł","Treść notatki",null,"",null)
        note1.title = "Tytuł"
        note1.content = "treść"
        val note2 = Note(2,"Tytuł","Treść notatki",null,"",null)
        val arrList = arrayListOf<Note>()



      arrList.add(note1)
        arrList.add(note2)
        adapter.submitList(arrList)

        //Button dodawania notatki
        //val buttonAddNote = findViewById<Button>(R.id.add_note_button) as Button




//        buttonAddNote.setOnClickListener{
//            val note_card = CardView(notatkaStyle)
//            val note_title = TextView(this)
//            note_title.text = "Title"
//            val notes_layout = findViewById<LinearLayout>(R.id.notes_layout)
//            notes_layout.addView(note_card)
//
//            note_card.addView(note_title)
        }

}



