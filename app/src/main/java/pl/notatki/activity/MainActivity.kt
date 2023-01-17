package pl.notatki.activity


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.SearchView
import android.widget.SearchView
import android.app.SearchManager;

import android.widget.SearchView.OnQueryTextListener;
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import pl.notatki.R
import pl.notatki.adapter.MainAdapter
import pl.notatki.databinding.ActivityMainBinding
import pl.notatki.model.Note
import pl.notatki.model.NoteWithLabels
import pl.notatki.repository.NoteRepository


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val adapter = MainAdapter {
        note -> NoteActivity.start(this, note)
    }
    private val repository by lazy { NoteRepository(applicationContext) }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)



        val buttonAddNote = binding.addNote
        buttonAddNote.setOnClickListener {
            val notePage = Intent(this, NoteActivity::class.java)
            startActivity(notePage)
        }

        //MenuClickListener
        binding.materialToolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.labels -> startLabelsActivity()
            }

            if(it.itemId == R.id.notes){
                binding.materialToolbar.title = "Notatki Mobilne"
                loadNotes()
            }

            if(it.itemId == R.id.search){
                val searchTest = R.id.search
                val searched = R.id.search.toString()

                binding.materialToolbar.title = searched
                loadSearchNotes(searched)
            }

            if(it.itemId == R.id.notes){
                binding.materialToolbar.title = "Notatki Mobilne"
                loadNotes()
            }

            if(it.itemId == R.id.reminders){
                binding.materialToolbar.title = "Przypomnienia"
                loadReminderNotes()
            }

            if(it.itemId == R.id.archives){
                binding.materialToolbar.title = "Archiwum"
                loadArchiveNotes()
            }

            true
        }

        val searchItem = binding.materialToolbar.menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return  false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter(newText)
                return true
            }

        })
        //adapter.submitList(arrList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        loadNotes()
        }

    private fun loadReminderNotes(){
        val value = intent.getStringExtra("info")
        repository.getAll { noteList: List<NoteWithLabels> ->
            runOnUiThread {
                adapter.setNotesReminders(noteList)
            }
        }

        if (value == "Notatka została zapisana" || value == "Notatka nie została zapisana"){
            Snackbar.make(binding.root,
                value, Snackbar.LENGTH_INDEFINITE).run {
                setActionTextColor(resources.getColor(R.color.black))
                setAction("OK") {

                    dismiss()
                }
            }.show()
        }
    }

    private fun loadArchiveNotes(){
        val value = intent.getStringExtra("info")
        repository.getAll { noteList: List<NoteWithLabels> ->
            runOnUiThread {
                adapter.setNotesArchive(noteList)
            }
        }

        if (value == "Notatka została zapisana" || value == "Notatka nie została zapisana"){
            Snackbar.make(binding.root,
                value, Snackbar.LENGTH_INDEFINITE).run {
                setActionTextColor(resources.getColor(R.color.black))
                setAction("OK") {

                    dismiss()
                }
            }.show()
        }
    }

    private fun loadSearchNotes(searched: String){
        val value = intent.getStringExtra("info")
        repository.getAll { noteList: List<NoteWithLabels> ->
            runOnUiThread {
                adapter.setNotesSearch(noteList, searched)
            }
        }

        if (value == "Notatka została zapisana" || value == "Notatka nie została zapisana"){
            Snackbar.make(binding.root,
                value, Snackbar.LENGTH_INDEFINITE).run {
                setActionTextColor(resources.getColor(R.color.black))
                setAction("OK") {

                    dismiss()
                }
            }.show()
        }
    }

    private fun loadNotes() {

        val value = intent.getStringExtra("info")
        repository.getAll { noteList: List<NoteWithLabels> ->
            runOnUiThread {
                adapter.setNotes(noteList)
            }
        }

        if (value == "Notatka została zapisana" || value == "Notatka nie została zapisana"){
            Snackbar.make(binding.root,
                value, Snackbar.LENGTH_INDEFINITE).run {
                setActionTextColor(resources.getColor(R.color.black))
                setAction("OK") {

                    dismiss()
                }
            }.show()
        }
    }

    private fun startLabelsActivity(){
        val notePage = Intent(this, LabelsActivity::class.java)
        startActivity(notePage)
    }


}



