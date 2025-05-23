package com.example.trackingexpenses

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast

class NotificationScheduler {
    fun scheduleNotification(context: Context, targetTimeInMillis: Long, message: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("message", message)
        }
        val requestCode = targetTimeInMillis.toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (alarmManager.canScheduleExactAlarms()) {
                try {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        targetTimeInMillis,
                        pendingIntent
                    )
                } catch (e: SecurityException) {
                    Toast.makeText(context, "Permission to schedule exact alarms is denied.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please allow the app to schedule exact alarms in the settings.", Toast.LENGTH_SHORT).show()
            }
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                targetTimeInMillis,
                pendingIntent
            )
        }
    }
}