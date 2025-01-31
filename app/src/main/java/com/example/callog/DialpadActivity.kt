package com.example.callog

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class DialpadActivity : AppCompatActivity() {
    private lateinit var numberInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialpad)

        numberInput = findViewById(R.id.number_input)
        setupDialpad()
    }

    private fun setupDialpad() {
        // Setup number buttons
        val numberClickListener = { v: android.view.View ->
            val digit = (v as Button).text
            numberInput.append(digit)
        }

        // Bind number buttons
        for (i in 0..9) {
            findViewById<Button>(
                resources.getIdentifier("btn_$i", "id", packageName)
            ).setOnClickListener(numberClickListener)
        }

        // Setup call button
        findViewById<ImageButton>(R.id.btn_call).setOnClickListener {
            val number = numberInput.text.toString()
            if (number.isNotEmpty()) {
                val mainActivity = MainActivity()
                mainActivity.makeCall(number)
                finish()
            }
        }

        // Setup backspace button
        findViewById<ImageButton>(R.id.btn_backspace).setOnClickListener {
            val text = numberInput.text
            if (text.isNotEmpty()) {
                numberInput.setText(text.substring(0, text.length - 1))
            }
        }
    }
}