package pl.notatki.activity

import android.app.*
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.notatki.R
import pl.notatki.databinding.ActivityNoteBinding
import pl.notatki.model.Note
import pl.notatki.model.Reminder
import pl.notatki.repository.NoteRepository
import java.text.SimpleDateFormat
import java.util.*


class NoteActivity : AppCompatActivity() {

    private lateinit var notificationManager : NotificationManager
    private lateinit var notificationChannel : NotificationChannel
    private lateinit var builder : Notification.Builder
    private var channelID = "pl.notatki.activity"

    private lateinit var binding: ActivityNoteBinding
    private val repository: NoteRepository by lazy { NoteRepository(applicationContext) }
    private var edit: Boolean = false
    private var selectedImg = ""
    private val channelId = "notatki_przypomnienia" //Kanał dla przypomnień

    private val REQUEST_CODE_PICK_IMAGE = 1     //Request code do wybierania obrazu z galerii
    private val REQUEST_CODE_SPEECH_INPUT = 1   //Request code do notatki głosowej
    private val MIC_STATUS = 0                  //Czy mikrofon jest włączony, czy nie

    companion object {
        const val EXTRAS_NOTE = "EXTRAS_NOTE"

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

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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



        //Labels
        val buttonAddLabel = binding.buttonAddTags
        buttonAddLabel.setOnClickListener  {

            //Tylko do testowania notyfikacji na razie, bo jedyny wolny button, można usunąć
            sendNotification()
        }

        //Remindeers
        val buttonReminder = binding.buttonReminder


        intent.extras?.getParcelable<Note>(EXTRAS_NOTE)?.let { note ->
            val reminder = note.reminder
            if (reminder != null) { //Sprawdza czy przypomnienie nie jest puste i na podstawie tego wyświetla je, albo nie
                if (reminder.timeReminder != "" || reminder.date != "" || reminder.location != "" ) {
                    buttonReminder.visibility = View.VISIBLE
                    buttonReminder.text = reminder.timeReminder + reminder.date + reminder.location
                }
            }

            if(note.archived == true){ //Przy ładowaniu sprawdza czy jest archiwizowana, żeby ustalić odpowiednią ikonę
                binding.archiveButton.setImageResource(R.drawable.ic_baseline_unarchive_24)
            } else {
                binding.archiveButton.setImageResource(R.drawable.ic_baseline_archive_24)
            }
        }

        val buttonAddReminder = binding.buttonAddReminder
        val reminder = Reminder("","","")

        val calendar = Calendar.getInstance()

        //Wybieranie czasu
        val timePicker = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            formatterReminder(calendar)

            val formatterTime = SimpleDateFormat("hh:mm", Locale.UK)
            reminder.timeReminder = formatterTime.format(calendar.time)
        }

        //Wybieranie daty
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.YEAR, year)
            formatterReminder(calendar)

            val formatterDate = SimpleDateFormat("dd.MM.yyyy", Locale.UK)
            reminder.date = formatterDate.format(calendar.time)
        }

        //Dodaje przycisk Reminder
        buttonAddReminder.setOnClickListener  {

            if(buttonReminder.visibility == View.GONE){
                buttonReminder.visibility = View.VISIBLE
                buttonAddReminder.text = "Przypomnienie-"

            } else {
                buttonReminder.visibility = View.GONE
                buttonAddReminder.text = "Przypomnienie+"

            }
        }

        //Otwiera date i time pickery
        buttonReminder.setOnClickListener {
            DatePickerDialog(this, datePicker, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR)).show()

            TimePickerDialog(this, timePicker, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE),
                true).show()
        }

        //Notatka głosowa
        binding.voiceButton.setOnClickListener {
            // on below line we are calling speech recognizer intent.
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

            // on below line we are passing language model
            // and model free form in our intent
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )

            // on below line we are passing our
            // language as a default language.
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
            )

            // on below line we are specifying a prompt
            // message as speak to text on below line.
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

            // on below line we are specifying a try catch block.
            // in this block we are calling a start activity
            // for result method and passing our result code.
            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
            } catch (e: Exception) {
                // on below line we are displaying error message in toast
                Toast
                    .makeText(
                        this, " " + e.message,
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }
        }


        binding.imageButon.setOnClickListener{ pickImg() }


        intent.extras?.getParcelable<Note>(EXTRAS_NOTE)?.let { note ->
            showData(note)
            edit = true

            binding.archiveButton.setOnClickListener {
                if(note.archived == false){
                    note.archived = true;
                    binding.archiveButton.setImageResource(R.drawable.ic_baseline_unarchive_24)
                    updateNote(note)
                } else {
                    note.archived = false;
                    binding.archiveButton.setImageResource(R.drawable.ic_baseline_archive_24)
                    updateNote(note)
                }
            }

            binding.deleteButton.setOnClickListener{ deleteNote(note)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            binding.returnButton.setOnClickListener{
                note.title = binding.inputTitle.text.toString()
                note.content = binding.inputDesc.text.toString()
                note.image = selectedImg
                if(reminder.timeReminder != "" || reminder.date != "" || reminder.location != "" ){
                    note.reminder = reminder
                } else {
                    note.reminder = null
                }



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


        //Część do notatki głosowej
        // in this method we are checking request
        // code with our result code.
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            // on below line we are checking if result code is ok
            if (resultCode == RESULT_OK && data != null) {

                // in that case we are extracting the
                // data from our array list
                val res: ArrayList<String> =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>

                // on below line we are setting data
                // to our output text view.
                binding.inputDesc.setText(
                    Objects.requireNonNull(res)[0]
                )
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

    private fun sendNotification() {
        val name = binding.inputTitle.toString()
        val desc = binding.inputDesc.toString()
        Log.d("Test", "Próba tworzenia notyfikacji")

        val intent = Intent(this,NoteActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_IMMUTABLE)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationChannel = NotificationChannel(channelID,name, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)

            Log.d("Test", "Próba tworzenia notyfikacji 2")

            builder = Notification.Builder(this,channelID)
                .setContentTitle(name)
                .setContentText(desc)
                .setSmallIcon(com.google.android.material.R.drawable.ic_clock_black_24dp)
                .setContentIntent(pendingIntent)
        } else {
            builder = Notification.Builder(this)
                .setContentTitle(name)
                .setContentText(desc)
                .setSmallIcon(com.google.android.material.R.drawable.ic_clock_black_24dp)
                .setContentIntent(pendingIntent)
        }

        notificationManager.notify(101,builder.build())
        Log.d("Test", notificationChannel.id.toString())
    }

    private fun formatterReminder(calendar: Calendar) {
        val formatter = SimpleDateFormat("hh:mm dd.MM.yyyy", Locale.UK)
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
            val note = Note( null,title,desc, " ", false, notification,"13 gru, 2022 21:00")
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

    private fun archiviseNote(note: Note) {

        runOnUiThread { repository.update(note) }
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

