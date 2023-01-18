package pl.notatki.activity


import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import pl.notatki.BuildConfig
import pl.notatki.R
import pl.notatki.adapter.NotificationReceiver
import pl.notatki.databinding.ActivityNoteBinding
import pl.notatki.model.Label
import pl.notatki.model.Note
import pl.notatki.model.Reminder
import pl.notatki.repository.NoteRepository
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context


class NoteActivity : AppCompatActivity(),EasyPermissions.PermissionCallbacks,EasyPermissions.RationaleCallbacks {


    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var builder: Notification.Builder
    private var channelID = "pl.notatki.activity"
    private var READ_STORAGE_PERM: Int = 123
    private var WRITE_STORAGE_PERM = 123
    private lateinit var binding: ActivityNoteBinding
    private val repository: NoteRepository by lazy { NoteRepository(applicationContext) }
    private var edit: Boolean = false
    private var selectedImg = ""

    private val REQUEST_CODE_PICK_IMAGE = 1     //Request code do wybierania obrazu z galerii
    private val REQUEST_CODE_SPEECH_INPUT = 2   //Request code do notatki głosowej
    private val REQUEST_CODE_IMAGE_CAPTURE = 3


    private var photoPath: String = "";

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
        val calendar = Calendar.getInstance()

        //createNotificationChannel()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val buttonNoteActivity = binding.returnButton
        buttonNoteActivity.setOnClickListener {
            val main = Intent(this, MainActivity::class.java)
            val message: String
            if (addNote(calendar) && !edit) {
                message = "Notatka została zapisana"
            } else {
                message = "Notatka nie została zapisana"
            }
            main.putExtra("info", message)
            startActivity(main)
            finish()
        }


        var labels = ArrayList<Label>()

        val buttonAddLabel = binding.buttonAddTags
        binding.buttonAddTags.setOnClickListener {

            showAddLabelDialog()

        }

        val buttonReminder = binding.buttonReminder
        val buttonAddReminder = binding.buttonAddReminder

        intent.extras?.getParcelable<Note>(EXTRAS_NOTE)?.let { note ->
            val reminder = note.reminder
            if (reminder != null) { //Sprawdza czy przypomnienie nie jest puste i na podstawie tego wyświetla je, albo nie
                if (reminder.timeReminder != "" || reminder.date != "" || reminder.location != "") {
                    buttonReminder.visibility = View.VISIBLE
                    buttonReminder.text = reminder.timeReminder +" "+ reminder.date

                    if (buttonReminder.visibility == View.VISIBLE) {
                        buttonAddReminder.text = "Przypomnienie-"

                    } else {
                        buttonAddReminder.text = "Przypomnienie+"
                    }
                }
            }

            if (note.archived == true) { //Przy ładowaniu sprawdza czy jest archiwizowana, żeby ustalić odpowiednią ikonę
                binding.archiveButton.setImageResource(R.drawable.ic_baseline_unarchive_24)
            } else {
                binding.archiveButton.setImageResource(R.drawable.ic_baseline_archive_24)
            }

        }

        //Ustawianie remindera i calendara na odpowiednie wartości
        val reminder = Reminder("", "", "")
        intent.extras?.getParcelable<Note>(EXTRAS_NOTE)?.let { note ->
            reminder.timeReminder = note.reminder?.timeReminder ?: ""
            reminder.date = note.reminder?.date ?: ""

            val formatter = SimpleDateFormat("HH:mm dd.MM.yyyy")

            var dateT = note.reminder?.timeReminder ?: ""
            var timeT = note.reminder?.date ?: ""
            var dateFull = dateT+" "+timeT

            Log.v("Testing", "dateFull =" + dateFull);

            //val pos = ParsePosition(0)+
            //val stringDate: Date = formatter.parse(dateFull, pos)

            if( dateT != "" || timeT != ""){
                calendar.setTime(formatter.parse(dateFull))
            }
        }

        //Wybieranie czasu
        val timePicker = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            formatterReminder(calendar)

            val formatterTime = SimpleDateFormat("HH:mm", Locale.ENGLISH)
            reminder.timeReminder = formatterTime.format(calendar.time)
        }

        val c = Calendar.getInstance() //Pobiera obecny czas

        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.YEAR, year)

            val formatterDate = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
            reminder.date = formatterDate.format(calendar.time)
            formatterReminder(calendar)
        }

        //Usuwanie powiadomienia reminder
        buttonAddReminder.setOnClickListener {

            if (buttonReminder.visibility == View.GONE) {
                buttonReminder.visibility = View.VISIBLE
                buttonAddReminder.text = "Przypomnienie-"

            } else {
                buttonReminder.visibility = View.GONE
                buttonAddReminder.text = "Przypomnienie+"
                reminder.timeReminder = ""
                reminder.date = ""
            }
        }

        //Otwiera date i time pickery
        buttonReminder.setOnClickListener {
            DatePickerDialog(
                this, datePicker, c[Calendar.YEAR] , c[Calendar.MONTH] ,c[Calendar.DAY_OF_MONTH]
            )   .show()

            TimePickerDialog(
                this, timePicker, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        //Notatka głosowa
        binding.voiceButton.setOnClickListener {

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )


            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
            )

            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")


            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
            } catch (e: Exception) {

                Toast
                    .makeText(
                        this, " " + e.message,
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }
        }


        binding.imageButon.setOnClickListener { pickImg() }
        binding.photoButton.setOnClickListener { takePhoto() }

        binding.noteImg.visibility = View.GONE
        intent.extras?.getParcelable<Note>(EXTRAS_NOTE)?.let { note ->
            showData(note)
            edit = true

            binding.archiveButton.setOnClickListener {
                if (note.archived == false) {
                    note.archived = true;
                    binding.archiveButton.setImageResource(R.drawable.ic_baseline_unarchive_24)
                    updateNote(note)
                } else {
                    note.archived = false;
                    binding.archiveButton.setImageResource(R.drawable.ic_baseline_archive_24)
                    updateNote(note)
                }
            }

            binding.deleteButton.setOnClickListener {
                deleteNote(note)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            binding.returnButton.setOnClickListener {
                note.title = binding.inputTitle.text.toString()
                note.content = binding.inputDesc.text.toString()
                if (selectedImg == "") {
                    note.image = note.image.toString()
                } else {
                    note.image = selectedImg
                }

                if (reminder.timeReminder != "" && reminder.date != "") {
                    note.reminder = reminder

                    val formatter = SimpleDateFormat("HH:mm dd-MM-yyyy")

                    //function for Creating [Notification Channel][1]
                    createNotificationChannel();
                    //function for scheduling the notification
                    scheduleNotification(calendar);
                }

                if(reminder.timeReminder == "" || reminder.date == ""){
                    note.reminder = null
                }



                /*else {
                    note.reminder = null
                }*/

                updateNote(note)
                val main = Intent(this, MainActivity::class.java)
                startActivity(main)
                finish()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // sprawdzamy, czy wynik pochodzi od odpowiedniego intentu
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            // pobieramy URI obrazka z intentu
            val imageUri = data?.data
            if (imageUri != null) {
                // wczytujemy obrazek do pamięci
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

                // wyświetlamy obrazek w imageView
                binding.noteImg.setImageBitmap(bitmap)

                binding.noteImg.visibility = View.VISIBLE

                selectedImg = getPathImg(imageUri)!!
            }

        }
        if (requestCode == REQUEST_CODE_IMAGE_CAPTURE) {

            val bmOptions = BitmapFactory.Options()
            var bitmap = BitmapFactory.decodeFile(photoPath, bmOptions)
            if (bitmap != null) {
                binding.noteImg.setImageBitmap(bitmap)
                binding.noteImg.visibility = View.VISIBLE
                selectedImg = photoPath
            } else {
                Toast.makeText(this, "Nie wykonano zdjęcia", Toast.LENGTH_SHORT).show()
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
                val res: ArrayList<String?> =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String?>

                // on below line we are setting data
                // to our output text view.
                binding.inputDesc.setText(
                    Objects.requireNonNull(res)[0]
                )
            }
        }
    }

    private fun getPathImg(contentUri: Uri): String? {
        var path: String? = null
        val cursor: Cursor? = contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null) {
            path = contentUri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex("_data")
            path = cursor.getString(index)
            cursor.close()

        }
        return path
    }

    fun createNotificationChannel() {
        val id = "channelID"
        val name = "Daily Alerts"
        val des = "Channel Description A Brief"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(id, name, importance)
        channel.description = des
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    fun scheduleNotification(calendar: Calendar) {
        val name = binding.inputTitle.text.toString()
        val desc = binding.inputDesc.text.toString()

        val intent = Intent(applicationContext, NotificationReceiver::class.java)
        intent.putExtra("titleExtra", name)
        intent.putExtra("textExtra", desc)
        intent.extras?.getParcelable<Note>(EXTRAS_NOTE)?.let { note ->
            intent.putExtra("notificationExtra", note.noteId!!)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        //Testowy czas. 5 sekund od utworzenia
        //val calendarTest = Calendar.getInstance()
        //calendarTest.add(Calendar.SECOND, 30)

        val date: Date = calendar.getTime()
        val longDate: Long = date.getTime()

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            longDate,
            pendingIntent
        )

        //alarmManager.cancel(pendingIntent);

        val format = SimpleDateFormat("HH:mm dd-MM-yyyy")
        var time = format.format(calendar.getTime())

        Toast.makeText(applicationContext, time, Toast.LENGTH_LONG).show()
    }

    //Działająca notyfikacja
    /*private fun sendNotification(calendar: Calendar) {
        val name = binding.inputTitle.text.toString()
        val desc = binding.inputDesc.text.toString()

        intent.extras?.getParcelable<Note>(EXTRAS_NOTE)?.let { note ->
            val reminder = note.reminder

            //val hour = reminder?.timeReminder.toString()
            //val date = reminder?.date.toString()

            var notificationID = 101
            notificationID = note.noteId!!

            val intent = Intent(this, MainActivity::class.java)
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel =
                    NotificationChannel(channelID, name, NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(notificationChannel)

                builder = Notification.Builder(this, channelID)
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

            notificationManager.notify(notificationID, builder.build())
        }
    }*/

    private fun formatterReminder(calendar: Calendar) {
        val formatter = SimpleDateFormat("HH:mm dd-MM-yyyy")

        val buttonReminder = binding.buttonReminder
        buttonReminder.text = formatter.format(calendar.time)
        val notificationTime = formatter.format(calendar.time);

    }

    private fun addNote(calendar : Calendar) : Boolean{
        val title = binding.inputTitle.text.toString()
        val desc = binding.inputDesc.text.toString()
        var img = selectedImg


        val label = null

        //Dodawanie notyfikacji przy tworzeniu
        val reminder = Reminder("", "", "")
        val formatterTime = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        reminder.timeReminder = formatterTime.format(calendar.time)
        val formatterDate = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
        reminder.date = formatterDate.format(calendar.time)

        //function for Creating [Notification Channel][1]
        createNotificationChannel();
        //function for scheduling the notification
        scheduleNotification(calendar);

        if (validateNote(title, desc)){

            val note = Note( 0,title,desc, img, false, Label(null, "Test"),reminder,"13 gru, 2022 21:00")

            runOnUiThread { repository.insertNoteToDabase(note) }
            return true
        }
       return false
    }

    private fun validateNote(title: String, desc: String): Boolean {
        if (title.isEmpty()  && desc.isEmpty() && selectedImg == "" ){
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


        binding.inputTitle.setText(note.title)
        binding.inputDesc.setText(note.content)
        val bmOptions = BitmapFactory.Options()
        var bitmap = BitmapFactory.decodeFile(note.image, bmOptions)
        if (bitmap != null) {
            binding.noteImg.setImageBitmap(bitmap)
            binding.noteImg.visibility = View.VISIBLE
        }else{
            binding.noteImg.visibility = View.GONE
        }
    }

    private fun pickImg(){
        readStorageTask()
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_CODE_PICK_IMAGE)

    }
    private fun isReadStoragePermission() : Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    private fun isWriteStoragePermission() : Boolean {
                return EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }


    private fun writeStorageTask(){
        if(isWriteStoragePermission()){

        }else{
            EasyPermissions.requestPermissions(this,
                getString(R.string.storage_permission_text),
                WRITE_STORAGE_PERM ,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun readStorageTask(){
            if(isReadStoragePermission()){

                Toast.makeText(this, "przyznano dostęp",Toast.LENGTH_SHORT).show()
            }else{
                EasyPermissions.requestPermissions(this,
                    getString(R.string.storage_permission_text),
                            READ_STORAGE_PERM ,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            }
    }
    fun takePhoto() {
        writeStorageTask()
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show()
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_CODE_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("dd.MM.yyyy HH:mm").format(Date())
        val storageDir: File? = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return  File(
            storageDir,
            "$timeStamp.jpg"
        ).apply {
            photoPath = absolutePath
        }
    }

    private fun showAddLabelDialog() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        val labels = repository.getLabelsForDialog()
        for (label in labels) {
            val checkBox = CheckBox(this)
            checkBox.text = label.name
            layout.addView(checkBox)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Wybierz etykiety")
            .setView(layout)
            .setPositiveButton("OK") { _, _ ->
                val selectedLabels = ArrayList<Label>()
                for (i in 0 until layout.childCount) {
                    val checkBox = layout.getChildAt(i) as CheckBox
                    if (checkBox.isChecked) {
                        selectedLabels.add(labels[i])
                    }
                }
                showSelectedLabels(selectedLabels)


            }
            .setNegativeButton("ZAMKNIJ", null)
            .create()
        dialog.show()
    }
    private fun showSelectedLabels(labels: List<Label>){
        val layout = LinearLayout(this)

        layout.layoutParams  = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
         var params : LinearLayout.LayoutParams = layout.layoutParams as LinearLayout.LayoutParams
        params.setMargins(20,0,0,0)
        for (label in labels) {
            val textView = TextView(this)
            textView.text = label.name
            textView.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            textView.setPadding(8,8,8,8)
            textView.layoutParams = params
            layout.addView(textView)
        }

        binding.labelLayout.addView(layout)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    //Pobiera obecną datę
    private fun currentdate(): Date? {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, 0)
        return cal.time
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        TODO("Not yet implemented")
    }

    override fun onRationaleAccepted(requestCode: Int) {
        TODO("Not yet implemented")
    }

    override fun onRationaleDenied(requestCode: Int) {
        TODO("Not yet implemented")
    }


}










