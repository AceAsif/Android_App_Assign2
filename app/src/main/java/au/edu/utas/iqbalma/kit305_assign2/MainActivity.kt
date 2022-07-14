package au.edu.utas.iqbalma.kit305_assign2

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import au.edu.utas.iqbalma.kit305_assign2.databinding.ActivityMainBinding

const val Name_Preference = "UsernameFile"
const val REQUEST_IMAGE_CAPTURE = 1 //This is for the camera permission

class MainActivity : AppCompatActivity() {
    private lateinit var ui : ActivityMainBinding //This is for using viewBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityMainBinding.inflate(layoutInflater)
        setContentView(ui.root)

        loadData() //This calls the loadData function so that the user name is loaded when the user reloads the app.

        //This is for the Setting button which sends the user to the setting page.
        ui.settingBut.setOnClickListener {
            val i = Intent(this, SettingPage::class.java)
            startActivity(i)
        }

        //This is for the Start button which sends the user to the main game page.
        ui.startBut.setOnClickListener {
            val i = Intent(this, DotGame::class.java)
            startActivity(i)
        }

        //This is for the History button which sends the user to the History page which contains "Dot Game History", "Free to Play History" and "Tap Game History"
        ui.historyBut.setOnClickListener {
            val i = Intent(this, HistoryPage::class.java)
            startActivity(i)
        }

        //This is for the Setting button which sends the user to the setting page.
        ui.freeToPlayBut.setOnClickListener {
            val i = Intent(this, FreeToPlay::class.java)
            startActivity(i)
        }

        //This is for the Setting button which sends the user to the setting page.
        ui.tapGameBut.setOnClickListener {
            val i = Intent(this, TapGame::class.java)
            startActivity(i)
        }


        /** Got help from these sources
         * Enter button handler: https://www.youtube.com/watch?v=kJsloC84W0o&ab_channel=Nosware
         * For setting text for editText: https://stackoverflow.com/questions/44493908/setting-text-in-edittext-kotlin
         **/
        /*This is for setting the username*/
        val userEditText = findViewById<EditText>(R.id.txtName)
        userEditText.setOnKeyListener(View.OnKeyListener { v, actionId, event ->
            if (actionId== KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP){
                saveData()
                return@OnKeyListener true
            }
            false
        })
    }

    /**
     * Got help for shared preference from: https://www.youtube.com/watch?v=S5uLAGnBvUY&list=PL5DdAktpEmHsbbQb1j9I14NVrypCBoR1a&index=15&ab_channel=CodePalace
    **/
    //This is used to save the user name using shared Preference
    private fun saveData() {
        val insertedText: String = ui.txtName.text.toString()
        ui.txtName.setText(insertedText) //This sets the text for the username editText field
        /**
         * getSharedPreferences() — Use this if you need multiple shared preference files identified by name, which you specify with the first parameter.
         * You can call this from any Context in your app.
        */
        val sharedPreferences = getSharedPreferences(Name_Preference, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        //apply() changes the in-memory SharedPreferences object immediately but writes the updates to disk asynchronously.
        editor.apply {
            putString("STRING_KEY", insertedText)
            Log.d("Name_tag1","It works for the shared Pre")
        }.apply()
        Toast.makeText(this, "Name Saved", Toast.LENGTH_SHORT).show()
    }

    //This is used to load the share preference data after the user reopens the app.
    private fun loadData(){
        val sharedPreferences = getSharedPreferences(Name_Preference, MODE_PRIVATE)
        val savedString = sharedPreferences.getString("STRING_KEY", null)
        ui.txtName.setText(savedString)
    }
}