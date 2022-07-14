package au.edu.utas.iqbalma.kit305_assign2

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import au.edu.utas.iqbalma.kit305_assign2.databinding.ActivityFreeToPlayBinding
import au.edu.utas.iqbalma.kit305_assign2.databinding.ActivityFreeToPlayCompleteBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FreeToPlayComplete : AppCompatActivity() {
    private lateinit var ui: ActivityFreeToPlayCompleteBinding

    lateinit var prefer: SharedPreferences //This is for getting the shared

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
            setPic(ui.userPicFree)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityFreeToPlayCompleteBinding.inflate(layoutInflater)
        setContentView(ui.root)

        /*These codes are for the shared preference*/
        //This is for getting the share Preferences
        prefer = getSharedPreferences(Name_Preference, MODE_PRIVATE)
        //The username variable is created to show the stored username preference
        val userName = prefer.getString("STRING_KEY","")
        ui.lblUsername.text = userName //This is used to display the name in the textView

        val gameDuration = intent.getLongExtra("Duration",60)
        ui.gameDurationFree.text = "Durations: ${gameDuration}s"
        Log.d("TAG_intent", "The value of game duration is $gameDuration")

        val totalBut = intent.getIntExtra("TotalButtonPressed",1)
        ui.textButtonsFree.text = "Total buttons pressed: $totalBut"
        Log.d("TAG_intent2", "Value of Total buttons pressed is $totalBut")

        //This allows the user to play the game again.
        ui.retryBut.setOnClickListener {
            val retry = Intent(this, FreeToPlay::class.java)
            startActivity(retry)
        }

        //This allows the user to take a photo using the camera
        ui.cameraBut.setOnClickListener {
            requestToTakeAPicture()
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
            val dotHistory = db.collection("free to play history")

            dotHistory.document(databaseID)
                .update("userPicture","$currentPhotoPath")
                .addOnSuccessListener {
                    Log.d("TAG_database_log", "file stored");
                }.addOnFailureListener {
                    Log.d("TAG_database_log", "not stored");
                }
        }
    }

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