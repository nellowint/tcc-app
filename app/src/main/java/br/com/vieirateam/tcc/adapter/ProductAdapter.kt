package br.com.vieirateam.tcc.adapter

import android.app.AlertDialog
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.RecyclerView
import android.view.*
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.extension.loadUrl
import br.com.vieirateam.tcc.fragment.ProductFragment
import br.com.vieirateam.tcc.model.Product
import br.com.vieirateam.tcc.util.DoubleFormatUtil
import kotlinx.android.synthetic.main.adapter_product.view.*
import kotlinx.android.synthetic.main.adapter_shopping_item.view.*

class ProductAdapter(
        var products: List<Product>,
        var onClick: (Product) -> Unit,
        var productFragment: ProductFragment) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    private lateinit var actionMode: ActionMode
    private var count = 0
    private var selected = false
    private var productsSelected: MutableList<Product> = mutableListOf()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_product, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val product = products[position]
        val view = holder.itemView

        if (productsSelected.contains(product)) {
            view.setBackgroundColor(Color.LTGRAY)
            selectedItem(true, view)
        } else {
            view.setBackgroundColor(Color.WHITE)
            selectedItem(false, view)
        }

        with(view) {

            view.textViewNameProduct.text = resources.getString(R.string.text_concat).format(product.name, product.measure_name)
            view.textViewValueProduct.text = DoubleFormatUtil.doubleToString(product.value)
            view.imageViewProduct.loadUrl(context, product.image, progressBarProduct)
            view.textViewQuantityProduct.text = product.quantity.toString()

            view.setOnClickListener {
                if (selected) {
                    if (product.selected) {
                        selectedItem(true, view)
                        configureDialog(product, view)
                    } else {
                        selectedItem(product, view)
                        product.selected = true
                    }
                } else {
                    onClick(product)
                }
            }

            view.setOnLongClickListener {
                if (!selected) {
                    selectedItem(true, view)
                    (view.context as AppCompatActivity).startSupportActionMode(actionModeCallbacks)
                    productFragment.startActionMode(true)
                    selected = true
                }
                selectedItem(product, view)
                view.textViewQuantityProduct.text = product.quantity.toString()
                return@setOnLongClickListener true
            }
        }
    }

    private fun configureDialog(product: Product, mView: View) {

        val context = mView.context
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_shopping_item, null)
        val alertDialog = AlertDialog.Builder(context)

        alertDialog.setTitle(product.name + " " + product.measure_name)
        alertDialog.setCancelable(true)
        alertDialog.setView(view)
        var productCopy = product.copy()
        productCopy = showDialog(view, productCopy)

        alertDialog.setPositiveButton(context.getString(R.string.app_save)) { _, _ ->
            product.quantity = productCopy.quantity
            updateQuantity(product, mView)
        }

        alertDialog.setNegativeButton(context.getString(R.string.app_back)) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun updateQuantity(product: Product, view: View){
        val context = view.context
        if(product.quantity > 99){
            view.textViewQuantityProduct.text = context.getString(R.string.text_quantity_99)
        }else{
            view.textViewQuantityProduct.text = product.quantity.toString()
        }
    }

    private fun showDialog(view: View, product: Product): Product {

        val context = view.context
        view.imageView.loadUrl(context, product.image, view.progressBar)
        view.textViewValue.text = context.getString(R.string.text_concat).format(context.getString(R.string.text_value), DoubleFormatUtil.doubleToString(product.value))
        view.textViewQuantity.text = product.quantity.toString()
        updateTotal(view, product.value * product.quantity)

        view.fab_increase.setOnClickListener {
            product.increaseQuantity()
            view.textViewQuantity.text = product.quantity.toString()
            updateTotal(view, product.value * product.quantity)
        }

        view.fab_decrease.setOnClickListener {
            if (product.decreaseQuantity()) {
                view.textViewQuantity.text = product.quantity.toString()
                updateTotal(view, product.value * product.quantity)
            }
        }
        return product
    }

    private fun updateTotal(view: View, value: Double) {
        val context = view.context
        view.textViewTotal.text = context.getString(R.string.text_total).format(DoubleFormatUtil.doubleToString(value))
    }

    private fun selectedItem(selected: Boolean, view: View) {
        if (selected) {
            view.imageViewOffer.visibility = View.VISIBLE
            view.textViewQuantityProduct.visibility = View.VISIBLE
        } else {
            view.imageViewOffer.visibility = View.INVISIBLE
            view.textViewQuantityProduct.visibility = View.INVISIBLE
        }
    }

    private fun selectedItem(product: Product, view: View) {
        if (selected) {
            if (productsSelected.contains(product)) {
                productsSelected.remove(product)
                selectedItem(false, view)
                view.setBackgroundColor(Color.WHITE)
                product.selected = false
                product.quantity = 1
                count--

                if (count == 0) {
                    if (::actionMode.isInitialized) {
                        actionMode.finish()
                    }
                }
            } else {
                productsSelected.add(product)
                selectedItem(true, view)
                view.setBackgroundColor(Color.LTGRAY)
                product.selected = true
                count++
            }
        }
        setTitle()
    }

    private fun setTitle() {
        if (::actionMode.isInitialized) {
            actionMode.title = count.toString()
        }
    }

    fun getProductsSelected(): List<Product> {
        return productsSelected
    }

    fun finishActionMode() {
        if (::actionMode.isInitialized) {
            actionMode.finish()
        }
    }

    private val actionModeCallbacks = object : ActionMode.Callback {

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            mode?.finish()
            return true
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            if (mode != null) {
                actionMode = mode
                val inflater = actionMode.menuInflater
                inflater.inflate(R.menu.close, menu)
            }
            if (::actionMode.isInitialized) {
                actionMode.title = "1"
            }
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            count = 0
            selected = false
            for (item in productsSelected) {
                item.selected = false
                item.quantity = 1
            }
            productsSelected.clear()
            productFragment.startActionMode(false)

            if (::actionMode.isInitialized) {
                actionMode.finish()
            }
            mode?.finish()
            notifyDataSetChanged()
        }
    }
}