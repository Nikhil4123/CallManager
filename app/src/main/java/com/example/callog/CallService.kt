package com.example.callog

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.telecom.TelecomManager
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat

class CallService : CallScreeningService() {
    private val CHANNEL_ID = "incoming_calls"
    private val NOTIFICATION_ID = 1001

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = callDetails.handle?.schemeSpecificPart ?: return
        val callerName = getCallerName(phoneNumber)

        // Create response builder
        val response = CallResponse.Builder()

        when (shouldBlockCall(phoneNumber)) {
            true -> {
                response.setDisallowCall(true)
                response.setSkipCallLog(false)
                response.setSkipNotification(false)
            }
            false -> {
                response.setDisallowCall(false)
                if (callerName != null) {
                    showIncomingCallNotification(phoneNumber, callerName)
                }
            }
        }

        respondToCall(callDetails, response.build())
    }

    private fun getCallerName(phoneNumber: String): String? {
        val contentResolver = contentResolver
        val uri = Uri.withAppendedPath(
            android.provider.ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val cursor = contentResolver.query(uri, arrayOf(android.provider.ContactsContract.PhoneLookup.DISPLAY_NAME), null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(0)
            }
        }
        return null
    }


    private fun shouldBlockCall(phoneNumber: String): Boolean {
        // Implement your call blocking logic here
        // For example, check against a blocklist
        return false
    }

    private fun showIncomingCallNotification(phoneNumber: String, callerName: String) {
        val acceptIntent = Intent(this, CallReceiver::class.java).apply {
            action = "ACCEPT_CALL"
            data = Uri.parse("tel:$phoneNumber")
        }

        val rejectIntent = Intent(this, CallReceiver::class.java).apply {
            action = "REJECT_CALL"
            data = Uri.parse("tel:$phoneNumber")
        }

        val acceptPendingIntent = PendingIntent.getBroadcast(
            this, 0, acceptIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val rejectPendingIntent = PendingIntent.getBroadcast(
            this, 1, rejectIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Incoming Call")
            .setContentText("From: ${callerName ?: phoneNumber}")
            .setSmallIcon(R.drawable.ic_call)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .addAction(R.drawable.ic_call_accept, "Accept", acceptPendingIntent)
            .addAction(R.drawable.ic_call_reject, "Reject", rejectPendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Incoming Calls"
            val descriptionText = "Notifications for incoming calls"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        fun makeOutgoingCall(context: Context, phoneNumber: String) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val telecomManager = context.getSystemService(TelecomManager::class.java)
                val uri = Uri.parse("tel:$phoneNumber")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val callIntent = Intent(Intent.ACTION_CALL, uri)
                    callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(callIntent)
                }
            }
        }
    }
}