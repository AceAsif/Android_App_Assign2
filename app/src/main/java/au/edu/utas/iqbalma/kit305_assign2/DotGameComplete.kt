package au.edu.utas.iqbalma.kit305_assign2

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import au.edu.utas.iqbalma.kit305_assign2.databinding.ActivityDotGameCompleteBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class DotGameComplete : AppCompatActivity() {
    private lateinit var ui:ActivityDotGameCompleteBinding //This is for using viewBinding

    lateinit var prefer: SharedPreferences //This is for getting the shared
    lateinit var ImageUri: Uri //This is for the image

    private var GALLERY_REQUEST_CODE = 2
    //Camera setting step 5
    private val getPermissionResult = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result : Boolean ->
        if (result) {
            // Permission is granted.
            takeAPicture()
        } else {
            Toast.makeText(this, "Cannot access camera, permission denied", Toast.LENGTH_LONG).show()
        }
    }

    //Camera setting step 6, part 2
    private val getCameraResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { result: Boolean  ->
        //Camera setting step 7, part 1
        if (result)
        {
            setPic(ui.userPic)
        }
    }

    //Getting the Gallery setting permission from the user.
    private val getPermissionResultGar = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result : Boolean ->
        if (result) {
            // Permission is granted.
            selectImage()
        } else {
            Toast.makeText(this, "Cannot access gallery, permission denied", Toast.LENGTH_LONG).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityDotGameCompleteBinding.inflate(layoutInflater)
        setContentView(ui.root)

        /*These codes are for the shared preference*/
        //This is for getting the share Preferences
        prefer = getSharedPreferences(Name_Preference, MODE_PRIVATE)
        //The username variable is created to show the stored username preference
        val userName = prefer.getString("STRING_KEY","")
        ui.lblUsername.text = userName //This is used to display the name in the textView

        /*These are getting the number of repetitions from the setting page using shared preference*/
        prefer = getSharedPreferences(Rep_Preference, MODE_PRIVATE)
        val repNum = prefer.getInt("NUM_REP",2)

        /*These are getting the number of dots from the setting page using shared preference*/
        prefer = getSharedPreferences(Time_Preference, MODE_PRIVATE)
        val timeLimit = prefer.getInt("TIME_LIMIT", 60)
        //Log.d("Time_TAG", "Got time limit from setting page: $timeLimit")

        /*This is for setting the number of repetitions*/
        ui.totalRepView.text = "Total Repetitions: $repNum"

        val gameDuration = intent.getLongExtra("Duration",60)
        ui.gameDuration.text = "Durations: ${gameDuration}s"
        Log.d("TAG_intent", "The value of game duration is $gameDuration")

        val totalBut = intent.getIntExtra("TotalButtonPressed",1)
        ui.textButtons.text = "Total buttons pressed: $totalBut"
        Log.d("TAG_intent2", "Value of Total buttons pressed is $totalBut")

        //This sets the time limit using the shared preference value of time limit
        when(timeLimit){
            60-> ui.timeLimitView.text = "Time limit: 1 min"
            120-> ui.timeLimitView.text = "Time limit: 2 mins"
            300-> ui.timeLimitView.text = "Time limit: 5 mins"
            600-> ui.timeLimitView.text = "Time limit:10 mins"
            900-> ui.timeLimitView.text = "Time limit: 15 mins"
        }

        //This allows the user to play the game again.
        ui.retryBut.setOnClickListener {
            val retry = Intent(this, DotGame::class.java)
            startActivity(retry)
        }

        //This allows the user to take a photo using the camera
        ui.cameraBut.setOnClickListener {
            requestToTakeAPicture()
            //selectImage()
        }

        //This allows the user to select a picture from the phone's gallery
        ui.btnGallery.setOnClickListener {
            requestToOpenGallery()
            //selectImage()
        }

        //This is for the quit button
        ui.quit.setOnClickListener {
            var builder = AlertDialog.Builder(ui.quit.context)
            with(builder){
                setTitle("Alert!") //Sets the title for the alert message after clicking button
                setMessage("Do you want to quit?") //sets the message fro the alert message after clicking button
                setIcon(au.edu.utas.iqbalma.kit305_assign2.R.drawable.ic_baseline_warning_24) //sets the icon for the alert message after clicking button
                setCancelable(false) //stops the user from interacting with the screen if he presses the quit button.
            }

            builder.setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, id ->
                    //Stops the game and sends the user to main page
                    val i = Intent(this, MainActivity::class.java)
                    startActivity(i)
                }) //If the user presses "Yes" then send the user to the main page.

            builder.setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, id ->

                }) //If the user presses "No" then keep the user in the current page.

            builder.show()

        }
    }

    //Camera Setting step 4
    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestToTakeAPicture()
    {
        getPermissionResult.launch(Manifest.permission.CAMERA)
    }

    //Camera setting step 6, part 1
    private fun takeAPicture() {
        val photoFile: File = createImageFile()!!
        val photoURI: Uri = FileProvider.getUriForFile(
            this,
            "au.edu.utas.iqbalma.kit305_assign2",
            photoFile
        )
        getCameraResult.launch(photoURI)
    }

    //Camera setting step 6, part 3
    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath

            //get db connection
            val db = Firebase.firestore
            val dotHistory = db.collection("dot game history")

            dotHistory.document(databaseID)
                .update("userPicture","$currentPhotoPath")
                .addOnSuccessListener {
                    Log.d("TAG_database_log", "file stored");
                }.addOnFailureListener {
                    Log.d("TAG_database_log", "not stored");
                }
        }
    }

    /**Got help from these sources:
     * Setting permission for gallery: https://www.youtube.com/watch?v=evI1UTL4RDE&t=1261s&ab_channel=Bersyte
     * Store image to database: https://www.youtube.com/watch?v=GmpD2DqQYVk&ab_channel=Foxandroid
     * startActivityForResult alternative code: https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative
     * Update a document in Firestore: https://stackoverflow.com/questions/56608046/update-a-document-in-firestore
     * */

    private fun requestToOpenGallery(){
        getPermissionResultGar.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun selectImage(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent,GALLERY_REQUEST_CODE)
    }


    //var filePath: String? = ""
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            //setPic(ui.userPic)
            ImageUri = data?.data!!

            val imageFile: File = createImageFile()!!
            val imageURI: Uri = FileProvider.getUriForFile(
                this,
                "au.edu.utas.iqbalma.kit305_assign2",
                imageFile
            )

            Log.d("TAG_B","The new filepath of image is $imageURI")

            ui.userPic.setImageURI(ImageUri)

            Log.d("TAG_Image","The filepath of gallery image is $ImageUri")
            //getImageFilePath(this,ImageUri)
            //Log.d("TAG_A","The value of filePath is $filePath")

        }
    }


   /* private fun uploadImage(){
        //get db connection
        val db = Firebase.firestore
        val dotHistory = db.collection("dot game history")

        dotHistory.document(databaseID)
            .update("userPicture","$filePath")
            .addOnSuccessListener {
                Log.d("TAG_database_log", "file stored");
            }.addOnFailureListener {
                Log.d("TAG_database_log", "not stored");
            }
    }*/


    //Camera setting step 7, part 2
    private fun setPic(imageView: ImageView) {
        // Get the dimensions of the View
        val targetW: Int = imageView.measuredWidth
        val targetH: Int = imageView.measuredHeight

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(currentPhotoPath, this)

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.max(1, Math.min(photoW / targetW, photoH / targetH))

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            imageView.setImageBitmap(bitmap)
        }
    }

}