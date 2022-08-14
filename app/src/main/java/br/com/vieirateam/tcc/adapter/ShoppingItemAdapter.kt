package br.com.vieirateam.tcc.adapter

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.RecyclerView
import android.view.*
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.extension.loadUrl
import br.com.vieirateam.tcc.fragment.ShoppingDetailFragment
import br.com.vieirateam.tcc.model.ShoppingItem
import br.com.vieirateam.tcc.util.DoubleFormatUtil
import kotlinx.android.synthetic.main.adapter_recycler_view.view.*

class ShoppingItemAdapter(var shoppingItem: List<ShoppingItem>,
                          var onClick: (ShoppingItem) -> Unit,
                          var shoppingDetailFragment: ShoppingDetailFragment) : RecyclerView.Adapter<ShoppingItemAdapter.ViewHolder>() {

    private lateinit var actionMode: ActionMode
    private lateinit var mView: View

    private var count = 0
    private var selected = false
    private var shoppingItemSelected = mutableListOf<ShoppingItem>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_recycler_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = shoppingItem.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val shoppingItem = shoppingItem[position]
        val view = holder.itemView

        if (shoppingItemSelected.contains(shoppingItem)) {
            view.setBackgroundColor(Color.LTGRAY)
        } else {
            view.setBackgroundColor(Color.WHITE)
        }

        with(view) {
            view.textViewName.text = resources.getString(R.string.text_concat).format(shoppingItem.product.name, shoppingItem.product.measure_name)
            view.textViewValue.text = DoubleFormatUtil.doubleToString(shoppingItem.value)
            view.textViewQuantity.visibility = View.VISIBLE
            view.textViewQuantity.text = resources.getString(R.string.text_quantity).format(shoppingItem.quantity)
            view.imageView.loadUrl(context, shoppingItem.product.image, view.progressBar)

            view.setOnClickListener {
                if (selected) {
                    selectedItem(shoppingItem, view)
                } else {
                    mView = view
                    onClick(shoppingItem)
                }
            }

            view.setOnLongClickListener {
                if (!selected) {
                    (view.context as AppCompatActivity).startSupportActionMode(actionModeCallbacks)
                    selected = true
                }
                selectedItem(shoppingItem, view)
                return@setOnLongClickListener true
            }
        }
    }

    fun getView(): View {
        return mView
    }

    private fun selectedItem(shoppingItem: ShoppingItem, view: View) {
        if (this.selected) {
            if (shoppingItemSelected.contains(shoppingItem)) {
                shoppingItemSelected.remove(shoppingItem)
                view.setBackgroundColor(Color.WHITE)
                count--
            } else {
                shoppingItemSelected.add(shoppingItem)
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

    private val actionModeCallbacks = object : ActionMode.Callback {

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            shoppingDetailFragment.deleteShoppingItem(shoppingItemSelected)
            shoppingItemSelected.clear()
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
            shoppingItemSelected.clear()

            if (::actionMode.isInitialized) {
                actionMode.finish()
            }
            mode?.finish()
            notifyDataSetChanged()
        }
    }
}