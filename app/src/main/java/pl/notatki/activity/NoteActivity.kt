package pl.notatki.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import pl.notatki.databinding.ActivityNoteBinding
import pl.notatki.model.Note
import pl.notatki.repository.NoteRepository
import java.text.SimpleDateFormat
import java.util.*


class NoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteBinding
    private val repository: NoteRepository by lazy { NoteRepository(applicationContext) }
    private var edit: Boolean = false
    private var selectedImg = ""


    private  val REQUEST_CODE_PICK_IMAGE = 1



    companion object {
        private const val EXTRAS_NOTE = "EXTRAS_NOTE"

        fun start(activity: Activity, note: Note) {
            val intent = Intent(activity, NoteActivity::class.java)
            intent.putExtra(EXTRAS_NOTE, note)
            activity.startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoteBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val buttonNoteActivity = binding.returnButton
        buttonNoteActivity.setOnClickListener {
            val main = Intent(this, MainActivity::class.java)
            val message: String
            if(addNote() && !edit){
                 message = "Notatka została zapisana"
            }else{
                  message =  "Notatka nie została zapisana"
            }
            main.putExtra("info", message)
            startActivity(main)
            finish()
        }



        val buttonAddLabel = binding.buttonAddTags
        buttonAddLabel.setOnClickListener  {
            val noteLabels = Intent(this, NoteLabelsActivity::class.java)
            startActivity(noteLabels)
        }

        val calendar = Calendar.getInstance()

        val timePicker = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            formatterReminder(calendar)
        }

        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.YEAR, year)
            formatterReminder(calendar)
        }

        val buttonReminder = binding.buttonReminder

        buttonReminder.setOnClickListener {
            DatePickerDialog(this, datePicker, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR)).show()
            TimePickerDialog(this, timePicker, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE),
                true).show()
        }

        val buttonAddReminder = binding.buttonAddReminder
        buttonAddReminder.setOnClickListener  {
            if(buttonReminder.visibility == View.GONE){
                buttonReminder.visibility = View.VISIBLE
                buttonAddReminder.text = "Przypomnienie-"

            } else {
                buttonReminder.visibility = View.GONE
                buttonAddReminder.text = "Przypomnienie+"
            }

        }
        binding.imageButon.setOnClickListener{ pickImg() }


        intent.extras?.getParcelable<Note>(EXTRAS_NOTE)?.let { note ->
            showData(note)
            edit = true
            binding.deleteButton.setOnClickListener{ deleteNote(note)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            binding.returnButton.setOnClickListener{
                note.title = binding.inputTitle.text.toString()
                note.content = binding.inputDesc.text.toString()
                note.image = selectedImg
                /*if(buttonReminder.visibility == View.VISIBLE){

                }*/

                updateNote(note)
                val main = Intent(this, MainActivity::class.java)
                startActivity(main)
                finish()
            }

        }

        binding.noteImg.visibility = View.GONE
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // sprawdzamy, czy wynik pochodzi od odpowiedniego intentu
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            // pobieramy URI obrazka z intentu
            val imageUri = data?.data
            if (imageUri != null){
                // wczytujemy obrazek do pamięci
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

                // wyświetlamy obrazek w imageView
                binding.noteImg.setImageBitmap(bitmap)

                binding.noteImg.visibility = View.VISIBLE

                selectedImg = getPathImg(imageUri)!!
            }


        }
    }

    private fun getPathImg(contentUri: Uri) : String?{
        var path: String? = null
        val cursor: Cursor? = contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null){
            path = contentUri.path
        }
        else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex("_data")
            path = cursor.getString(index)
            cursor.close()

        }
        return path


    }




    private fun formatterReminder(calendar: Calendar) {
        val formatter = SimpleDateFormat("hh:mm dd-MM-yyyy", Locale.UK)
        //val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.UK)
        val buttonReminder = binding.buttonReminder
        buttonReminder.text = formatter.format(calendar.time)
    }

    private fun addNote() : Boolean{
        val title = binding.inputTitle.text.toString()
        val desc = binding.inputDesc.text.toString()
        val img = binding.noteImg.context.toString()
        val label = null
        val notification = null

        if (validateNote(title, desc)){
            val note = Note( null,title,desc, " ",notification,"13 gru, 2022 21:00")
            runOnUiThread { repository.insertNoteToDabase(note) }
            return true
        }
       return false
    }

    private fun validateNote(title: String, desc: String): Boolean {
        if (title.isEmpty() || title == " " && desc.isEmpty() || desc == "" ){
            return false
        }
        return true
    }
    private fun deleteNote(note: Note) {

        runOnUiThread { repository.delete(note) }
    }

    private fun updateNote(note: Note){
        runOnUiThread { repository.update(note) }
    }

    private fun showData(note: Note){

        if (note.image != ""){
            binding.noteImg.visibility = View.VISIBLE
        }
        else{
            binding.noteImg.visibility = View.GONE
        }
        binding.inputTitle.setText(note.title)
        binding.inputDesc.setText(note.content)
    }

    private fun pickImg(){
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_CODE_PICK_IMAGE)

    }



}

