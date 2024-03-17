package com.farzin.batterymanager.service

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.farzin.batterymanager.R
import com.farzin.batterymanager.activities.MainActivity

class BatteryNotifications : Service() {


    var manger: NotificationManager? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        createChannel()
        startNotifications()
        registerReceiver(batteryInfo, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        return START_STICKY
    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun createChannel() {

        //Build.VERSION_CODES.O = api target 26

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manger = getSystemService(NotificationManager::class.java)
            manger?.createNotificationChannel(channel)
        }

    }


    private fun startNotifications() {

        //set action
        val intent1 = Intent(this.applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this.applicationContext,
            1,
            intent1,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notifications = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("My Title")
            .setContentText("My Content")
            .setColor(Color.GREEN)
            .setSmallIcon(R.drawable.good)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(Notification_ID, notifications)

    }

    private val batteryInfo: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val batteryLevel = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            val plugged = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
            var pluggedState = ""
            if (intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) == 0) {
                pluggedState = "You are using battery"
            } else {
                pluggedState = "Your phone is charging"
            }

                if (batteryLevel!! >= 99 && plugged == 1){
                    startAlarm()
                    pluggedState = "Your phone is fully charged"
                }


            updateNotifications(batteryLevel, pluggedState)
        }

    }

    private fun updateNotifications(batteryLevel: Int, pluggedState: String) {
        //set action
        val intent1 = Intent(this.applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this.applicationContext,
            1,
            intent1,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notifications = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(pluggedState)
            .setContentText("Battery Level : $batteryLevel")
            .setColor(Color.GREEN)
            .setSmallIcon(R.drawable.good)
            .setContentIntent(pendingIntent)
            .build()

        manger?.notify(Notification_ID, notifications)
    }

    private fun startAlarm() {
        val alarm: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ring = RingtoneManager.getRingtone(applicationContext, alarm)
        ring.play()

        val v = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(1500,VibrationEffect.DEFAULT_AMPLITUDE))
        }else{
            v.vibrate(1500)
        }
    }


    companion object {
        const val CHANNEL_ID = "BatteryManagerChannel"
        const val CHANNEL_NAME = "BatteryManagerService"
        const val Notification_ID = 1
    }
}