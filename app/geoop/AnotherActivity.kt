package com.oryx.geoop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_another.*
import java.util.*
import android.provider.AlarmClock
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment.getExternalStoragePublicDirectory
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.FileProvider.getUriForFile
import androidx.core.app.ActivityCompat.startActivityForResult






lateinit var gmmIntentUri: Uri
const val REQUEST_IMAGE_CAPTURE = 1

class AnotherActivity : AppCompatActivity() {
    var TAKE_PHOTO_CODE = 0
    var count = 0

    val REQUEST_TAKE_PHOTO = 1
    var currentPhotoPath: String = ""
    private var cameraFilePath: String? = null

    private var task_id: Int = 0
    private var status_note: String = ""
    private var mobile: Long = 0

    override fun onBackPressed() {
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_another)


        // Here, we are making a folder named picFolder to store
        // pics taken by the camera using this application.
        /*       val dir =
            """${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/picFolder/"""
        val newdir = File(dir)
        newdir.mkdirs()
*/   //   save_local.setOnClickListener { captureFromCamera(this) }
        /*     save_local.setOnClickListener {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val targetFilename = "GeoOpp$timeStamp"

            val resolver = this.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, targetFilename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/PerracoLabs")
            }
            val locationForPhotos = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, Uri.withAppendedPath(locationForPhotos, targetFilename))
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }
        }   */

        //  Button capture = (Button) findViewById(R.id.btnCapture);
/*        save_local.setOnClickListener {

            // Here, the counter will be incremented each time, and the
            // picture taken by camera will be stored as 1.jpg,2.jpg
            // and likewise.
            count++;
            val file = dir+count+".jpg";
            val newfile = File(file);
            try {
                newfile.createNewFile();
            } catch (e: IOException ) {
            }

            val outputFileUri = Uri.fromFile(newfile);

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)

            startActivityForResult(cameraIntent, TAKE_PHOTO_CODE)
        }
*/

        //  val hours = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis()) as Int
        // val minutes = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis()) as Int
        //  val seconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) as Int

        //  val date = Date().time

        //  status_note = statusNote.text.toString()

        button.setOnClickListener {
            status_note = statusNote.text.toString()
            val formatedDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
            val formatedTime = SimpleDateFormat("HH:mm").format(Date())
            val DateTime = "$formatedDate  $formatedTime"
            appViewModel.updateStatusNoteById(this, task_id, status_note)
            appViewModel.updateLastUpdateById(this, task_id, DateTime)

            finish()
        }

        // https://developers.google.com/maps/documentation/urls/android-intents
        locationBtn.setOnClickListener {
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        alarmBtn.setOnClickListener {
            val i = Intent(AlarmClock.ACTION_SET_ALARM)
            startActivity(i)
        }

        dialBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:+$mobile")
            startActivity(intent)
        }


        whasappBtn.setOnClickListener {
            val sendIntent = Intent("android.intent.action.MAIN").apply {
                setPackage("com.whatsapp")
                type = "text/plain"
                action = Intent.ACTION_SEND
                putExtra("jid", "$mobile@s.whatsapp.net")
                putExtra(Intent.EXTRA_TEXT, "Hi, we are sending you frm XYZ company.")
            }
            try {
                startActivity(sendIntent)
            } catch (e: Exception) {
                Toast.makeText(this, "WhatsApp is not installed in your phone", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }

        gwhatsapp.setOnClickListener {
            val group = "9RXnMJZo2j4J2P4UjO54lo"
            val sendIntent = Intent("android.intent.action.MAIN").apply {
                setPackage("com.whatsapp")
                type = "text/plain"
                action = Intent.ACTION_SEND
                putExtra("jid", "$group@s.whatsapp.net")
                putExtra(Intent.EXTRA_TEXT, "Hi, we are sending you frm XYZ company.")
            }
            try {
                startActivity(sendIntent)
            } catch (e: Exception) {
                Toast.makeText(this, "WhatsApp is not installed in your phone", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.status_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            status.adapter = adapter
        }

        status.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                arg0: AdapterView<*>, arg1: View,
                arg2: Int, arg3: Long
            ) {
                when (arg2) {
                    1 -> appViewModel.update(this@AnotherActivity, task_id, "On the way")
                    2 -> appViewModel.update(this@AnotherActivity, task_id, "Started")
                    3 -> appViewModel.update(this@AnotherActivity, task_id, "On Hold")
                    4 -> appViewModel.update(this@AnotherActivity, task_id, "Stuck")
                    5 -> appViewModel.update(this@AnotherActivity, task_id, "Cancelled")
                    6 -> appViewModel.update(this@AnotherActivity, task_id, "Completed")
                    else -> appViewModel.update(this@AnotherActivity, task_id, "New")
                }
                val formatedDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
                val formatedTime = SimpleDateFormat("HH:mm").format(Date())
                val DateTime = "$formatedDate  $formatedTime"
                // Calendar.getInstance().time.toString()
                appViewModel.updateLastUpdateById(this@AnotherActivity, task_id, DateTime)

            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }

    public override fun onStart() {
        super.onStart()

        val statusSelection =
            arrayOf("New", "On the way", "Started", "On Hold", "Stuck", "Cancelled", "Completed")

        textView.text = Html.fromHtml(intent.getStringExtra("text")!!, 0)
      //  textView.text = intent.getStringExtra("text")!!
        val longitude = intent.getStringExtra("lon")!!.toDouble()
        val latitude = intent.getStringExtra("lat")!!.toDouble()

        val customer = intent.getStringExtra("customer")!!
        mobile = intent.getStringExtra("mobile")!!.toLong()
        // val text = "Task <a href=\"https://www.google.com/maps/search/?api=1&query=$lon,$lat\">location</a>"
        // taskLocation.text = Html.fromHtml(text,0)
        // taskLocation.movementMethod = LinkMovementMethod.getInstance()

        // Creates an Intent that will load a map of San Francisco
        // gmmIntentUri = Uri.parse("geo:$lon,$lat")
        gmmIntentUri = Uri.parse("geo:0,0?q=$latitude,$longitude")

        // gmmIntentUri = Uri.parse("google.streetview:cbll=$lat,$lon")

        task_id = intent.getStringExtra("task_id")!!.toInt()
        status_note = intent.getStringExtra("statusNote")!!
        status.setSelection(statusSelection.indexOf(intent.getStringExtra("status")!!))
        statusNote.text.clear()
        statusNote.append(status_note)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val dir =
       """${getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/geoOp/"""
       val storageDir = File(dir)
        storageDir.mkdirs()

     //   val storageDir: File =
     //       getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            val currentPhotoPath = absolutePath
        }

    }

    private fun captureFromCamera(context: Context) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(context.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.oryx.geoop.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result code is RESULT_OK only if the user captures an Image
        if (resultCode == Activity.RESULT_OK && resultCode == RESULT_OK) {
            Toast.makeText(this, "pic saved", Toast.LENGTH_SHORT).show()
            println("pic saved")
        }
    }
}
