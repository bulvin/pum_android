package pl.notatki.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import pl.notatki.adapter.LabelAdapter
import pl.notatki.adapter.MainAdapter
import pl.notatki.databinding.ActivityLabelsBinding
import pl.notatki.model.Label
import pl.notatki.model.Note
import pl.notatki.repository.NoteRepository


class LabelsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLabelsBinding
    private val adapter = LabelAdapter()
    private val repository: NoteRepository by lazy { NoteRepository(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLabelsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        val buttonNoteActivity = binding.returnButton
        buttonNoteActivity.setOnClickListener {
            val notePage = Intent(this, MainActivity::class.java)
            startActivity(notePage)
        }


        val buttonAddLabel = binding.addLabelButton
        buttonAddLabel.setOnClickListener {
            addLabel()
            loadlabels()
        }

        val label1 = Label(111,"Tytuł")
        label1.name = "Tytuł"
        val arrList = arrayListOf<Label>()

        arrList.add(label1)
        //adapter.submitList(arrList)

        loadlabels()
    }

    private fun addLabel(){
        val title = "test"

        val label = Label( null,title)
        runOnUiThread { repository.insertLabelToDabase(label) }
    }

    private fun loadlabels() {

        repository.getLabels { labelList: List<Label> ->
            runOnUiThread {
                adapter.submitList(labelList)

            }
        }
    }
}
