package pl.notatki.adapter

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import pl.notatki.R
import pl.notatki.activity.MainActivity

const val notificationIDExtra = "notificationExtra"
const val channelID = "channel1"
const val titleExtra = "titleExtra"
const val messageExtra = "textExtra"

/*class NotificationReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent?) {
        val builder = NotificationCompat.Builder(context, "channelID")
            .setSmallIcon(com.google.android.material.R.drawable.ic_clock_black_24dp)
            .setContentTitle(intent?.getStringExtra(titleExtra))
            .setContentText(intent?.getStringExtra(messageExtra))

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(notificationID, builder.build())
    }
}*/

class NotificationReceiver : BroadcastReceiver()
{
    override fun onReceive(context: Context, intent: Intent) {

        val notification = NotificationCompat.Builder(context, "channelID")
                .setSmallIcon(com.google.android.material.R.drawable.ic_clock_black_24dp)
                .setContentTitle(intent?.getStringExtra(titleExtra))
                .setContentText(intent?.getStringExtra(messageExtra))
                .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, notification)
    }
}
