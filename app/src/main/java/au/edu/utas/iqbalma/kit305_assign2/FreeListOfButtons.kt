package au.edu.utas.iqbalma.kit305_assign2

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import au.edu.utas.iqbalma.kit305_assign2.databinding.ActivityFreeListOfButtonsBinding
import au.edu.utas.iqbalma.kit305_assign2.databinding.ActivityFreeToPlayCompleteBinding

class FreeListOfButtons : AppCompatActivity() {
    private lateinit var ui : ActivityFreeListOfButtonsBinding

    var list = "" //This saves all the data displayed from the database into a string so that it can be shared as CSV format

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityFreeListOfButtonsBinding.inflate(layoutInflater)
        setContentView(ui.root)

        //Link: https://stackoverflow.com/questions/8642823/using-setimagedrawable-dynamically-to-set-image-in-an-imageview
        ui.userImage.setImageResource(R.drawable.ic_baseline_account_circle_24)

        val historyID = intent.getIntExtra(FREE_HISTORY_INDEX, -1)
        val historyObject = freeItems[historyID]
        Log.d("TAG_ListCheck", historyObject.buttonList.toString())

        //I used a safe call (?.) here. This prevents the code from crashing if the value is null.
        historyObject.startTime?.let{
            ui.txtStartTime.text = "Start time: ${historyObject.startTime}" //This sets the start time status
        }

        historyObject.endTime?.let{
            ui.txtEndTime.text = "End time: ${historyObject.endTime}" //This sets the end time status
        }

        historyObject.durationOfGame?.let{
            ui.txtDurTime.text = "Duration: ${historyObject.durationOfGame}s"
        }

        historyObject.totalButtonClick?.let{
            ui.txtTotalBut.text = "Total Buttons Click: ${historyObject.totalButtonClick}"
        }

        /**Got help from these sources:
         * Removing brackets from list: https://stackoverflow.com/questions/7536154/remove-brackets-from-a-list-set-to-a-textview
         * */
        historyObject.buttonList?.let{
            ui.txtButton.text = "Button List: " + historyObject.buttonList.toString().replace("[", "").replace("]", "").replace(",", "\n")
        }

        //Got help from the source: https://stackoverflow.com/questions/3487389/convert-string-to-uri
        //I used a safe call (?.) here because the user might choose not to upload a picture so the value will be null. This prevents the code from crashing if the value is null.
        historyObject.userPicture?.let{
            var link = Uri.parse(historyObject.userPicture)
            Log.d("TAG_uri2","The link is $link")
            ui.userImage.setImageURI(link)
        }

        list = "${ui.txtDurTime.text}, ${ui.txtStartTime.text}, ${ui.txtEndTime.text}, ${ui.txtTotalBut.text}, ${ui.txtButton.text}"

        //Log.d("TAG_ListCheck2", history.buttonList.toString())
        Log.d("TAG_ShareCheck", list)

        /*//This is for the home button which sends the user to the home page.
        ui.backButton.setOnClickListener {
            val i = Intent(this, HistoryPage::class.java)
            startActivity(i)
        }*/
        //Source: https://developer.android.com/reference/androidx/activity/OnBackPressedCallback
        //This is for the home button which sends the user to the home page.
        ui.backButton.setOnClickListener {
            onBackPressed() //This works same way as the Android system "Back" button.
        }

        //This is for the share button which lets the user share the data as plain text
        ui.shareButton.setOnClickListener {
            var sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, list)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, "Share via..."))
        }
    }
}