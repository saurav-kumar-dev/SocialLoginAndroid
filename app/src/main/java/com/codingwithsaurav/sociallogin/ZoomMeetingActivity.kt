package com.codingwithsaurav.sociallogin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class ZoomMeetingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom_meeting)

        val meetingUrl = "https://us04web.zoom.us/j/71760584038?pwd=vHBSfwRNuv8vsEecvrHPofdjaMEVpa.1"
        val zoomAppPackage = "us.zoom.videomeetings"
        val zoomIntent = packageManager.getLaunchIntentForPackage(zoomAppPackage)

        val zoomAppIntent = Intent(Intent.ACTION_VIEW)
        zoomAppIntent.data = Uri.parse(meetingUrl)
        zoomAppIntent.setPackage(zoomAppPackage)

        if (zoomAppIntent.resolveActivity(packageManager) != null) {
            startActivity(zoomAppIntent)
        } else {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(meetingUrl))
            startActivity(browserIntent)
        }

        /* if (zoomIntent != null) {
             zoomIntent.data = Uri.parse(meetingUrl)
             startActivity(zoomIntent)
         } else {
             val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(meetingUrl))
             startActivity(browserIntent)
         }*/
    }
}