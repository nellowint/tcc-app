package br.com.vieirateam.tcc.adapter

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.RecyclerView
import android.view.*
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.extension.loadUrl
import br.com.vieirateam.tcc.fragment.ShoppingFragment
import br.com.vieirateam.tcc.model.Shopping
import br.com.vieirateam.tcc.util.DoubleFormatUtil
import kotlinx.android.synthetic.main.adapter_recycler_view.view.*

class ShoppingAdapter(var shopping: List<Shopping>,
                      var onClick: (Shopping) -> Unit,
                      var shoppingFragment: ShoppingFragment) : RecyclerView.Adapter<ShoppingAdapter.ViewHolder>() {

    private lateinit var actionMode: ActionMode
    private var count = 0
    private var selected = false
    private var shoppingSelected: MutableList<Shopping> = mutableListOf()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_recycler_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = shopping.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val shopping = shopping[position]
        val view = holder.itemView

        if (shoppingSelected.contains(shopping)) {
            view.setBackgroundColor(Color.LTGRAY)
        } else {
            view.setBackgroundColor(Color.WHITE)
        }

        with(view) {
            view.textViewName.text = shopping.name
            view.textViewValue.text = DoubleFormatUtil.doubleToString(shopping.value)
            view.textViewQuantity.visibility = View.VISIBLE
            view.textViewQuantity.text = shopping.date
            view.imageView.loadUrl(context, null, view.progressBar)

            view.setOnClickListener {
                if (selected) {
                    selectedItem(shopping, view)
                } else {
                    onClick(shopping)
                }
            }

            view.setOnLongClickListener {
                if (!selected) {
                    (view.context as AppCompatActivity).startSupportActionMode(actionModeCallbacks)
                    shoppingFragment.startActionMode(false)
                    selected = true
                }
                selectedItem(shopping, view)
                return@setOnLongClickListener true
            }
        }
    }

    private fun selectedItem(shopping: Shopping, view: View) {
        if (this.selected) {
            if (shoppingSelected.contains(shopping)) {
                shoppingSelected.remove(shopping)
                view.setBackgroundColor(Color.WHITE)
                count--
            } else {
                shoppingSelected.add(shopping)
                view.setBackgroundColor(Color.LTGRAY)
                count++
            }
        }
        if (count == 0) {
            if (::actionMode.isInitialized)
                actionMode.finish()
        }
        setTitle()
    }

    private fun setTitle() {
        if (::actionMode.isInitialized) {
            actionMode.title = count.toString()
        }
    }

    private val actionModeCallbacks = object : ActionMode.Callback {

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            shoppingFragment.deleteShopping(shoppingSelected)
            shoppingSelected.clear()
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
            shoppingSelected.clear()
            shoppingFragment.startActionMode(true)

            if (::actionMode.isInitialized) {
                actionMode.finish()
            }
            mode?.finish()
            notifyDataSetChanged()
        }
    }
}