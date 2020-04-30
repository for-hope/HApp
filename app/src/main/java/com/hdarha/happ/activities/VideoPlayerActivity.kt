package com.hdarha.happ.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.hdarha.happ.R
import kotlinx.android.synthetic.main.activity_video_player.*


class VideoPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        setSupportActionBar(toolbar_va)
        supportActionBar?.title = "Results"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home_black_24dp)

        val videoPath = "android.resource://" + packageName+ "/" + R.raw.vid
        val uri = Uri.parse(videoPath)
        videoView.setVideoURI(uri)

        val mediaController = MediaController(this)

        videoView.setMediaController(mediaController)
        mediaController.setAnchorView(videoView)
        videoView.start()

        bottomAppBarVA.setNavigationOnClickListener {
            Toast.makeText(
                this,
                "Start Act",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        return super.onSupportNavigateUp()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.video_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id: Int = item.itemId

        if (id == R.id.action_save_video) {
            Toast.makeText(
                this,
                "Video saved.",
                Toast.LENGTH_LONG
            ).show()

            return true
        } else if (id == R.id.action_cancel_video) {
            showConfirmationDialog()

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showConfirmationDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        builder.setTitle("Cancel")
        builder.setMessage("Are you sure you want to cancel without saving?")

        builder.setPositiveButton("YES") { dialog, which -> // Do nothing but close the dialog
            dialog.dismiss()
        }

        builder.setNegativeButton("NO") { dialog, which -> // Do nothing
            dialog.dismiss()
        }

        val alert: AlertDialog = builder.create()
        alert.show()
    }
}