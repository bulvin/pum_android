package pl.notatki.adapter

import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat


const val notificationIDExtra = "notificationExtra"
const val channelID = "channel1"
const val titleExtra = "titleExtra"
const val messageExtra = "textExtra"


class NotificationReceiver : BroadcastReceiver()
{
    override fun onReceive(context: Context, intent: Intent) {

        val notID = intent.getIntExtra(
            notificationIDExtra,
            1
        )

        val notification = NotificationCompat.Builder(context, "channelID")
                .setSmallIcon(com.google.android.material.R.drawable.ic_clock_black_24dp)
                .setContentTitle(intent?.getStringExtra(titleExtra))
                .setContentText(intent?.getStringExtra(messageExtra))
                .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notID, notification)
    }
}
