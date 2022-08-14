package br.com.vieirateam.tcc.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.*
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.activity.EntertainmentDetailActivity
import br.com.vieirateam.tcc.activity.ProductDetailActivity
import br.com.vieirateam.tcc.adapter.NotificationAdapter
import br.com.vieirateam.tcc.database.DatabaseService
import br.com.vieirateam.tcc.preference.UserPreference
import br.com.vieirateam.tcc.model.Notification
import br.com.vieirateam.tcc.model.User
import br.com.vieirateam.tcc.retrofit.RetrofitWebService
import br.com.vieirateam.tcc.util.SnackBarUtil
import kotlinx.android.synthetic.main.adapter_about.view.*
import kotlinx.android.synthetic.main.fragment_recycler_view.*
import me.leolin.shortcutbadger.ShortcutBadger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class NotificationFragment : Fragment(),
        SwipeRefreshLayout.OnRefreshListener {

    private var mMenu: Menu? = null
    private var noInternet: Boolean = false
    private lateinit var user: User
    private lateinit var mView: View
    private lateinit var mSnackbar: Snackbar

    private var notifications: MutableList<Notification> = mutableListOf()
    private var adapter = NotificationAdapter(notifications, { onClick(it) }, this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_recycler_view, container, false)
        setHasOptionsMenu(true)
        return mView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        this.user = arguments?.getSerializable("user") as User
        this.noInternet = arguments?.getBoolean("internet") as Boolean
        getNotifications()
    }

    override fun onPause() {
        super.onPause()
        adapter.finishActionMode()
    }

    override fun onDetach() {
        super.onDetach()
        if (::mSnackbar.isInitialized) {
            mSnackbar.dismiss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefresh.setOnRefreshListener(this)
        swipeRefresh.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.colorAccent))
        recyclerView.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
    }

    override fun onRefresh() {

        val handler = Handler()
        handler.postDelayed({
            try {
                swipeRefresh.isRefreshing = false
                getNotifications()
                adapter.finishActionMode()
            } catch (ex: IllegalStateException) {
                activity?.finish()
            }
        }, 2000)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.notification, menu)
        mMenu = menu
        configureNotification()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.notification_on -> {
                UserPreference.notification = false
                showSnackBar(getString(R.string.text_notification_off), Snackbar.LENGTH_LONG)
            }
            R.id.notification_off -> {
                UserPreference.notification = true
                showSnackBar(getString(R.string.text_notification_on), Snackbar.LENGTH_LONG)
            }
        }
        configureNotification()

        val handler = Handler()
        handler.postDelayed({
            showSnackBarNotifications()
        }, 2000)

        return super.onOptionsItemSelected(item)
    }

    private fun configureNotification() {
        if (mMenu != null) {
            if (UserPreference.notification) {
                Log.d("tcc", getString(R.string.text_notification_on))
                mMenu!!.findItem(R.id.notification_on).isVisible = true
                mMenu!!.findItem(R.id.notification_off).isVisible = false
            } else {
                Log.d("tcc", getString(R.string.text_notification_off))
                mMenu!!.findItem(R.id.notification_off).isVisible = true
                mMenu!!.findItem(R.id.notification_on).isVisible = false
            }
        }
    }

    private fun getNotifications() {
        if (noInternet) {
            getNotificationsDatabase()
        } else {
            val notificationLastId = DatabaseService.selectNotificationLastId(user.email)
            RetrofitWebService().getNotifications(user.email, notificationLastId, {
                for (notification in it) {
                    DatabaseService.insertNotification(notification)
                }
                getNotificationsDatabase()
            }, {
                showSnackBar(getString(R.string.app_error), Snackbar.LENGTH_LONG)
            })
        }
    }

    private fun getNotificationsDatabase() {

        this.notifications.clear()
        val notification = DatabaseService.selectNotificationList(user.email)
        notifications.addAll(notification)
        configureAdapter(notifications)
        countNotifications()
    }

    private fun countNotifications() {
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
    }

    private fun showSnackBarNotifications() {
        if (this.notifications.isEmpty()) {
            showSnackBar(getString(R.string.text_notification_empty), Snackbar.LENGTH_INDEFINITE)
        } else {
            if (::mSnackbar.isInitialized) {
                mSnackbar.dismiss()
            }
        }
    }

    private fun showSnackBar(message: String, time: Int) {
        try {
            mSnackbar = SnackBarUtil.show(mView, message, time)
        } catch (ex: IllegalStateException) {
            Log.e("tcc", ex.message.toString())
        }
    }

    private fun onClick(notification: Notification) {
        notification.visualized = true
        DatabaseService.updateNotification(notification)
        configureNotification(notification)
        countNotifications()
    }

    private fun configureNotification(notification: Notification) {
        when {
            notification.type == "product" -> {
                RetrofitWebService().getProductId(notification.id_object, {
                    val intent = Intent(activity, ProductDetailActivity::class.java)
                    val bundle = Bundle()
                    bundle.putSerializable("product", it)
                    bundle.putSerializable("user", user)
                    intent.putExtra("bundle", bundle)
                    startActivity(intent)
                }, {
                    showSnackBar(getString(R.string.app_error), Snackbar.LENGTH_LONG)
                })
            }
            notification.type == "entertainment" -> {
                RetrofitWebService().getEntertainmentId(notification.id_object, {
                    val intent = Intent(activity, EntertainmentDetailActivity::class.java)
                    val bundle = Bundle()
                    bundle.putSerializable("entertainment", it)
                    intent.putExtra("bundle", bundle)
                    startActivity(intent)
                }, {
                    showSnackBar(getString(R.string.app_error), Snackbar.LENGTH_LONG)
                })
            }
            else -> {
                configureDialog(notification)
            }
        }
    }

    private fun configureDialog(notification: Notification) {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_about, null)
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setCancelable(true)
        alertDialog.setTitle(notification.title)
        alertDialog.setView(view)
        view.textViewAbout.text = notification.body
        view.imageViewUser.setImageResource(R.mipmap.ic_launcher_round)

        alertDialog.setNegativeButton(getString(R.string.app_back)) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun configureAdapter(notifications: List<Notification>) {
        this.adapter.notifications = notifications
        this.adapter.notifyDataSetChanged()
        showSnackBarNotifications()
    }

    fun deleteNotification(notificationsSelected: List<Notification>) {
        for (notification in notificationsSelected) {
            DatabaseService.deleteNotification(notification)
        }
        getNotificationsDatabase()
    }
}