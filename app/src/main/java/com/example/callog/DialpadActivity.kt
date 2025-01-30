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

        // Setup numeric buttons
        setupDialpad()

        // Setup call button
        findViewById<ImageButton>(R.id.btn_call).setOnClickListener {
            val number = numberInput.text.toString()
            if (number.isNotEmpty()) {
                CallService.makeCall(this, number)
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

    private fun setupDialpad() {
        val numberClickListener = { v: android.view.View ->
            val digit = (v as Button).text
            numberInput.append(digit)
        }

        // Setup number buttons 0-9, *, #
        findViewById<Button>(R.id.btn_0).setOnClickListener(numberClickListener)
        findViewById<Button>(R.id.btn_1).setOnClickListener(numberClickListener)
        findViewById<Button>(R.id.btn_2).setOnClickListener(numberClickListener)
        findViewById<Button>(R.id.btn_3).setOnClickListener(numberClickListener)
        findViewById<Button>(R.id.btn_4).setOnClickListener(numberClickListener)
        findViewById<Button>(R.id.btn_5).setOnClickListener(numberClickListener)
        findViewById<Button>(R.id.btn_6).setOnClickListener(numberClickListener)
        findViewById<Button>(R.id.btn_7).setOnClickListener(numberClickListener)
        findViewById<Button>(R.id.btn_8).setOnClickListener(numberClickListener)
        findViewById<Button>(R.id.btn_9).setOnClickListener(numberClickListener)
        findViewById<Button>(R.id.btn_star).setOnClickListener(numberClickListener)
        findViewById<Button>(R.id.btn_hash).setOnClickListener(numberClickListener)
    }
}