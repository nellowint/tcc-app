package br.com.vieirateam.tcc.fragment

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.SearchView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.*
import android.widget.ImageView
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.adapter.ShoppingItemAdapter
import br.com.vieirateam.tcc.database.DatabaseService
import br.com.vieirateam.tcc.extension.loadUrl
import br.com.vieirateam.tcc.model.Shopping
import br.com.vieirateam.tcc.model.ShoppingItem
import br.com.vieirateam.tcc.model.User
import br.com.vieirateam.tcc.preference.UserPreference
import br.com.vieirateam.tcc.util.DoubleFormatUtil
import br.com.vieirateam.tcc.util.SnackBarUtil
import br.com.vieirateam.tcc.util.UriUtil
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.adapter_about.view.*
import kotlinx.android.synthetic.main.adapter_shopping.view.*
import kotlinx.android.synthetic.main.adapter_shopping_item.view.*
import kotlinx.android.synthetic.main.fragment_recycler_view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ShoppingDetailFragment : Fragment(),
        SearchView.OnQueryTextListener {

    private lateinit var mView: View
    private lateinit var mSnackbar: Snackbar
    private lateinit var shopping: Shopping
    private lateinit var user: User
    private var shoppingItem = mutableListOf<ShoppingItem>()
    private var shoppingItemFilter = mutableListOf<ShoppingItem>()
    private val adapter = ShoppingItemAdapter(shoppingItem, { onClick(it) }, this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_recycler_view, container, false)
        setHasOptionsMenu(true)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        swipeRefresh.isRefreshing = false
        swipeRefresh.isEnabled = false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (!UserPreference.shopping) {
            configureDialog()
        }

        if (::mSnackbar.isInitialized) {
            mSnackbar.dismiss()
        }

        val shopping = arguments?.getSerializable("shopping") as Shopping
        val user = arguments?.getSerializable("user") as User
        this.user = user
        this.shopping = shopping
        getItems(false)
    }

    override fun onResume() {
        super.onResume()
        getItems(true)
    }

    override fun onDetach() {
        super.onDetach()
        if (::mSnackbar.isInitialized) {
            mSnackbar.dismiss()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.search, menu)
        inflater?.inflate(R.menu.share, menu)

        val search = menu?.findItem(R.id.search)?.actionView as SearchView
        search.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.search -> {
                return true
            }
            R.id.delete -> {
                configureDialog(true)
            }
            R.id.rename -> {
                configureDialog(false)
            }
            R.id.share -> {
                configureIntent()
                return true
            }
            R.id.note -> {
                configureDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun configureIntent() {

        doAsync {

            val uri: Uri?
            val intent = Intent(Intent.ACTION_SEND)
            val shoppingItems: MutableList<String> = mutableListOf()

            uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val url = getString(R.string.app_server) + getString(R.string.app_server_image)
                val bitmap = Picasso.with(mView.context).load(url).get()
                UriUtil.getUri(mView.context, bitmap)
            } else {
                val imageView = ImageView(mView.context)
                imageView.setImageResource(R.mipmap.ic_launcher_round)
                UriUtil.getUri(imageView)
            }

            val value = DoubleFormatUtil.doubleToString(shopping.value)
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.nav_shopping))
            shoppingItems.add(getString(R.string.nav_shopping) + ": " + shopping.name + "\n")
            shoppingItems.add(getString(R.string.text_total).format(value) + "\n\n")

            for (items in shoppingItem) {
                val product = "[" + items.quantity.toString() + " ] " + items.product.name + " " + items.product.measure_name
                shoppingItems.add(product + "\n")
            }

            if (uri != null) {
                var shoppingItemsDescription = ""

                for (items in shoppingItems) {
                    shoppingItemsDescription += items + "\n"
                }

                intent.putExtra(Intent.EXTRA_TEXT, shoppingItemsDescription)
                intent.type = "plain/text"

                intent.putExtra(Intent.EXTRA_STREAM, uri)
                intent.type = "image/*"
            }

            uiThread {
                try {
                    startActivity(Intent.createChooser(intent, getString(R.string.text_choose)))
                } catch (exception: android.content.ActivityNotFoundException) {
                    showSnackBar(getString(R.string.app_error_start_intent), Snackbar.LENGTH_LONG)
                }
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val query = newText?.toLowerCase()
        val filter: MutableList<ShoppingItem> = mutableListOf()
        for (shoppingItem in this.shoppingItemFilter) {
            val name = shoppingItem.product.name.toLowerCase()
            val category = shoppingItem.product.category_name.toLowerCase()
            if (name.contains(query.toString()) || category.contains(query.toString())) {
                filter.add(shoppingItem)
            }
        }
        configureAdapter(filter)
        return true
    }

    private fun getItems(resumed: Boolean) {

        if (resumed) {
            this.shopping = DatabaseService.selectShopping(user.email, shopping.id)
        }

        this.shoppingItem = DatabaseService.selectShoppingItemList(this.shopping.id) as ArrayList
        this.shoppingItemFilter = this.shoppingItem
        configureAdapter(this.shoppingItem)

        if (this.shoppingItem.isEmpty()) {
            showSnackBar(getString(R.string.text_list_empty), Snackbar.LENGTH_INDEFINITE)
        } else {
            updateTotalShopping(shopping.value)
        }
    }

    private fun configureAdapter(shoppingItem: List<ShoppingItem>) {
        this.adapter.shoppingItem = shoppingItem
        this.adapter.notifyDataSetChanged()
    }

    fun updateTotalShopping(total: Double) {
        showSnackBar(getString(R.string.text_total).format(DoubleFormatUtil.doubleToString(total)), Snackbar.LENGTH_INDEFINITE)
    }

    private fun updateTotalShoppingItem(view: View, total: Double) {
        view.textViewTotal.text = getString(R.string.text_total).format(DoubleFormatUtil.doubleToString(total))
    }

    private fun onClick(shoppingItem: ShoppingItem) {
        configureDialog(shoppingItem)
    }

    private fun configureDialog() {
        val view = LayoutInflater.from(activity).inflate(R.layout.adapter_about, null)
        val alertDialog = AlertDialog.Builder(activity)

        val check = view.checkbox
        check.visibility = View.VISIBLE
        check.isChecked = UserPreference.shopping
        view.textViewAbout.text = getString(R.string.text_show_note_shopping)

        alertDialog.setView(view)
        alertDialog.setCancelable(true)
        alertDialog.setTitle(getString(R.string.app_note))
        alertDialog.setPositiveButton(getString(R.string.app_ok)) { dialog, _ ->
            UserPreference.shopping = check.isChecked
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun configureDialog(option: Boolean) {

        val view = LayoutInflater.from(activity).inflate(R.layout.adapter_shopping, null)
        val alertDialog = AlertDialog.Builder(activity)
        alertDialog.setCancelable(true)

        if (option) {
            alertDialog.setTitle(getString(R.string.text_delete))
            alertDialog.setPositiveButton(getString(R.string.app_yes)) { _, _ ->
                deleteShopping(shopping)
                activity?.finish()
            }
            alertDialog.setNegativeButton(getString(R.string.app_no)) { dialog, _ ->
                dialog.dismiss()
            }
            alertDialog.show()
        } else {
            alertDialog.setTitle(getString(R.string.nav_shopping))
            alertDialog.setView(view)
            view.textInputEditTextName.setText(shopping.name)
            alertDialog.setPositiveButton(getString(R.string.app_save)) { _, _ ->
                onClick(view)
            }
            alertDialog.setNegativeButton(getString(R.string.app_back)) { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = alertDialog.create()
            dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            dialog.show()
        }
    }

    private fun onClick(view: View) {
        val name = view.textInputEditTextName.text.toString().trim()
        when {
            name.isEmpty() -> {
                showSnackBar(getString(R.string.app_error_shopping_alert), Snackbar.LENGTH_LONG)
            }
            DatabaseService.selectShoppingName(name) -> {
                showSnackBar(getString(R.string.app_error_shopping_exits), Snackbar.LENGTH_LONG)
            }
            else -> {
                shopping.name = name
                updateShopping(shopping)
                (mView.context as AppCompatActivity).title = shopping.name
            }
        }
    }

    private fun configureDialog(shoppingItem: ShoppingItem) {

        val view = LayoutInflater.from(activity).inflate(R.layout.adapter_shopping_item, null)
        val alertDialog = AlertDialog.Builder(activity)

        alertDialog.setTitle(shoppingItem.product.name + " " + shoppingItem.product.measure_name)
        alertDialog.setCancelable(true)
        alertDialog.setView(view)
        val item = shoppingItem.copy()
        val updateShoppingItem = showDialog(view, item)

        alertDialog.setPositiveButton(getString(R.string.app_save)) { _, _ ->
            saveShoppingItem(updateShoppingItem)
            shoppingItem.quantity = item.quantity
            shoppingItem.value = item.value
        }
        alertDialog.setNegativeButton(getString(R.string.app_back)) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun showDialog(view: View, shoppingItem: ShoppingItem): ShoppingItem {

        view.imageView.loadUrl(mView.context, shoppingItem.product.image, view.progressBar)

        view.textViewValue.text = getString(R.string.text_concat).format(getString(R.string.text_value), DoubleFormatUtil.doubleToString(shoppingItem.product.value))
        view.textViewQuantity.text = shoppingItem.quantity.toString()
        updateTotalShoppingItem(view, shoppingItem.value)

        view.fab_increase.setOnClickListener {
            shoppingItem.increaseQuantity()
            view.textViewQuantity.text = shoppingItem.quantity.toString()
            updateTotalShoppingItem(view, shoppingItem.value)
            shopping.updateValue(shoppingItem.product.value)
        }

        view.fab_decrease.setOnClickListener {
            if (shoppingItem.decreaseQuantity()) {
                view.textViewQuantity.text = shoppingItem.quantity.toString()
                updateTotalShoppingItem(view, shoppingItem.value)
                shopping.updateValue(-shoppingItem.product.value)
            }
        }
        return shoppingItem
    }

    private fun saveShoppingItem(shoppingItem: ShoppingItem) {
        doAsync {
            DatabaseService.updateShoppingItem(shoppingItem)
            DatabaseService.updateShopping(shopping)

            uiThread {
                it.adapter.getView().textViewValue.text = DoubleFormatUtil.doubleToString(shoppingItem.value)
                it.adapter.getView().textViewQuantity.text = getString(R.string.text_quantity).format(shoppingItem.quantity)
                it.updateTotalShopping(shopping.value)
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

    private fun deleteShopping(shopping: Shopping) {
        DatabaseService.deleteShopping(shopping)
    }

    private fun updateShopping(shopping: Shopping) {
        DatabaseService.updateShopping(shopping)
    }

    fun deleteShoppingItem(shoppingItemSelected: List<ShoppingItem>) {
        var value = 0.0
        for (shoppingItem in shoppingItemSelected) {
            value += shoppingItem.value
            DatabaseService.deleteShoppingItem(shoppingItem)
        }

        if (shoppingItemSelected.size == shoppingItem.size) {
            shopping.value = 0.0
        } else {
            shopping.updateValue(-value)
        }
        DatabaseService.updateShopping(shopping)
        getItems(false)
        updateTotalShopping(shopping.value)
    }
}