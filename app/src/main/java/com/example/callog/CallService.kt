package com.example.callog

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.telecom.Call
import android.telecom.CallScreeningService
import android.telecom.TelecomManager
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class CallService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        val response = CallResponse.Builder()

        // Get the phone number
        val phoneNumber = callDetails.handle?.schemeSpecificPart

        // You can implement custom logic here to determine whether to:
        // - Allow the call
        // - Reject the call
        // - Show UI to user for decision

        response.setDisallowCall(false)  // Allow call by default
        response.setSkipCallLog(false)   // Log the call
        response.setSkipNotification(false)  // Show notification

        respondToCall(callDetails, response.build())
    }

    companion object {
        fun makeCall(context: android.content.Context, phoneNumber: String) {
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                val telecomManager = context.getSystemService(TelecomManager::class.java)
                val uri = android.net.Uri.parse("tel:$phoneNumber")
                val intent = Intent(Intent.ACTION_CALL, uri)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        }
    }
}