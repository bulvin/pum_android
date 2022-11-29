package pl.notatki


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import pl.notatki.databinding.ActivityMainBinding
import pl.notatki.databinding.ItemMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val buttonAddNote = binding.addNote
        buttonAddNote.setOnClickListener {
            val notePage = Intent(this, NoteActivity::class.java)
            startActivity(notePage)
        }

        //Button dodawania notatki
        //val buttonAddNote = findViewById<Button>(R.id.add_note_button) as Button

       var textView: TextView
       textView = findViewById(R.id.noteTitle)
        textView.text = "Tytuł"

        var textView2: TextView
        textView2 = findViewById(R.id.noteContent)
        textView2.text = "Treść notatki"



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



