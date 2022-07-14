package au.edu.utas.iqbalma.kit305_assign2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import au.edu.utas.iqbalma.kit305_assign2.databinding.ActivityFreeListOfButtonsBinding
import au.edu.utas.iqbalma.kit305_assign2.databinding.ActivityTapListOfButtonsBinding

class TapListOfButtons : AppCompatActivity() {
    private lateinit var ui : ActivityTapListOfButtonsBinding

    var list = "" //This saves all the data displayed from the database into a string so that it can be shared as CSV format

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityTapListOfButtonsBinding.inflate(layoutInflater)
        setContentView(ui.root)

        val historyID = intent.getIntExtra(TAP_HISTORY_INDEX, -1)
        val historyObject = itemsTap[historyID]
        //Log.d("TAG_ListCheck", historyObject.buttonList.toString())

        //I used a safe call (?.) here. This prevents the code from crashing if the value is null.
        historyObject.startTimeTap?.let{
            ui.txtStartTime.text = "Start time: ${historyObject.startTimeTap}" //This sets the start time status
        }

        historyObject.endTimeTap?.let{
            ui.txtEndTime.text = "End time: ${historyObject.endTimeTap}" //This sets the end time status
        }

        historyObject.durationOfGameTap?.let{
            ui.txtDurTime.text = "Duration: ${historyObject.durationOfGameTap}s"
        }

        historyObject.totalButtonTap?.let{
            ui.txtTotalButTap.text = "Total number of tap: ${historyObject.totalButtonTap}"
        }

        historyObject.gameModeTap?.let{
            ui.textGameModeTap.text = "Game Mode: ${historyObject.gameModeTap}"
        }

        historyObject.gameStatus?.let{
            ui.txtStatusTap.text = "Status: ${historyObject.gameStatus}"
        }

        list = "${ui.txtStatusTap.text}, ${ui.textGameModeTap.text}, ${ui.txtStartTime.text}, ${ui.txtEndTime.text}, ${ui.txtDurTime.text}, ${ui.txtTotalButTap.text}"

        //Log.d("TAG_ListCheck2", history.buttonList.toString())
        Log.d("TAG_ShareCheck", list)

        /*//This is for the home button which sends the user to the home page.
        ui.backButton2.setOnClickListener {
            val i = Intent(this, HistoryPage::class.java)
            startActivity(i)
        }*/

        //Source: https://developer.android.com/reference/androidx/activity/OnBackPressedCallback
        //This is for the home button which sends the user to the home page.
        ui.backButton2.setOnClickListener {
            onBackPressed() //This works same way as the Android system "Back" button.
        }

        //This is for the share button which lets the user share the data as plain text
        ui.shareButton2.setOnClickListener {
            var sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, list)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, "Share via..."))
        }
    }
}