package au.edu.utas.iqbalma.kit305_assign2

import android.R
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.MonthDisplayHelper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import au.edu.utas.iqbalma.kit305_assign2.databinding.ActivitySettingPageBinding


//These items are for the spinner of the Time limit option setting
var timeMap = mapOf(
    60 to "1 min",
    120 to "2 mins",
    300 to "5 mins",
    600 to "10 mins",
    900 to "15 mins"
)

//This is the shared preference for all the settings in the setting page.
const val Dot_Preference = "dots"  //This is the default value for number of dots shared preference string
const val Time_Preference = "time" //This is the default value for time limit shared preference string
const val Last_Preference = "Last_Selection" //This is the default value for time limit spinner shared preference string
const val Rep_Preference = "Number_of_Repetition" //This is the default value for number of repetition shared preference string
const val LastRep_Preference = "Last_SelectionRep" //This is the default value for number of repetition spinner shared preference string
const val Size_Preference = "size" //This is the default value for button size shared preference string
const val Random_Preference = "true" //This is the default value for randomisation of buttons shared preference string
const val Indication_Preference = "true" //This is the default value for next-button indication shared preference string

class SettingPage : AppCompatActivity() {
    private lateinit var ui : ActivitySettingPageBinding

    lateinit var preferences: SharedPreferences //This is for getting the shared preference from MainActivity

    var numOfDots = 3 //The default value for number of Dots in the setting page
    var milliseconds = 60 //The default value for Time limit
    var numRep = 2 //The default value for Number of Repetition
    var sizeButton = "small" //The default value for button size
    var randomBut = false //The default value for randomisation of buttons
    var indicationBut = false //The default value for next-button indication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivitySettingPageBinding.inflate(layoutInflater)
        setContentView(ui.root)

        loadData() //This calls the loadData function so that the preferred settings are loaded when the user reloads the app.

        /*This is for the setting the last item selected on the time limit spinner by the user*/
        val lastSelect = getSharedPreferences(Last_Preference, MODE_PRIVATE) //This is for saving the last selected item from time limit into shared preference
        val editorClick = lastSelect.edit() //This is for time limit last selection item

        val lastClick: Int = lastSelect.getInt("LastClick",1) //This gets the value for the item position

        /*This is for the setting the last item selected on the number of Repetition spinner by the user*/
        val lastSelectRep = getSharedPreferences(LastRep_Preference, MODE_PRIVATE) //This is for saving the last selected item from time limit into shared preference
        val editorClickRep = lastSelectRep.edit() ////This is for time limit last selection item

        val lastClickRep: Int = lastSelectRep.getInt("LastClickRep",1) //This gets the value for the item position

        //This is for loading the user choice for number of dots from shared Preference
        when(numOfDots){
            2->ui.dot2.isChecked = true
            3->ui.dot3.isChecked = true
            4->ui.dot4.isChecked = true
            5->ui.dot5.isChecked = true
            else->Log.d("TAG_Dot6", "Just showing the default selection for dots")
        }

        //This is for loading the user choice for button size from shared Preference
        when(sizeButton){
            "small"->ui.small.isChecked = true
            "medium"->ui.medium.isChecked = true
            "large"->ui.large.isChecked = true

            else->Log.d("TAG_Size3", "Just showing the default selection for dots")
        }


        /*This is the code for the number of repetitions spinners*/
        var mySpinnerNum = arrayOf("2", "3", "4", "5") //Option for the number of repetitions using spinners
        //Adapters provide a common interface to the data model behind a selection-style widget.
        ui.spinnerNum.adapter = ArrayAdapter<String>(
            this,
            R.layout.simple_spinner_dropdown_item,
            mySpinnerNum
        )

        ui.spinnerNum.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                numRep = selectedItem.toInt()
                editorClickRep.putInt("LastClickRep",position).commit() //This is for saving the last selected item from time limit into shared preference

                Log.d("TAG_Rep", selectedItem)
                Log.d("TAG_Rep4", "$numRep")
            } // to close the onItemSelected

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        ui.spinnerNum.setSelection(lastClickRep)//This is for loading the last item selected by the user.

        /*This is the code for the Time limit spinners*/
        //Adapters provide a common interface to the data model behind a selection-style widget.
        ui.spinnerTime.adapter = ArrayAdapter<String>(
            this,
            R.layout.simple_spinner_dropdown_item,
            timeMap.values.toTypedArray()
        )

        /** Spinners use OnItemSelectedListeners, which are slightly different to OnClickListeners.
         *  The callback provides us with an integer position of the selected item.
         *  This spinner is for time limit
         *  Help for getting the last item selection: https://www.youtube.com/watch?v=ZUGnSv1WeDE&ab_channel=LearnUs_2
         * */
        ui.spinnerTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                milliseconds = timeMap.keys.toTypedArray()[position]
                editorClick.putInt("LastClick",position).commit() //This is for saving the last selected item from time limit into shared preference

                Log.d("TAG1", selectedItem)
                Log.d("TAG_Time", "$milliseconds")
            } // to close the onItemSelected

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        ui.spinnerTime.setSelection(lastClick)//This is for loading the last item selected by the user.



        /**I got from these sources.
         * Switch button Kotlin:https://www.geeksforgeeks.org/switch-in-kotlin/
         * Description: This function is for the randomisation switch button
         * I
        * */
        ui.switchRandom.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                // if the toggle button is enabled
                randomBut = true
                Toast.makeText(this, "Random: On", Toast.LENGTH_SHORT).show ()
                Log.d("TAG_Random", "Randomisation: $randomBut")
            }
            else{
                randomBut = false
                Toast.makeText(this, "Random: off", Toast.LENGTH_SHORT) .show ()
                Log.d("TAG_Random1", "Randomisation: $randomBut")
            }
        }
        //This is for loading the user choice for randomisation of buttons from shared Preference
        when(randomBut){
            true->{
                ui.switchRandom.isChecked = true
            }
            false->{
                ui.switchRandom.isChecked = false
            }
        }

        /*This is for the Next-button indication switch button*/
        ui.switchNextBut.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                // if the toggle button is enabled
                indicationBut = true
                Toast.makeText(this, "Indication: On", Toast.LENGTH_SHORT).show ()
                Log.d("TAG_Indication", "Next-button Indication: $indicationBut")
            }
            else{
                indicationBut = false
                Toast.makeText(this, "Indication: off", Toast.LENGTH_SHORT) .show ()
                Log.d("TAG_Indication1", "Next-button Indication: $indicationBut")
            }
        }

        //This is for loading the user choice for next-button indication from shared Preference
        when(indicationBut){
            true->ui.switchNextBut.isChecked = true
            false->ui.switchNextBut.isChecked = false
        }


        /**
         * Got help from this source: https://youtu.be/ljDRCqKp2UE
         * This helps to get the Shared preference for username from MainActivity
         * and displays the username
        */
        preferences = getSharedPreferences(Name_Preference, MODE_PRIVATE)
        val userName = preferences.getString("STRING_KEY","")
        ui.lblEnteredText1.text = userName

        //This is for the home button which sends the user to the home page.
        ui.homeBtn.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            //loadData()
        }

        //This is for the save button which saves the settings in the shared preferences.
        ui.saveBtn.setOnClickListener {
            saveData()
        }


    }
    /**
     * Got help for shared preference from: https://www.youtube.com/watch?v=S5uLAGnBvUY&list=PL5DdAktpEmHsbbQb1j9I14NVrypCBoR1a&index=15&ab_channel=CodePalace
     **/
   //This is used to save the user name using shared Preference
    private fun saveData() {
        /**
         * getSharedPreferences() — Use this if you need multiple shared preference files identified by name, which you specify with the first parameter.
         * You can call this from any Context in your app.
         */
        val numDot: Int = numOfDots //This is for getting the number of dots variable
        val timeLimit: Int = milliseconds //This is for getting time limit variable
        val repNum: Int = numRep //This is for getting number of repetition variable
        val buttonSize: String = sizeButton //This is for getting the button size
        val buttonRand: Boolean = randomBut //This is for getting the randomisation of button
        val buttonIndication: Boolean = indicationBut //This is for getting the next button indication

        val sharedPreferences = getSharedPreferences(Dot_Preference, MODE_PRIVATE) //This is for the number of dots shared preference
        val sharedPreferTime = getSharedPreferences(Time_Preference, MODE_PRIVATE) //This is for the time limit shared preference
        val sharedPreferRep = getSharedPreferences(Rep_Preference, MODE_PRIVATE) //This is for the number of repetitions shared preference
        val sharedPreferSize = getSharedPreferences(Size_Preference, MODE_PRIVATE) //This is for the button size shared preference
        val sharedPreferRandom = getSharedPreferences(Random_Preference, MODE_PRIVATE) //This is for the randomisation of button shared preference
        val sharedPreferIndication = getSharedPreferences(Indication_Preference, MODE_PRIVATE) //This is for the next button indication shared preference

        val editor = sharedPreferences.edit()  //This is for number of dots
        val editorTime = sharedPreferTime.edit() //This is for the time limit
        val editorRep = sharedPreferRep.edit() //This is for the number of repetition
        val editorSize = sharedPreferSize.edit() //This is for the button size
        val editorRandom = sharedPreferRandom.edit() //This is for the randomisation of button
        val editorIndication = sharedPreferIndication.edit() //This is for the next button indication

        editor.putInt("NUM_DOT",numDot)  //This put values into the shared preference for number of dots
        editorTime.putInt("TIME_LIMIT",timeLimit) //This put value into the shared preference for time limit
        editorRep.putInt("NUM_REP",repNum) //This put value into the shared preference for number of repetitions
        editorSize.putString("BUTTON_SIZE",buttonSize) //This put value into the shared preference for button size
        editorRandom.putBoolean("RANDOM",buttonRand) //This put value into the shared preference for randomisation of buttons
        editorIndication.putBoolean("INDICATION",buttonIndication) //This put value into the shared preference for next button indication

        //apply() changes the in-memory SharedPreferences object immediately but writes the updates to disk asynchronously.
        editor.apply()  //This saves values into the shared preference for number of dots
        editorTime.apply() //This saves values into the shared preference for time limit
        editorRep.apply() //This saves values into the shared preference for number of repetitions
        editorSize.apply() //This saves values into the shared preference for button size
        editorRandom.apply() //This saves values into the shared preference for randomisation of button
        editorIndication.apply() //This saves values into the shared preference for next button indication

        Toast.makeText(this, "Settings Saved", Toast.LENGTH_SHORT).show()

        Log.d("TAG_Dot2", "Number of dots: $numDot")
        Log.d("TAG_Time1", "Time limit: $timeLimit")
        Log.d("TAG_Rep1", "Number of Repetitions: $repNum")
        Log.d("TAG_Size1", "Button size: $buttonSize")
        Log.d("TAG_Random2", "Randomisation: $buttonRand")
        Log.d("TAG_Indication2", "Next-button Indication: $buttonIndication")
    }

    //This is used to load the share preference data after the user reopens the app.
    private fun loadData(){
        val sharedPreferences = getSharedPreferences(Dot_Preference, MODE_PRIVATE) //This gets the shared preference for number of dots
        val sharedPrefTime = getSharedPreferences(Time_Preference, MODE_PRIVATE) //This gets the shared preference for time limit
        val sharedPrefRep = getSharedPreferences(Rep_Preference, MODE_PRIVATE) //This gets the shared preference for number of repetitions
        val sharedPrefSize = getSharedPreferences(Size_Preference, MODE_PRIVATE) //This gets the shared preference for button size
        val sharedPrefRandom = getSharedPreferences(Random_Preference, MODE_PRIVATE) //This gets the shared preference for randomisation of button
        val sharedPrefIndication = getSharedPreferences(Indication_Preference, MODE_PRIVATE) //This gets the shared preference for next button indication

        val savedSetting = sharedPreferences.getInt("NUM_DOT", 3) //This gets the number for number of dots (numDots) stored in shared preference
        val savedTime = sharedPrefTime.getInt("TIME_LIMIT", 60) //This gets the number for time limit (timeLimit) stored in shared preference
        val savedRep = sharedPrefRep.getInt("NUM_REP",2) //This gets the number for number of repetitions (repNum) stored in shared preference
        val savedSize = sharedPrefSize.getString("BUTTON_SIZE","") //This gets the string for button size (buttonSize) stored in shared preference
        val savedRandom = sharedPrefRandom.getBoolean("RANDOM",false) //This gets the boolean for randomisation button (buttonRand) stored in shared preference
        val savedIndication = sharedPrefIndication.getBoolean("INDICATION",false) //This gets the boolean for next button indication (buttonIndication) stored in shared preference

        numOfDots = savedSetting //This changes the value of numOfDots to the shared preference value
        milliseconds = savedTime //This changes the value of milliseconds to the shared preference value
        numRep = savedRep //This changes the value of numRep to the shared preference value
        if (savedSize != null) {
            sizeButton = savedSize //This changes the value of sizeButton to the shared preference value if the shared preference value is not null
        }
        randomBut = savedRandom //This changes the value of randomBut to the shared preference value
        indicationBut = savedIndication //This changes the value of indicationBut to the shared preference value

        Log.d("TAG_Dot3", "Number of dots: $savedSetting")
        Log.d("TAG_Time2", "Time limit: $savedTime")
        Log.d("TAG_Rep2", "Number of Repetitions: $numRep")
        Log.d("TAG_Size2", "Button size: $sizeButton")
        Log.d("TAG_Random3", "Randomisation: $randomBut")
        Log.d("TAG_Indication3", "Next-button Indication: $indicationBut")
    }

    /**I got help from this source.
     * Radio button index: https://stackoverflow.com/questions/6440259/how-to-get-the-selected-index-of-a-radiogroup-in-android?fbclid=IwAR2x3VN1ZwdxHAoV_Cr87AIrNz-fjdtXOs-Y_jusIcZtdHBxQuzYEfWCF6Q
     * Description of the function: This function is for getting the number of dots for the game
     * */
    fun onRadioButtonClicked(view: View){
        val radioDotID: Int = ui.radioDot.checkedRadioButtonId //This gets the id of the radio button clicked by the user.
        val selectedButton: View = ui.radioDot.findViewById(radioDotID) //This gets the view for the radio button clicked by the user.
        numOfDots = ui.radioDot.indexOfChild(selectedButton) + 2 //I added 2 because the index always starts from 0. So by adding 2, I can get the correct value
        Log.d("TAG_Dot", "Number of dots: $numOfDots")
    }

    //This function is for getting the button size for the game
    fun onSizeButtonClicked(view: View){
        val radioSizeID: Int = ui.radioSize.checkedRadioButtonId
        val selectedButton: View = ui.radioSize.findViewById(radioSizeID)
        var butSize = ui.radioSize.indexOfChild(selectedButton) //This gets the index number of the button clicks. It starts with index of value 0.

        //the when statement is used to make the radio button values into string instead of index number
        sizeButton = when(butSize){
            1-> "medium"
            2-> "large"
            else-> "small"
        }

        Log.d("TAG_Size", "Button size: $sizeButton")
    }


}