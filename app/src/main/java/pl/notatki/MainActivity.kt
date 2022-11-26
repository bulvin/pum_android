package pl.notatki

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import pl.notatki.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        //Button dodawania notatki
     //   val buttonAddNote = findViewById<Button>(R.id.add_note_button) as Button

        //Styl dla notatki
        val notatkaStyle = ContextThemeWrapper(baseContext, android.R.style.Theme)

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
