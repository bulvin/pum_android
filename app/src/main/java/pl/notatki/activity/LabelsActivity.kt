package pl.notatki.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pl.notatki.databinding.ActivityLabelsBinding


class LabelsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLabelsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLabelsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val buttonNoteActivity = binding.returnButton
        buttonNoteActivity.setOnClickListener {
            val notePage = Intent(this, MainActivity::class.java)
            startActivity(notePage)
        }

    }
}
