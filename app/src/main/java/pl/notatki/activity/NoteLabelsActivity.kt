package pl.notatki.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import pl.notatki.adapter.LabelAdapter
import pl.notatki.adapter.MainAdapter
import pl.notatki.databinding.ActivityLabelsBinding
import pl.notatki.databinding.ActivityNoteLabelsBinding
import pl.notatki.model.Label
import pl.notatki.model.Note
import pl.notatki.repository.NoteRepository


class NoteLabelsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteLabelsBinding
    private var edit: Boolean = false
    private val adapter = LabelAdapter {

            label: Label -> label
            loadlabels()

            val menuAdd = binding.menuBottomAdd
            menuAdd.visibility = View.GONE

            val menuEdit = binding.menuBottomEdit
            menuEdit.visibility = View.VISIBLE

            binding.textInputEditLabel.hint = label.name

            binding.deleteLabelButton2.setOnClickListener{ deleteLabel(label)
                loadlabels()

                val menuEdit = binding.menuBottomEdit
                menuEdit.visibility = View.GONE

                val menuAdd = binding.menuBottomAdd
                menuAdd.visibility = View.VISIBLE
            }

            binding.confirmLabelButton2.setOnClickListener{
                label.name = binding.inputEditLabel.text.toString()
                updateLabel(label)
                val main = Intent(this, MainActivity::class.java)

                loadlabels()

                val menuEdit = binding.menuBottomEdit
                menuEdit.visibility = View.GONE

                val menuAdd = binding.menuBottomAdd
                menuAdd.visibility = View.VISIBLE

            }
    }

    private val repository: NoteRepository by lazy { NoteRepository(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoteLabelsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        val buttonNoteActivity = binding.returnButton
        buttonNoteActivity.setOnClickListener {
            val notePage = Intent(this, NoteActivity::class.java)
            startActivity(notePage)
            finish()
        }

        val buttonConfirmLabel = binding.confirmLabelButton2
        buttonConfirmLabel.setOnClickListener() {
            loadlabels()

            val menuEdit = binding.menuBottomEdit
            menuEdit.visibility = View.GONE

            val menuAdd = binding.menuBottomAdd
            menuAdd.visibility = View.VISIBLE
        }

        val buttonAddLabel = binding.addLabelButton
        buttonAddLabel.setOnClickListener {
            addLabel()
            loadlabels()
        }

        loadlabels()
    }

    private fun showData(label: Label){
        binding.inputEditLabel.setText(label.name)
    }

    private fun addLabel(): Boolean{
        var name = binding.inputLabel.text.toString()

        if(binding.inputLabel.text.toString() == "")
        {
            name = ""
        }

        if (validateLabel(name)) {
            val label = Label(null, name)
            runOnUiThread { repository.insertLabelToDabase(label)}
            return true
        }
        return false
    }

    private fun loadlabels() {

        repository.getLabels { labelList: List<Label> ->
            runOnUiThread {
                adapter.submitList(labelList)

            }
        }
    }

    private fun deleteLabel(label: Label) {
        runOnUiThread { repository.deleteLabel(label) }
    }

    private fun updateLabel(label: Label){
        runOnUiThread { repository.updateLabel(label) }
    }

    private fun validateLabel(name: String): Boolean {
        if (name.isEmpty() || name == " "){
            return false
        }
        return true
    }
}
