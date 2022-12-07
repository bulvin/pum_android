package pl.notatki.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
