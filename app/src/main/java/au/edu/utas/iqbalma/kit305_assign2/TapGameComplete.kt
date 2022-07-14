package au.edu.utas.iqbalma.kit305_assign2

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import au.edu.utas.iqbalma.kit305_assign2.databinding.ActivityTapGameBinding
import au.edu.utas.iqbalma.kit305_assign2.databinding.ActivityTapGameCompleteBinding

class TapGameComplete : AppCompatActivity() {
    private lateinit var ui : ActivityTapGameCompleteBinding

    lateinit var preferences: SharedPreferences //This is for getting the shared preference from MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityTapGameCompleteBinding.inflate(layoutInflater)
        setContentView(ui.root)

        /*These codes are for the shared preference*/
        //This is for getting the share Preferences
        preferences = getSharedPreferences(Name_Preference, MODE_PRIVATE)
        //The username variable is created to show the stored username preference
        val userName = preferences.getString("STRING_KEY","")
        ui.lblUsername2.text = userName //This is used to display the name in the textView

        /*This is for getting the data from the previous page directly*/
        val startTimeTap = intent.getStringExtra("StartTime")
        ui.gameStart.text = "Start Time: $startTimeTap"

        val endTimeTap = intent.getStringExtra("EndTime")
        ui.gameEnd.text = "End Time: $endTimeTap"

        val gameModeTap = intent.getStringExtra("GameMode")
        ui.gameMode.text = "Game Mode: $gameModeTap"
        Log.d("TAG_intent", "The value of Game Mode: $gameModeTap")

        val totalBut = intent.getIntExtra("TotalButtonTap",1)
        ui.textButtonsTap.text = "Total number of taps: $totalBut"
        Log.d("TAG_intent2", "Value of Total buttons pressed is $totalBut")

        val gameDuration = intent.getLongExtra("Duration",60)
        ui.gameDurationTap.text = "Duration: ${gameDuration}s"
        Log.d("TAG_intent", "The value of game duration is $gameDuration")

        //Showing the relevant information for each game mode
        if (gameModeTap == "Goal Mode"){
            ui.gameDurationTap.visibility = View.VISIBLE
            ui.textButtonsTap.visibility = View.INVISIBLE

        }

        //This allows the user to play the game again.
        ui.retryBut2.setOnClickListener {
            val retry = Intent(this, TapGame::class.java)
            startActivity(retry)
        }

        //This is for the quit button
        ui.quit2.setOnClickListener {
            var builder = AlertDialog.Builder(ui.quit2.context)
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
}