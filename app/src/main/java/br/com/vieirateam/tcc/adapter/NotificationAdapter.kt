package br.com.vieirateam.tcc.adapter

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.RecyclerView
import android.view.*
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.extension.loadUrl
import br.com.vieirateam.tcc.fragment.NotificationFragment
import br.com.vieirateam.tcc.model.Notification
import kotlinx.android.synthetic.main.adapter_recycler_view.view.*

class NotificationAdapter(var notifications: List<Notification>,
                          var onClick: (Notification) -> Unit,
                          var notificationFragment: NotificationFragment) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    private lateinit var actionMode: ActionMode

    private var count = 0
    private var selected = false
    private var notificationsSelected: MutableList<Notification> = mutableListOf()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_recycler_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = notifications.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val notification = notifications[position]
        val view = holder.itemView

        if (notificationsSelected.contains(notification)) {
            view.setBackgroundColor(Color.LTGRAY)
        } else {
            view.setBackgroundColor(Color.WHITE)
        }

        with(view) {
            view.textViewName.text = notification.title
            view.textViewValue.text = notification.body
            view.imageView.loadUrl(context, notification.image, progressBar)

            view.setOnClickListener {
                if (selected) {
                    selectedItem(notification, view)
                } else {
                    onClick(notification)
                }
            }

            view.setOnLongClickListener {
                if (!selected) {
                    (view.context as AppCompatActivity).startSupportActionMode(actionModeCallbacks)
                    selected = true
                }
                selectedItem(notification, view)
                return@setOnLongClickListener true
            }
        }
    }

    private fun selectedItem(notification: Notification, view: View) {
        if (this.selected) {
            if (notificationsSelected.contains(notification)) {
                notificationsSelected.remove(notification)
                view.setBackgroundColor(Color.WHITE)
                count--
            } else {
                notificationsSelected.add(notification)
                view.setBackgroundColor(Color.LTGRAY)
                count++
            }
        }
        if (count == 0) {
            if (::actionMode.isInitialized) {
                actionMode.finish()
            }
        }
        setTitle()
    }

    private fun setTitle() {
        if (::actionMode.isInitialized) {
            actionMode.title = count.toString()
        }
    }

    fun finishActionMode() {
        if (::actionMode.isInitialized) {
            actionMode.finish()
        }
    }

    private val actionModeCallbacks = object : ActionMode.Callback {

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            notificationFragment.deleteNotification(notificationsSelected)
            notificationsSelected.clear()
            mode?.finish()
            return true
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            if (mode != null) {
                actionMode = mode
                val inflater = actionMode.menuInflater
                inflater.inflate(R.menu.delete, menu)
            }
            if (::actionMode.isInitialized)
                actionMode.title = "1"
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            count = 0
            selected = false
            notificationsSelected.clear()

            if (::actionMode.isInitialized) {
                actionMode.finish()
            }
            mode?.finish()
            notifyDataSetChanged()
        }
    }
}