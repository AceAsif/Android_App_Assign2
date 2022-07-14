package au.edu.utas.iqbalma.kit305_assign2

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import au.edu.utas.iqbalma.kit305_assign2.ui.main.SectionsPagerAdapter
import au.edu.utas.iqbalma.kit305_assign2.databinding.ActivityHistoryPageBinding

class HistoryPage : AppCompatActivity() {

    private lateinit var ui: ActivityHistoryPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityHistoryPageBinding.inflate(layoutInflater)
        setContentView(ui.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = ui.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = ui.tabs
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = ui.homeFab

        fab.setOnClickListener { view ->
            /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()*/
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)

            /*var sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                //putExtra(Intent.EXTRA_TEXT, ui.fab.text.toString())
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, "Share via..."))*/
        }

    }
}