package br.com.vieirateam.tcc.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.activity.MainActivity
import br.com.vieirateam.tcc.database.DatabaseService
import br.com.vieirateam.tcc.preference.UserPreference
import br.com.vieirateam.tcc.model.Notification
import br.com.vieirateam.tcc.model.User
import br.com.vieirateam.tcc.retrofit.RetrofitWebService
import br.com.vieirateam.tcc.util.InternetUtil
import br.com.vieirateam.tcc.util.NotificationUtil
import com.squareup.picasso.Picasso
import me.leolin.shortcutbadger.ShortcutBadger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class NotificationService : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val bundle = intent.getBundleExtra("bundle")
        val user = bundle.getSerializable("user") as User

        if (InternetUtil.checkInternet(context)) {
            Log.d("tcc", context.getString(R.string.app_error_internet))
        } else {
            Log.d("tcc", "Iniciando serviço background")
            getNotifications(context, user)
        }
    }

    private fun getNotifications(context: Context, user: User) {

        Log.d("tcc", "Buscando notificações...")
        val notificationLastId = DatabaseService.selectNotificationLastId(user.email)
        RetrofitWebService().getNotifications(user.email, notificationLastId, {
            if (!it.isEmpty()) {
                notifyUser(context, it.first())
            }
            for (notification in it) {
                DatabaseService.insertNotification(notification)
            }

            doAsync {
                val countNotifications = DatabaseService.selectNotificationCount(user.email)
                uiThread {
                    if (countNotifications > 0) {
                        ShortcutBadger.applyCount(context, countNotifications)
                    } else {
                        ShortcutBadger.removeCount(context)
                    }
                }
            }

        }, {
            Log.d("tcc", context.getString(R.string.app_error))
        })
    }

    private fun notifyUser(context: Context, notification: Notification) {

        Log.d("tcc", "Recebi uma notificação")
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("notification", notification)

        doAsync {
            val url = context.getString(R.string.app_server) + notification.image
            Log.d("tcc", url)
            val bitmap = Picasso.with(context).load(url).get()
            createNotification(context, intent, bitmap, notification.title, notification.body)
        }
    }

    private fun createNotification(context: Context, intent: Intent, bitmap: Bitmap, title: String, body: String) {

        if (UserPreference.notification) {
            NotificationUtil.create(context, intent, bitmap, title, body)
        } else {
            Log.d("tcc", "Notificações desativadas")
        }
    }
}