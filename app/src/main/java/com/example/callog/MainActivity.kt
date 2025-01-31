package com.example.callog  // Changed from com.example.calllog
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
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
    private val requestReadLog = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS),
                requestReadLog
            )
        } else {
            loadData()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestReadLog && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadData()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadData() {
        val listView: ListView = findViewById(R.id.list_item) // Ensure list_view exists in XML
        val list = getCallsDetails(this)
        val adapter = ListAdapter(this, list)
        listView.adapter = adapter
    }

    private fun getCallsDetails(context: Context): ArrayList<CallDetails> {
        val callDetailsList = ArrayList<CallDetails>()
        val contentUri = CallLog.Calls.CONTENT_URI

        try {
            val cursor: Cursor? = context.contentResolver.query(contentUri, null, null, null, CallLog.Calls.DATE + " DESC")

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

                    // Get Caller Name from Contacts
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
            Toast.makeText(context, "User denied permission", Toast.LENGTH_SHORT).show()
        }

        return callDetailsList
    }

    // 🔹 Function to Get Caller Name from Contacts
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

    fun makeCall(number: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$number")
        }
        startActivity(intent)
    }
}

// Model Class for Call Details
data class CallDetails(
    val callerName: String,
    val phoneNumber: String,
    val callDuration: String,
    val callType: String,
    val callDate: String
)
