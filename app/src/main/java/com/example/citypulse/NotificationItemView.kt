package com.example.citypulse

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView

class NotificationItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val tvTitle: TextView
    private val tvMessage: TextView
    private val tvTime: TextView

    init {
        // Pastikan menggunakan layout XML yang benar untuk item notifikasi
        LayoutInflater.from(context).inflate(R.layout.activity_notificationitemview, this, true)

        tvTitle = findViewById(R.id.tvTitle)
        tvMessage = findViewById(R.id.tvMessage)
        tvTime = findViewById(R.id.tvTime)

        orientation = VERTICAL
    }

    // Metode bind untuk mengikat data ke tampilan
    fun bind(
        title: String,
        message: String,
        time: String
    ) {
        tvTitle.text = title
        tvMessage.text = message
        tvTime.text = time
    }
}
