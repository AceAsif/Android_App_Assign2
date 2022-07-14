package au.edu.utas.iqbalma.kit305_assign2.ui.main

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.edu.utas.iqbalma.kit305_assign2.*
import au.edu.utas.iqbalma.kit305_assign2.databinding.FragmentTapGameBinding
import au.edu.utas.iqbalma.kit305_assign2.databinding.FreeListItemBinding
import au.edu.utas.iqbalma.kit305_assign2.databinding.TapListItemsBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class TapGameHistory: Fragment() {
    private var _binding: FragmentTapGameBinding?= null
    private val binding get() = _binding!!
    val db = Firebase.firestore //Connection for Firebase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTapGameBinding.inflate(inflater, container, false)

        binding.historyTap.adapter = TapHistoryAdapter(tapHistories = itemsTap)

        //vertical list
        binding.historyTap.layoutManager = LinearLayoutManager(activity) //The context for LinearLayoutManager should be 'activity' instead of 'this' in a fragment

        /*These code are for connecting to the Firebase*/
        //get db connection
        var tapHistory = db.collection("tap game history")

        binding.txtTotalEx3.text = "Loading..."

        tapHistory
            .get()
            .addOnSuccessListener { result ->
                itemsTap.clear() //this line clears the list, and prevents a bug where items would be duplicated upon rotation of screen
                Log.d(FIREBASE_TAG, "--- all tap game history ---")
                for (document in result)
                {
                    //Log.d(FIREBASE_TAG, document.toString())
                    val historyTap = document.toObject<TapGameData>()
                    historyTap.id = document.id
                    Log.d(FIREBASE_TAG, historyTap.toString())

                    itemsTap.add(historyTap)
                }
                (binding.historyTap.adapter as TapGameHistory.TapHistoryAdapter).notifyDataSetChanged()
                val totalData = itemsTap.size // This variable gets the total number of game data from Firebase
                binding.txtTotalEx3.text = "Total Exercises: $totalData" //This gets the total number of data stored in Firebase for Dot game history.
            }
        return binding.root
    }

    inner class TapHistoryHolder(var ui: TapListItemsBinding) : RecyclerView.ViewHolder(ui.root) {}

    inner class TapHistoryAdapter(private val tapHistories: MutableList<TapGameData>) : RecyclerView.Adapter<TapGameHistory.TapHistoryHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TapHistoryHolder {
            val ui = TapListItemsBinding.inflate(layoutInflater, parent, false)   //inflate a new row from the my_list_item.xml
            return TapHistoryHolder(ui)                                                            //wrap it in a ViewHolder
            Log.d("TAG_Issue", "It is inside the onCreateViewHolder. No issue")
        }

        override fun getItemCount(): Int {
            Log.d("TAG_Issue2", "It is inside the getItemCount. No issue")
            return tapHistories.size
        }

        override fun onBindViewHolder(holder: TapHistoryHolder, position: Int) {
            val history = tapHistories[position]   //get the data at the requested position
            holder.ui.txtDurationTap.text = "Duration: ${history.durationOfGameTap}s" //This display the duration of one game
            holder.ui.txtStartTap.text = "Start time: ${history.startTimeTap}" //This display the start time of one game
            holder.ui.txtGameModeTap.text = "Game Mode: ${history.gameModeTap}"

            holder.ui.root.setOnClickListener {
                var i = Intent(holder.ui.root.context, TapListOfButtons::class.java)
                i.putExtra(TAP_HISTORY_INDEX, position)
                startActivity(i)
            }

            /**Got Help from these source:
             * How to properly use setOnLongClickListener() with Kotlin: https://stackoverflow.com/questions/49712663/how-to-properly-use-setonlongclicklistener-with-kotlin
             * Toast documentation: https://developer.android.com/guide/topics/ui/notifiers/toasts
             * */
            holder.ui.root.setOnLongClickListener{
                var builder = AlertDialog.Builder(holder.ui.root.context)
                with(builder){
                    setTitle("Alert!") //Sets the title for the alert message after clicking button
                    setMessage("Do you want to delete?") //sets the message fro the alert message after clicking button
                    setIcon(au.edu.utas.iqbalma.kit305_assign2.R.drawable.ic_baseline_warning_24) //sets the icon for the alert message after clicking button
                    setCancelable(false) //stops the user from interacting with the screen if he presses the quit button.
                }
                builder.setPositiveButton("Yes",
                    DialogInterface.OnClickListener { dialog, id ->
                        db.collection("tap game history").document("${history.id}")
                            .delete()
                            .addOnSuccessListener {
                                Log.d("TAG", "DocumentSnapshot successfully deleted!")
                                tapHistories.remove(history) //This removes the data
                                notifyItemRemoved(holder.adapterPosition) //This tells the view that an item has been removed.
                                (binding.historyTap.adapter as TapGameHistory.TapHistoryAdapter).notifyDataSetChanged()
                                val totalData = itemsTap.size // This variable gets the total number of game data from Firebase
                                binding.txtTotalEx3.text = "Total Exercises: $totalData"
                            }
                            .addOnFailureListener { e -> Log.w("TAG", "Error deleting document", e) }


                        Toast.makeText(context, "Successfully Deleted!", Toast.LENGTH_SHORT).show()
                    }) //If the user presses "Yes" then send the user to the main page.

                builder.setNegativeButton("No",
                    DialogInterface.OnClickListener { dialog, id ->
                        Toast.makeText(context, "Not deleted!", Toast.LENGTH_SHORT).show()
                    }) //If the user presses "No" then keep the user in the current page.

                builder.show()

                //Toast.makeText(context, "Long click detected", Toast.LENGTH_SHORT).show()
                Log.d("TAG_OnLongPress","This is the id for this one ${history.id}")
                return@setOnLongClickListener true
            }
            Log.d("TAG_Issue3", "It is inside the onBindViewHolder. No issue")
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}