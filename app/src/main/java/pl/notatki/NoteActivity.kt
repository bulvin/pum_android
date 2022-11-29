package pl.notatki

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import pl.notatki.databinding.ActivityMainBinding
import pl.notatki.databinding.ActivityNoteBinding


class NoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoteBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val buttonNoteActivity = binding.returnButton
        buttonNoteActivity.setOnClickListener {
            val notePage = Intent(this, MainActivity::class.java)
            startActivity(notePage)
        }

    }
}
