package com.example.callog

import CallDetails
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.CallLog
import android.provider.ContactsContract
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calllog.ListAdapter
import java.util.Date

class MainActivity : AppCompatActivity() {
    private lateinit var listView: ListView

    private val PERMISSIONS = arrayOf(
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_PHONE_STATE
    )
    private val PERMISSION_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listView = findViewById(R.id.list_item)
        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        for (permission in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission)
            }
        }

        if (permissionsToRequest.isEmpty()) {
            // All permissions are granted, load data
            loadData()
        } else {
            // Request required permissions
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // All permissions granted
                loadData()
            } else {
                // Show a more informative message
                Toast.makeText(
                    this,
                    "This app needs call log and contacts permissions to function properly",
                    Toast.LENGTH_LONG
                ).show()
                // Give user another chance to grant permissions
                checkAndRequestPermissions()
            }
        }
    }

    private fun loadData() {
        val list = getCallsDetails(this)
        val adapter = ListAdapter(this, list)
        listView.adapter = adapter
    }

    private fun getCallsDetails(context: Context): ArrayList<CallDetails> {
        val callDetailsList = ArrayList<CallDetails>()
        val contentUri = CallLog.Calls.CONTENT_URI

        try {
            val cursor: Cursor? = context.contentResolver.query(
                contentUri,
                null,
                null,
                null,
                CallLog.Calls.DATE + " DESC"
            )

            cursor?.use {
                val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
                val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)
                val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
                val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)

                while (it.moveToNext()) {
                    val callType = when (it.getInt(typeIndex)) {
                        CallLog.Calls.INCOMING_TYPE -> "Incoming"
                        CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                        CallLog.Calls.MISSED_TYPE -> "Missed"
                        CallLog.Calls.REJECTED_TYPE -> "Rejected"
                        else -> "Not Defined"
                    }

                    val phoneNumber = it.getString(numberIndex) ?: "Unknown"
                    val callDate = it.getLong(dateIndex)
                    val callDayTime = Date(callDate).toString()
                    val callDuration = it.getString(durationIndex) ?: "0"
                    val callerName = getCallerName(context, phoneNumber)

                    callDetailsList.add(
                        CallDetails(
                            callerName,
                            phoneNumber,
                            callDuration,
                            callType,
                            callDayTime
                        )
                    )
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(context, "Permission denied for call logs", Toast.LENGTH_SHORT).show()
        }

        return callDetailsList
    }

    @SuppressLint("Range")
    private fun getCallerName(context: Context, phoneNumber: String): String {
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)

        val cursor: Cursor? = context.contentResolver.query(
            uri,
            projection,
            ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
            arrayOf(phoneNumber),
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            }
        }

        return "Unknown"
    }
}