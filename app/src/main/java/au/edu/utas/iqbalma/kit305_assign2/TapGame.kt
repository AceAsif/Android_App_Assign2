package au.edu.utas.iqbalma.kit305_assign2

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import au.edu.utas.iqbalma.kit305_assign2.databinding.ActivityTapGameBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

const val TAP_HISTORY_INDEX = "Tap_History_Index"
val itemsTap = mutableListOf<TapGameData>()
var tapDatabaseID = "" //This is for the id for data being stored in database


class TapGame : AppCompatActivity() {
    private lateinit var ui : ActivityTapGameBinding

    lateinit var preferences: SharedPreferences //This is for getting the shared preference from MainActivity
    lateinit var clockTimer: CountDownTimer //This is for the timer of the game

    private val start_time_in_millis = (60 * 1000).toLong() //60 is 1 min and 1000 is for converting 1 min to milliseconds. This is for creating the time in the timer
    var timeRemain: Long = 0 //This variable is created for the timer function and to show the time remaining
    var clockRunning = false
    var startTime = "" //This is the start time of the game
    var endTime = "" //This is the end time of the game
    var diffStart: LocalDateTime? = null //This is for storing the start time in LocalTime format instead of string and using it other place
    var differ: Long = 0 //This is the difference between the start time and end time to calculate the duration of the game
    var count = 0
    var totalTap = 10
    var speedDescribe = "normal"
    var speed: Long = 0
    var gameMode = "Goal Mode"
    var gameOver = "something" //This is for saying that the user completed the game or not
    var gameStart = false
    var tapCount = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityTapGameBinding.inflate(layoutInflater)
        setContentView(ui.root)

        /*Some default settings*/
        ui.redButton.isEnabled = false //This stops the user from being able to press the button
        ui.blinkButton.isEnabled = false
        ui.radioNormal.isEnabled = false

        /**Got help from these sources.
         * button text color: https://www.tutorialkart.com/kotlin-android/kotlin-android-button-text-color/
         * button text size: https://www.tutorialkart.com/kotlin-android/kotlin-android-button-text-size/
         * */
        //ui.blinkButton.setTextColor(Color.MAGENTA) //SET CUSTOM COLOR
        ui.blinkButton.setTextColor(Color.WHITE) //SET CUSTOM COLOR
        ui.blinkButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30F)

        /*These codes are for the shared preference*/
        //This is for getting the share Preferences
        preferences = getSharedPreferences(Name_Preference, MODE_PRIVATE)
        //The username variable is created to show the stored username preference
        val userName = preferences.getString("STRING_KEY","")
        ui.lblEnteredText2.text = userName //This is used to display the name in the textView

        ui.textTimer.visibility = View.INVISIBLE //This hides the timer text from user if the Time mode is disabled
        ui.switchTime.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                // if the toggle button is enabled
                gameMode = "Time Mode"
                ui.radioSlow.isEnabled = false
                ui.radioNormal.isEnabled = false
                resetGame()
                Toast.makeText(this, "Time Mode: On", Toast.LENGTH_SHORT).show ()
            }
            else{
                gameMode = "Goal Mode"
                ui.radioSlow.isEnabled = true
                ui.radioNormal.isEnabled = true
                resetGame()
                /*if(gameStart && gameMode == "Time Mode"){
                    pauseClock() //This pauses the clock when the user presses the 'quit' button.
                    timeRemain = 0
                }*/
                Toast.makeText(this, "Time Mode: off", Toast.LENGTH_SHORT) .show ()
            }
        }

        ui.startButton.setOnClickListener { view ->
            /**Got help from this source
             * Date and Time in Kotlin: https://www.ictdemy.com/kotlin/oop/date-and-time-in-kotlin-creating-and-formatting
             * These are used for getting the start time and end time of the game
             * */
            val dateTime  = LocalDateTime.now() //This gets the current date and time of the real world.
            //ofLocalizedDateTime() - Formats to a local date and time format. It takes two parameters - the date style and the time style. We can choose anything from the full format to the short format, this applies for all of the methods except for ofPattern().
            startTime = dateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.MEDIUM))
            diffStart = dateTime
            Log.d("TAG_StartTime", "This is the start time of the player: $startTime")

            gameStart = true
            ui.redButton.isEnabled = true
            ui.blinkButton.isEnabled = true
            handleButtonClick(view)
            /*if(clockRunning){
                pauseClock()
            }
            else{
                startClock()
            }*/
            if(gameMode == "Time Mode"){
                //timeRemain = (60 * 1000).toLong() //This is for creating the time in the timer
                startClock()
                ui.radioFast.isEnabled = false
            }
            blinkAnimation()
            ui.startButton.isClickable = false

        }


        //This is for the quit button
        ui.quitButton.setOnClickListener {
            val builder = AlertDialog.Builder(ui.quitButton.context)
            with(builder){
                setTitle("Alert!") //Sets the title for the alert message after clicking button
                setMessage("Do you want to quit?") //sets the message fro the alert message after clicking button
                setIcon(R.drawable.ic_baseline_warning_24) //sets the icon for the alert message after clicking button
                setCancelable(false) //stops the user from interacting with the screen if he presses the quit button.
            }

            if(gameStart && gameMode == "Time Mode"){
                pauseClock() //This pauses the clock when the user presses the 'quit' button.
            }
            //Source: https://stackoverflow.com/questions/4112599/how-to-stop-an-animation-cancel-does-not-work
            ui.blinkButton.clearAnimation() //This stops the animation

            builder.setPositiveButton("Yes",
                DialogInterface.OnClickListener { _, id ->
                    //Stops the game and sends the user to main page
                    /*val i = Intent(this, MainActivity::class.java)
                    startActivity(i)*/
                    //gameStart = false
                    gameOver = "Incomplete"

                    if(gameStart && tapCount >0){
                        //These parts are for getting the duration of the game
                        val dateTime2  = LocalDateTime.now()
                        endTime = dateTime2.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.MEDIUM))
                        differ = Duration.between(diffStart, dateTime2).seconds

                        Log.d("TAG_EndTime", "This is the end time of the player: $endTime")
                        Log.d("TAG_Dur", "The duration is $differ")
                    }

                    gameCompletePage()
                }) //If the user presses "Yes" then send the user to the main page.

            builder.setNegativeButton("No",
                DialogInterface.OnClickListener { _, id ->
                    blinkAnimation()
                }) //If the user presses "No" then keep the user in the current page.
            builder.show()
        }

        //This is to tell the users that they are pressing the black dot which is wrong.
        ui.blinkButton.setOnClickListener {
            Toast.makeText(this, "Click Red Dot!! Why you press black??", Toast.LENGTH_SHORT) .show ()
        }

        ui.redButton.setOnClickListener {view->
            handleButtonClick(view)
            count += 1
            if(gameMode == "Goal Mode"){
                ui.textTapCounter.text = "Number of Taps: $count out of $totalTap"
            }
            else{
                ui.textTapCounter.text = "Number of Taps: $count"
                Log.d("TAG_Mode3","This is the $gameMode mode")
            }
        }
    }

    fun gameCompletePage(){
        if(gameStart && tapCount > 0){
            storeToDatabase()
        }else{
            tapCount = 0
        }

        val complete = Intent(this, TapGameComplete::class.java)
        complete.putExtra("GameMode", gameMode)
        complete.putExtra("StartTime", startTime)
        complete.putExtra("EndTime", endTime)
        complete.putExtra("Duration", differ)
        complete.putExtra("TotalButtonTap", tapCount)
        startActivity(complete)
    }

    fun blinkAnimation(){
        /**I got help from this source.
         * Animation help: https://stackoverflow.com/questions/43705249/how-to-make-a-button-blink-in-android
         * */
        val anim: Animation = AlphaAnimation(0.0f, 1.0f)
        //800 = slow , 400 = normal, 90 = fast. This is for the duration of the blink animation.
        anim.duration = speed //You can manage the blinking time with this parameter
        anim.startOffset = 20
        anim.repeatMode = Animation.REVERSE  //This repeats the blinking
        anim.repeatCount = Animation.INFINITE //This repeats the blinking for the infinite amount of time
        ui.blinkButton.startAnimation(anim) // This starts the animation
        Log.d("TAG_Speed1", "Blink rate speed: $speedDescribe")
        Log.d("TAG_Speed2", "anim duration: $speed")
    }

    /** The sources used for getting help for the timer are:
     * Kotlin documentation: https://developer.android.com/reference/kotlin/android/os/CountDownTimer
     * How to create a simple countdown timer in Kotlin?: https://stackoverflow.com/questions/54095875/how-to-create-a-simple-countdown-timer-in-kotlin
     * Counter time converter from minutes to milliseconds: https://www.inchcalculator.com/convert/minute-to-millisecond
     * */
    /*This function is used to start the timer of the game*/
    private fun startClock(){
        clockTimer = object : CountDownTimer(timeRemain, 1000) {
            //onTick(millisUntilFinished: Long) = Callback fired on regular interval.
            override fun onTick(millisUntilFinished: Long) {
                timeRemain = millisUntilFinished
                updateClockInfo() //Calls the updateClockInfo() function created by me
            }
            //onFinish() = Callback fired when the time is up.
            @RequiresApi(Build.VERSION_CODES.O)
            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                //clockRunning = false
                ui.textTimer.text = "Time: 0 : 00 s"
                gameOver = "Complete"
                //These parts are for getting the duration of the game
                val dateTime2  = LocalDateTime.now()
                endTime = dateTime2.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.MEDIUM))
                differ = Duration.between(diffStart, dateTime2).seconds

                Log.d("TAG_EndTime", "This is the end time of the player: $endTime")
                Log.d("TAG_Dur", "The duration is $differ")
                gameCompletePage()
            }
        }.start() //This function Start the countdown.
        clockRunning = true
    }

    /**Got help from this sources:
     * Minutes and second conversion from milliseconds: https://www.programiz.com/kotlin-programming/examples/milliseconds-minutes-seconds
     * This function is for updating the timer number for the remaining time
     * */
    @SuppressLint("SetTextI18n")
    private fun updateClockInfo() {
        val secs = ((timeRemain / 1000) % 60).toInt()  //This is for converting the time into seconds
        val mins = ((timeRemain / 1000) / 60).toInt()  //This is for converting the time into minutes

        //This if else statement used to show double digits for seconds in the timer.
        if(secs < 10 ){
            ui.textTimer.text = "Time: $mins : 0$secs s"
        }
        else {
            ui.textTimer.text = "Time: $mins : $secs s"
        }
    }

    /*This function is used for pausing the timer of the game*/
    private fun pauseClock(){
        clockTimer.cancel() //This function Cancel the countdown.
        //clockRunning = false
    }

    /**Got help from these source
     * Reset idea: https://medium.com/@olajhidey/working-with-countdown-timer-in-android-studio-using-kotlin-39fd7826e205
    * */
    private fun resetClock(){
        Log.d("TAG_Check13","value of clockRunning: $clockRunning")
        if (clockRunning){
            pauseClock()
        }
        timeRemain = start_time_in_millis
        updateClockInfo()

        Log.d("TAG_Check","resetClock() is called")
        Log.d("TAG_Check","The value of gameMode is $gameMode")
        Log.d("TAG_Check","Value of timeRemain is $timeRemain")
    }

    fun resetGame(){
        resetClock()
        ui.startButton.isClickable = true
        gameStart = false
        count = 0
        ui.redButton.isEnabled = false //This stops the user from being able to press the button
        //Source: https://stackoverflow.com/questions/4112599/how-to-stop-an-animation-cancel-does-not-work
        ui.blinkButton.clearAnimation() //This stops the animation

        if(gameMode == "Goal Mode"){
            ui.radioSlow.isClickable = true
            ui.radioNormal.isClickable = true
            ui.textTimer.visibility = View.INVISIBLE
            ui.textTimer.text = "Time: 1:00 min"
            ui.textTapCounter.text = "Number of Taps: $count out of $totalTap"
            ui.tapDescription.text = "Tap the red dot 10 times with the same frequency at which the black dot blinks. Press the Start button to start the game."
        }
        else{//This is the "Time Mode"
            ui.radioSlow.isClickable = false
            ui.radioNormal.isClickable = false
            ui.radioFast.isChecked = true
            speedDescribe = "fast"
            ui.textTimer.visibility = View.VISIBLE
            ui.textTimer.text = "Time: 1:00 min"
            ui.textTapCounter.text = "Number of Taps: $count"
            ui.tapDescription.text = "Within 1 minute, tap the red dot as many times as possible. To initiate time mode, click the Start button. To remind you to tap fast, the blink rate will be set to Fast."
            Log.d("TAG_Mode3","This is the $gameMode mode")
        }

        Toast.makeText(this, "Click Start Button to apply change", Toast.LENGTH_SHORT).show()
    }

    /**I got help from this source.
     * Radio button index: https://stackoverflow.com/questions/6440259/how-to-get-the-selected-index-of-a-radiogroup-in-android?fbclid=IwAR2x3VN1ZwdxHAoV_Cr87AIrNz-fjdtXOs-Y_jusIcZtdHBxQuzYEfWCF6Q
     * Description of the function: This function is for getting the number of dots for the game
     * */
    fun onRadioButtonClicked(view: View){
        val radioDotID: Int = ui.radioSpeed.checkedRadioButtonId //This gets the id of the radio button clicked by the user.
        val selectedButton: View = ui.radioSpeed.findViewById(radioDotID) //This gets the view for the radio button clicked by the user.
        var numSpeed = ui.radioSpeed.indexOfChild(selectedButton) //I added 2 because the index always starts from 0. So by adding 2, I can get the correct value

        //the when statement is used to make the radio button values into string instead of index number
        speedDescribe = when(numSpeed){
            1-> "normal"
            2-> "fast"
            else-> "slow"
        }
        if(gameMode == "Goal Mode"){
            when(numSpeed){
                0->{
                    ui.radioSlow.isEnabled = false
                    ui.radioNormal.isEnabled = true
                    ui.radioFast.isEnabled = true
                }
                1->{
                    ui.radioNormal.isEnabled = false
                    ui.radioSlow.isEnabled = true
                    ui.radioFast.isEnabled = true
                }
                2->{
                    ui.radioFast.isEnabled = false
                    ui.radioSlow.isEnabled = true
                    ui.radioNormal.isEnabled = true
                }
            }
        }

        Log.d("TAG_Self","The current position of blink rate: $numSpeed")

        resetGame() //This resets the game and tells the user to press the Start button to apply the new changes
        Log.d("TAG_Speed", "Blink rate speed: $speedDescribe")

    }

    /**The source that I got help from
     * Button check: https://stackoverflow.com/questions/63570210/kotlin-check-which-button-was-clicked
     * Description of the function: This function is for getting the number of button clicks
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun handleButtonClick(view: View){
        with(view as Button){

        }
        Log.d("Just_Tag1", "$count")

        tapCount = count //tapCount was created because count starts from 0 but we want to count from 1. Therefore, tapCount stores the count value with plus (+) 1.
        when(gameStart){
            true->{
                tapCount += 1
            }
            else-> tapCount
        }
        Log.d("TAG_Check","The value of gameStart: $gameStart")
        Log.d("TAG_count","The value of tapCount: $tapCount")

        Log.d("TAG_Mode","This is the $gameMode mode")
        if(gameMode == "Goal Mode"){
            Log.d("TAG_Mode","This is the $gameMode mode")
            if (count == (totalTap - 1)){
                gameOver = "Complete"
                //These parts are for getting the duration of the game
                val dateTime2  = LocalDateTime.now()
                endTime = dateTime2.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.MEDIUM))
                differ = Duration.between(diffStart, dateTime2).seconds

                Log.d("TAG_EndTime", "This is the end time of the player: $endTime")
                Log.d("TAG_Dur", "The duration is $differ")

                ui.redButton.isEnabled = false //This stops the user from being able to press the button
                ui.blinkButton.clearAnimation() //This stops the animation

                ui.textTapCounter.text = "Number of Taps: $count out of $totalTap"
                gameCompletePage()
            }
        }
        else{

            Log.d("TAG_Mode","This is the $gameMode mode")
        }

        //This is for loading the user choice for button size from shared Preference
        when(speedDescribe){
            //800 = slow , 400 = normal, 90 = fast. This is for the duration of the blink animation.
            "slow"->{
                ui.radioSlow.isChecked = true
                speed = 800
                Log.d("TAG_Speed3", "Blink rate speed: $speed")
            }
            "normal"->{
                ui.radioNormal.isChecked = true
                speed = 400
                Log.d("TAG_Speed4", "Blink rate speed: $speed")
            }
            "fast"->{
                ui.radioFast.isChecked = true
                speed = 90
                Log.d("TAG_Speed5", "Blink rate speed: $speed")
            }
            else->Log.d("TAG_Size3", "Just showing the default selection for dots")
        }

        when(count){
            50->ui.blinkButton.text = "try harder"
            100->ui.blinkButton.text = "Not bad"
            150->ui.blinkButton.text = "Good effort"
            200->ui.blinkButton.text = "Awesome"
            250->ui.blinkButton.text = "Great"
            300->ui.blinkButton.text = "Best"
            350->ui.blinkButton.text = "Excellent"
            400->ui.blinkButton.text = "OMG!!"
            500->ui.blinkButton.text = "Superb"
            600->ui.blinkButton.text = "Super Saiyan"
            999->ui.blinkButton.text = "Insane Speed"
            else->ui.blinkButton.text = ""
        }
    }

    //This is for storing data into the database from the game
    fun storeToDatabase(){
        //SimpleDateFormat source: https://developer.android.com/reference/kotlin/java/text/SimpleDateFormat
        tapDatabaseID = SimpleDateFormat("MMddyyyyHHmmss").format(Date())
        Log.d("TAG_IDData","This is the value of databaseID: $tapDatabaseID")

        //get db connection
        val db = Firebase.firestore
        val dotHistory = db.collection("tap game history") //Create a collection if it doesn't exist

        val attempt = TapGameData(
            id = tapDatabaseID,
            gameStatus = gameOver,
            gameModeTap = gameMode,
            startTimeTap = startTime,
            endTimeTap = endTime,
            durationOfGameTap = differ,
            totalButtonTap = tapCount
            //totalButtonTap =
        )

        dotHistory.document(tapDatabaseID)
            .set(attempt)
            .addOnSuccessListener {
                Log.d(FIREBASE_TAG, "Document created with id ${attempt.id}")
            }
            .addOnFailureListener {
                Log.e(FIREBASE_TAG, "Error writing document", it)
            }
    }

}