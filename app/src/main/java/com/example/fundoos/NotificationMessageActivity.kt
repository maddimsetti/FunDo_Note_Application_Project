package com.example.fundoos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_notification_message.*

class NotificationMessageActivity : AppCompatActivity() {
    lateinit var textViewTitle: TextView
    lateinit var textViewContent: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_message)
        textViewTitle = tv_title
        textViewContent = tv_content
        val bundle = intent.extras
        textViewTitle.setText(bundle?.getString("Header"))
        textViewContent.setText(bundle?.getString("description"))
    }
}