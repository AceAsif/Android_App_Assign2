package au.edu.utas.iqbalma.kit305_assign2

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import au.edu.utas.iqbalma.kit305_assign2.databinding.ActivityFreeToPlayBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

const val FREE_HISTORY_INDEX = "Free_History_Index"
val freeItems = mutableListOf<GameData>()

@RequiresApi(Build.VERSION_CODES.O)
class FreeToPlay : AppCompatActivity() {
    private lateinit var ui: ActivityFreeToPlayBinding

    lateinit var preferences: SharedPreferences //This is for getting the shared preference from MainActivity

    var totalDot = 0 //This is the number of dots in the game and for storing the shared preference value and using it in other places
    var count = 1 //This helps to count the number of clicks by the user
    var indicateCount = 0  //This is for next button indication
    var repCount = 1 //This is for counting the number of repetition completed by user.
    var sizeBut = "small" //This is for storing button size from shared preference and using it in functions
    var startTime = "12:00 am" //This is the start time of the game
    var endTime = "5:00 am" //This is the end time of the game
    var totalButClick = 0
    var listButtons = mutableListOf<String>()
    var butRandom = false
    var diffStart: LocalDateTime? = null
    var indicateFeat = false
    var differ: Long = 0
    var quitGame = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityFreeToPlayBinding.inflate(layoutInflater)
        setContentView(ui.root)

        /**Got help from this source
         * Date and Time in Kotlin: https://www.ictdemy.com/kotlin/oop/date-and-time-in-kotlin-creating-and-formatting
         * These are used for getting the start time and end time of the game
         * */
        val dateTime  = LocalDateTime.now() //This gets the current date and time of the real world.
        //ofLocalizedDateTime() - Formats to a local date and time format. It takes two parameters - the date style and the time style. We can choose anything from the full format to the short format, this applies for all of the methods except for ofPattern().
        startTime = dateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.MEDIUM))
        diffStart = dateTime
        Log.d("TAG_StartTime", "This is the start time of the player: $startTime")

        Log.d("TAG_Quit","The value of quit: $quitGame")
        /*These codes are for the shared preference*/
        //This is for getting the share Preferences
        preferences = getSharedPreferences(Name_Preference, MODE_PRIVATE)
        //The username variable is created to show the stored username preference
        val userName = preferences.getString("STRING_KEY","")
        ui.lblEnteredTextFree.text = userName //This is used to display the name in the textView

        /*These are getting the number of dots from the setting page using shared preference*/
        preferences = getSharedPreferences(Dot_Preference, MODE_PRIVATE)
        val dotNum = preferences.getInt("NUM_DOT", 3)

        /*These are getting the number of repetitions from the setting page using shared preference*/
        /*preferences = getSharedPreferences(Rep_Preference, MODE_PRIVATE)
        val repNum = preferences.getInt("NUM_REP",2)*/

        /*These are getting the next button indication setting from the setting page using shared preference*/
        preferences = getSharedPreferences(Indication_Preference, MODE_PRIVATE)
        val indicate = preferences.getBoolean("INDICATION",false)
        //Log.d("TAG_Indicate", "The user turned on the indicate feature: $indicate")

        /*These are getting the next button indication setting from the setting page using shared preference*/
        preferences = getSharedPreferences(Random_Preference, MODE_PRIVATE)
        val randomisation = preferences.getBoolean("RANDOM",false)

        /*These are getting the next button indication setting from the setting page using shared preference*/
        preferences = getSharedPreferences(Size_Preference, MODE_PRIVATE)
        val butSize = preferences.getString("BUTTON_SIZE","")
        Log.d("TAG_SIZE", "The button size: $butSize")

        /*This is the progressbar*/
        ui.progressBar.max = dotNum
        ui.progressBar.progress = 0

        //The number of dots are saved into totalDot variable for using the if statement in handleButtonClick function
        totalDot = dotNum

        //The total number of repetitions are saved into numRep variable for using the if statement in gameComplete function
        //numRep = repNum

        butRandom = randomisation

        indicateFeat = indicate

        if (butSize != null) {
            sizeBut = butSize
        }

        when(butSize){
            "small"->{
                with(ui){
                    button.scaleX = 1F
                    button.scaleY = 1F

                    button2.scaleX = 1F
                    button2.scaleY = 1F

                    button3.scaleX = 1F
                    button3.scaleY = 1F

                    button4.scaleX = 1F
                    button4.scaleY = 1F

                    button5.scaleX = 1F
                    button5.scaleY = 1F
                }
                Log.d("TAG_SIZE2", "The button size: $butSize")
            }
            "medium"->{
                with(ui){
                    button.scaleX = 1.4F
                    button.scaleY = 1.4F

                    button2.scaleX = 1.4F
                    button2.scaleY = 1.4F

                    button3.scaleX = 1.4F
                    button3.scaleY = 1.4F

                    button4.scaleX = 1.4F
                    button4.scaleY = 1.4F

                    button5.scaleX = 1.4F
                    button5.scaleY = 1.4F
                }
                Log.d("TAG_SIZE3", "The button size: $butSize")
            }
            "large"->{
                with(ui){
                    button.scaleX = 1.8F
                    button.scaleY = 1.8F

                    button2.scaleX = 1.8F
                    button2.scaleY = 1.8F

                    button3.scaleX = 1.8F
                    button3.scaleY = 1.8F

                    button4.scaleX = 1.8F
                    button4.scaleY = 1.8F

                    button5.scaleX = 1.8F
                    button5.scaleY = 1.8F
                }
                Log.d("TAG_SIZE4", "The button size: $butSize")
            }

        }
        if(randomisation){
            randomBut()
            Log.d("TAG_RandomFeat", "The user turned on the random feature: $randomisation")
        }
        else{
            Log.d("TAG_RandomFeat1", "The user turned off the random feature: $randomisation")
        }

        //This is for the next button indication
        if (indicate){
            ui.button.background = resources.getDrawable(R.drawable.btn_indicate, theme)
            indicateCount = 1
            Log.d("TAG_Indicate1", "The user turned on the indicate feature: $indicate")
        }
        else{
            //indicateCount = 0
            Log.d("TAG_Indicate2", "The user turned on the indicate feature: $indicate")
        }


        //This is for setting the number of dots for the game using the settings.
        when (dotNum) {
            3 -> {
                ui.button.visibility = View.VISIBLE  //VISIBLE allows the button to be shown to user
                ui.button2.visibility = View.VISIBLE
                ui.button3.visibility = View.VISIBLE
                ui.button4.visibility = View.INVISIBLE
                ui.button5.visibility = View.INVISIBLE  //INVISIBLE allows the button to be hidden from the user
                Log.d("TAG_DOT3","Number of dot: $dotNum")
                ui.button2.isEnabled = false  //This stops the user from clicking any other button than number 1
                ui.button3.isEnabled = false
                Log.d("TAG_status","Button 2 clickable status: ${ui.button2.isEnabled}")
            }
            4 -> {
                ui.button.visibility = View.VISIBLE
                ui.button2.visibility = View.VISIBLE
                ui.button3.visibility = View.VISIBLE
                ui.button4.visibility = View.VISIBLE
                ui.button5.visibility = View.INVISIBLE
                Log.d("TAG_DOT4","Number of dot: $dotNum")
                ui.button2.isEnabled = false  //This stops the user from clicking any other button than number 1
                ui.button3.isEnabled = false
                ui.button4.isEnabled = false
                Log.d("TAG_status1","Button 3 clickable status: ${ui.button3.isEnabled}")
            }
            5 -> {
                ui.button.visibility = View.VISIBLE
                ui.button2.visibility = View.VISIBLE
                ui.button3.visibility = View.VISIBLE
                ui.button4.visibility = View.VISIBLE
                ui.button5.visibility = View.VISIBLE
                Log.d("TAG_DOT5","Number of dot: $dotNum")
                ui.button2.isEnabled = false  //This stops the user from clicking any other button than number 1
                ui.button3.isEnabled = false
                ui.button4.isEnabled = false
                ui.button5.isEnabled = false
                Log.d("TAG_status2","Button 4 clickable status: ${ui.button4.isEnabled}")
            }
            else -> {
                ui.button3.visibility = View.INVISIBLE
                ui.button4.visibility = View.INVISIBLE
                ui.button5.visibility = View.INVISIBLE
                Log.d("TAG_DOT0","Number of dot: $dotNum")
                ui.button2.isEnabled = false //This stops the user from clicking any other button than number 1
                Log.d("TAG_status3","Button 2 clickable status: ${ui.button2.isEnabled}")
            }
        }

        //For button 1
        //This is for the changing button color and disabling the button for the user.
        ui.button.setOnClickListener {view->
            handleButtonClick(view) //This button isnot affected by the gameComplete() function so no need to change the position of this code
            ui.button.background = resources.getDrawable(R.drawable.circle_done, theme)
            ui.button.isEnabled = false
            ui.progressBar.progress = 1

            var clickTime1 = SimpleDateFormat("ss.SSS").format(Date())
            listButtons.add("Button1 -> $clickTime1 s")

            totalButClick +=1
            count += 1
            indicateCount +=1
            ui.button2.isEnabled = true
            Log.d("TAG_status4","Button 2 clickable status: ${ui.button2.isEnabled}")
            Log.d("TAG_IndCount1", "Indicate increase: $indicateCount")
            Log.d("Just_Tag2", "Count increase= $count")
        }
        //For button 2
        //This is for the changing button color and disabling the button for the user.
        ui.button2.setOnClickListener {view->
            ui.button2.background = resources.getDrawable(R.drawable.circle_done, theme)
            ui.button2.isEnabled = false
            ui.progressBar.progress = 2

            var clickTime2 = SimpleDateFormat("ss.SSS").format(Date())
            listButtons.add("Button2 -> $clickTime2 s")

            totalButClick +=1
            handleButtonClick(view) //This has to be placed here in order for the buttons to change colour and progress value using gameComplete() function
            count += 1
            indicateCount +=1
            ui.button3.isEnabled = true
            Log.d("TAG_status5","Button 3 clickable status: ${ui.button3.isEnabled}")
            Log.d("TAG_IndCount2", "Indicate increase: $indicateCount")
            Log.d("Just_Tag3", "Count increase= $count")
        }
        //For button 3
        //This is for the changing button color and disabling the button for the user.
        ui.button3.setOnClickListener {view->
            ui.button3.background = resources.getDrawable(R.drawable.circle_done, theme)
            ui.button3.isEnabled = false
            ui.progressBar.progress = 3

            /*var clickTime3 = SimpleDateFormat("HH:mm:ss").format(Date())
            listButtons.add("$clickTime3 Button3")*/
            var clickTime3 = SimpleDateFormat("ss.SSS").format(Date())
            listButtons.add("Button3 -> $clickTime3 s")

            totalButClick +=1
            handleButtonClick(view) //This has to be placed here in order for the buttons to change colour and progress value using gameComplete() function
            count += 1
            indicateCount +=1
            ui.button4.isEnabled = true
            Log.d("TAG_status6","Button 4 clickable status: ${ui.button4.isEnabled}")
            Log.d("TAG_IndCount3", "Indicate increase: $indicateCount")
            Log.d("Just_Tag4", "Count increase= $count")
        }
        //For button 4
        //This is for the changing button color and disabling the button for the user.
        ui.button4.setOnClickListener {view->
            ui.button4.background = resources.getDrawable(R.drawable.circle_done, theme)
            ui.button4.isEnabled = false
            ui.progressBar.progress = 4

            /*var clickTime4 = SimpleDateFormat("HH:mm:ss").format(Date())
            listButtons.add("$clickTime4 Button4")*/
            var clickTime4 = SimpleDateFormat("ss.SSS").format(Date())
            listButtons.add("Button4 -> $clickTime4 s")

            totalButClick +=1
            handleButtonClick(view) //This has to be placed here in order for the buttons to change colour and progress value using gameComplete() function
            count += 1
            indicateCount +=1
            ui.button5.isEnabled = true
            Log.d("TAG_status7","Button 5 clickable status: ${ui.button5.isEnabled}")
            Log.d("TAG_IndCount4", "Indicate increase: $indicateCount")
            Log.d("Just_Tag5", "Count increase= $count")
            //ui.button5.background = resources.getDrawable(R.drawable.btn_indicate, theme)
        }
        //For button 5
        //This is for the changing button color and disabling the button for the user.
        ui.button5.setOnClickListener {view->
            ui.button5.background = resources.getDrawable(R.drawable.circle_done, theme)
            ui.button5.isEnabled = false
            ui.progressBar.progress = 5

            /*var clickTime5 = SimpleDateFormat("HH:mm:ss").format(Date())
            listButtons.add("$clickTime5 Button5")*/
            var clickTime5 = SimpleDateFormat("ss.SSS").format(Date())
            listButtons.add("Button5 -> $clickTime5 s")

            totalButClick +=1
            handleButtonClick(view) //This has to be placed here in order for the buttons to change colour and progress value using gameComplete() function
            count += 1
            indicateCount +=1
            Log.d("TAG_status8","Button 1 clickable status: ${ui.button.isEnabled}")
            Log.d("TAG_IndCount5", "Indicate increase: $indicateCount")
            Log.d("Just_Tag6", "Count increase= $count")
        }

        //This is for the quit button
        ui.quitButFree.setOnClickListener {
            val builder = AlertDialog.Builder(ui.quitButFree.context)
            with(builder){
                setTitle("Alert!") //Sets the title for the alert message after clicking button
                setMessage("Do you want to quit?") //sets the message fro the alert message after clicking button
                setIcon(R.drawable.ic_baseline_warning_24) //sets the icon for the alert message after clicking button
                setCancelable(false) //stops the user from interacting with the screen if he presses the quit button.
            }
            //pauseClock() //This pauses the clock when the user presses the 'quit' button.

            builder.setPositiveButton("Yes",
                DialogInterface.OnClickListener { _, id ->
                    //Stops the game and sends the user to main page
                    /*val i = Intent(this, MainActivity::class.java)
                    startActivity(i)*/
                    Log.d("TAG_Quit6","The value of quit: $quitGame")
                    quitGame = true
                    Log.d("TAG_Quit1","The value of quit: $quitGame")
                    gameComplete()
                }) //If the user presses "Yes" then send the user to the main page.

            builder.setNegativeButton("No",
                DialogInterface.OnClickListener { _, id ->

                }) //If the user presses "No" then keep the user in the current page.
            builder.show()

        }
    }

    //This function is for moving the buttons to random position which is the randomisation feature
    fun randomBut(){
        buttonPosX()
        buttonPosY()

        val param4 = ui.button4.layoutParams as ViewGroup.MarginLayoutParams
        param4.setMargins(convertToDP(50),convertToDP(100),convertToDP(50),convertToDP(100))
        ui.button4.layoutParams = param4 // Tested!! - We need this line for the params to be applied.

        val param5 = ui.button5.layoutParams as ViewGroup.MarginLayoutParams
        param5.setMargins(convertToDP(50),convertToDP(100),convertToDP(50),convertToDP(100))
        ui.button5.layoutParams = param5 // Tested!! - We need this line for the params to be applied.

    }

    //This function is for moving the buttons to random position of Y which is for the randomBut function
    fun buttonPosY(){
        //val rand = Random()
        //val random = (0..10).random()
        if (sizeBut == "large" || sizeBut == "medium"){
            val random = (0..4).random()
            ui.button.y = (random * convertToDP(10)).toFloat()
            ui.button2.y = (random * convertToDP(10)).toFloat()
            ui.button3.y = (random * convertToDP(10)).toFloat()
            ui.button4.y = (random * convertToDP(10)).toFloat()
            ui.button5.y = (random * convertToDP(-10)).toFloat()

            Log.d("TAG_PosY10", "PosY of button 1: ${ui.button.y}")
            Log.d("TAG_PosY14", "PosY of button 1: ${ui.button4.y}")
            Log.d("TAG_PosY15", "PosY of button 1: ${ui.button5.y}")
        }
        else{
            val random = (0..6).random()
            ui.button.y = (random * convertToDP(20)).toFloat()
            ui.button2.y = (random * convertToDP(20)).toFloat()
            ui.button3.y = (random * convertToDP(20)).toFloat()
            ui.button4.y = (random * convertToDP(20)).toFloat()
            ui.button5.y = (random * convertToDP(-20)).toFloat()

            Log.d("TAG_PosY", "PosY of button 1: ${ui.button.y}")
            Log.d("TAG_PosY4", "PosY of button 1: ${ui.button4.y}")
            Log.d("TAG_PosY4", "PosY of button 1: ${ui.button5.y}")
        }
    }

    //This function is for moving the buttons to random position of X which is for the randomBut function
    fun buttonPosX(){
        if(sizeBut == "large" || sizeBut == "medium"){
            val random = (0..4).random()
            ui.button.x = (random * convertToDP(5)).toFloat()
            ui.button2.x = (random * convertToDP(5)).toFloat()
            ui.button3.x = (random * convertToDP(5)).toFloat()
            ui.button4.x = (random * convertToDP(-15)).toFloat()
            ui.button5.x = (random * convertToDP(-5)).toFloat()

            Log.d("TAG_PosX10", "PosX of button 1: ${ui.button.x}")
            Log.d("TAG_PosX14", "PosX of button 4: ${ui.button4.x}")
            Log.d("TAG_PosX15", "PosX of button 5: ${ui.button5.x}")
        }
        else{
            val random = (0..8).random()
            ui.button.x = (random * convertToDP(10)).toFloat()
            ui.button2.x = (random * convertToDP(10)).toFloat()
            ui.button3.x = (random * convertToDP(10)).toFloat()
            ui.button4.x = (random * convertToDP(-20)).toFloat()
            ui.button5.x = (random * convertToDP(-10)).toFloat()

            Log.d("TAG_PosX", "PosX of button 1: ${ui.button.x}")
            Log.d("TAG_PosX4", "PosX of button 4: ${ui.button4.x}")
            Log.d("TAG_PosX5", "PosX of button 5: ${ui.button5.x}")
        }
    }

    /**Got help from these sources.
     * Convert pixels to dp: https://stackoverflow.com/questions/4605527/converting-pixels-to-dp
     * Get screen width and height in Android: https://stackoverflow.com/questions/4743116/get-screen-width-and-height-in-android
     * Substitute for depreciated code of displayMetrics: https://programming.vip/docs/get-screen-width-and-height-in-android.html
     * */
    fun convertToDP(dp: Int): Int {
        val displayMetrics = resources.displayMetrics
        //val height = displayMetrics.heightPixels
        //val width = displayMetrics.widthPixels
        return (dp * (displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()

        //Log.d("TAG_Display","Height: $height px and Width: $width px")
    }

    fun animateBut(){
        val random = (0..150).random().toFloat()
        //Button1
        ObjectAnimator.ofFloat(ui.button, "translationY", random).apply {
            duration = 10
            start()
        }
        //Button2
        ObjectAnimator.ofFloat(ui.button2, "translationX", random).apply {
            duration = 10
            start()
        }
        //Button3
        ObjectAnimator.ofFloat(ui.button3, "translationX", random).apply {
            duration = 10
            start()
        }
        //Button4
        ObjectAnimator.ofFloat(ui.button4, "translationX", random).apply {
            duration = 10
            start()
        }
        //Button5
        ObjectAnimator.ofFloat(ui.button5, "translationY", random).apply {
            duration = 10
            start()
        }
    }

    fun animateBut2(){
        //Button1
        val random2 = (0..150).random().toFloat()
        ObjectAnimator.ofFloat(ui.button, "translationX", random2).apply {
            duration = 10
            start()
        }
        //Button2
        ObjectAnimator.ofFloat(ui.button2, "translationY", random2).apply {
            duration = 10
            start()
        }
        //Button3
        ObjectAnimator.ofFloat(ui.button3, "translationX", random2).apply {
            duration = 10
            start()
        }
        //Button4
        ObjectAnimator.ofFloat(ui.button4, "translationY", random2).apply {
            duration = 10
            start()
        }
        //Button5
        ObjectAnimator.ofFloat(ui.button5, "translationX", random2).apply {
            duration = 10
            start()
        }
    }

    fun animateBut3(){
        val random3 = (0..250).random().toFloat()
        //Button1
        ObjectAnimator.ofFloat(ui.button, "translationY", random3).apply {
            duration = 10
            start()
        }
        //Button2
        ObjectAnimator.ofFloat(ui.button2, "translationX", random3).apply {
            duration = 10
            start()
        }
        //Button3
        ObjectAnimator.ofFloat(ui.button3, "translationY", random3).apply {
            duration = 10
            start()
        }
        //Button4
        ObjectAnimator.ofFloat(ui.button4, "translationX", random3).apply {
            duration = 10
            start()
        }
        //Button5
        ObjectAnimator.ofFloat(ui.button5, "translationY", random3).apply {
            duration = 10
            start()
        }
    }

    fun animateBut4(){
        val random4 = (0..200).random().toFloat()
        //Button1
        ObjectAnimator.ofFloat(ui.button, "translationX", random4).apply {
            duration = 10
            start()
        }
        //Button2
        ObjectAnimator.ofFloat(ui.button2, "translationY", random4).apply {
            duration = 10
            start()
        }
        //Button3
        ObjectAnimator.ofFloat(ui.button3, "translationX", random4).apply {
            duration = 10
            start()
        }
        //Button4
        ObjectAnimator.ofFloat(ui.button4, "translationY", random4).apply {
            duration = 10
            start()
        }
        //Button5
        ObjectAnimator.ofFloat(ui.button5, "translationY", random4).apply {
            duration = 10
            start()
        }
    }

    //gameCompletePage() sends the user to the DotGameComplete page if the game is over or the time is over or the user clicks the quit button.
    fun gameCompletePage(){
        storeToDatabase()
        val complete = Intent(this, FreeToPlayComplete::class.java)
        complete.putExtra("Duration", differ)
        complete.putExtra("TotalButtonPressed", totalButClick)
        startActivity(complete)
    }

    //gameComplete() is used for sending the user to DotGameComplete page or if the user didn't finish the game then start a new repetition
    fun gameComplete() {
        Log.d("TAG_Quit2","The value of quit: $quitGame")
        if (quitGame){
            Log.d("TAG_Quit3","The value of quit: $quitGame")
            val dateTime2  = LocalDateTime.now()
            endTime = dateTime2.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.MEDIUM))
            differ = Duration.between(diffStart, dateTime2).seconds //(dateTime2 - diffStart!!)

            Log.d("TAG_EndTime", "This is the end time of the player: $endTime")
            Log.d("TAG_Dur", "The duration is $differ")

            gameCompletePage()
            Log.d("TAG_Check","It does enter the if statement")
        }
        else{
            resetGame()
        }
    }

    //This is for resetting the game for new repetition or next repetition
    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    fun resetGame(){
        with(ui){
            count = 0
            indicateCount = 0
            ui.progressBar.progress = 0
            button.background = resources.getDrawable(R.drawable.circle, theme)
            button2.background = resources.getDrawable(R.drawable.circle, theme)
            button3.background = resources.getDrawable(R.drawable.circle, theme)
            button4.background = resources.getDrawable(R.drawable.circle, theme)
            button5.background = resources.getDrawable(R.drawable.circle, theme)
            button.isEnabled = true
            Log.d("TAG_Check1","It does enter the else statement ")
            Log.d("Just_Tag2", "$count")
        }
    }

    /**The source that I got help from
     * Button check: https://stackoverflow.com/questions/63570210/kotlin-check-which-button-was-clicked
     * Description of the function: This function is for getting the number of button clicks
     * */
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun handleButtonClick(view: View){
        with(view as Button){
            Log.d("Button_Tag", "$text")
            Log.d("Just_Tag", "$totalDot")
        }
        Log.d("Just_Tag1", "$count")
        //This sends the user to the game complete page if the user clicked all the buttons
        if(totalDot == count){
            repCount += 1 //This increases the repCount variable

            //This is for moving the buttons for each repetition
            if(butRandom){
                when(repCount){
                    2->{
                        animateBut()
                        Log.d("TAG_Animate0","Check animateBut()!! This is animation for repCount: $repCount")
                    }
                    3->{
                        animateBut2()
                        Log.d("TAG_Animate1","Check animateBut2()!! This is animation for repCount: $repCount")
                    }
                    4->{
                        animateBut3()
                        Log.d("TAG_Animate2","Check animateBut3()!! This is animation for repCount: $repCount")
                    }
                    5->{
                        animateBut4()

                        val param4 = ui.button4.layoutParams as ViewGroup.MarginLayoutParams
                        param4.setMargins(convertToDP(50),convertToDP(100),convertToDP(50),convertToDP(100))
                        ui.button4.layoutParams = param4 // Tested!! - You need this line for the params to be applied.


                        Log.d("TAG_Animate3","Check animateBut4()!! This is animation for repCount: $repCount")
                    }
                    else->{
                        animateBut2()
                        Log.d("TAG_Animate9","This is random for repCount: $repCount")
                    }
                }
            }
            gameComplete()
            Log.d("Tag_RepCount1", "RepCount = $repCount")
        }

        if (indicateFeat){
            //This is used for next button indication if the option is enabled in setting page
            when(indicateCount){
                1->{
                    ui.button2.background = resources.getDrawable(R.drawable.btn_indicate, theme)
                    Log.d("TAG_Indicate3", "Current button clicked: $indicateCount")
                }
                2->{
                    ui.button3.background = resources.getDrawable(R.drawable.btn_indicate, theme)
                    Log.d("TAG_Indicate4", "Current button clicked: $indicateCount")
                }
                3->{
                    ui.button4.background = resources.getDrawable(R.drawable.btn_indicate, theme)
                    Log.d("TAG_Indicate5", "Current button clicked: $indicateCount")
                }
                4->{
                    ui.button5.background = resources.getDrawable(R.drawable.btn_indicate, theme)
                    Log.d("TAG_Indicate6", "Current button clicked: $indicateCount")
                }
                else->{
                    ui.button.background = resources.getDrawable(R.drawable.btn_indicate, theme)
                    Log.d("TAG_Indicate7", "All button clicked: $indicateCount")
                }
            }
        }
    }

    //This is for storing data into the database from the game
    fun storeToDatabase(){
        //SimpleDateFormat source: https://developer.android.com/reference/kotlin/java/text/SimpleDateFormat
        databaseID = SimpleDateFormat("MMddyyyyHHmmss").format(Date())
        Log.d("TAG_IDData","This is the value of databaseID: $databaseID")

        //get db connection
        val db = Firebase.firestore
        val dotHistory = db.collection("free to play history")

        val attempt = GameData(
            id = databaseID,
            startTime = startTime,
            endTime = endTime,
            durationOfGame = differ,
            totalButtonClick = totalButClick,
            buttonList = listButtons
        )

        dotHistory.document(databaseID)
            .set(attempt)
            .addOnSuccessListener {
                //attempt.id = it.id
                Log.d(FIREBASE_TAG, "Document created with id ${attempt.id}")
            }
            .addOnFailureListener {
                Log.e(FIREBASE_TAG, "Error writing document", it)
            }
    }
}