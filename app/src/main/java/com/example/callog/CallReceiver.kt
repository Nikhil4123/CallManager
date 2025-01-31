package com.example.callog

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.telecom.TelecomManager
import android.widget.Toast
import androidx.core.app.ActivityCompat

class CallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "ACCEPT_CALL" -> acceptCall(context)
            "REJECT_CALL" -> rejectCall(context)
        }
    }

    private fun acceptCall(context: Context) {
        val telecomManager = context.getSystemService(TelecomManager::class.java)

        if (telecomManager != null && ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ANSWER_PHONE_CALLS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    telecomManager.acceptRingingCall() // Works for API 26-27
                } else {
                    Toast.makeText(context, "Accepting call not supported on this version", Toast.LENGTH_SHORT).show()
                }
            } catch (e: SecurityException) {
                Toast.makeText(context, "Failed to accept call: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Permission denied to accept call", Toast.LENGTH_SHORT).show()
        }
    }

    private fun rejectCall(context: Context) {
        val telecomManager = context.getSystemService(TelecomManager::class.java)

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(context, "Permission denied to reject call", Toast.LENGTH_SHORT).show()
            return
        }

        if (telecomManager != null && telecomManager.getCallCapablePhoneAccounts().isNotEmpty()) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // Android 12+ (API 31): Use CallScreeningService (Better approach)
                    Toast.makeText(context, "Call rejection not supported directly in Android 12+", Toast.LENGTH_SHORT).show()
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    telecomManager.endCall() // Only works if app is the default dialer
                } else {
                    Toast.makeText(context, "Call rejection not supported on this version", Toast.LENGTH_SHORT).show()
                }
            } catch (e: SecurityException) {
                Toast.makeText(context, "Failed to reject call: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Cannot reject call: App is not a default dialer", Toast.LENGTH_SHORT).show()
        }
    }
}
