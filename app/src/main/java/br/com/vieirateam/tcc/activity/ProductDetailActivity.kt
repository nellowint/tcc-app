package br.com.vieirateam.tcc.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.database.DatabaseService
import br.com.vieirateam.tcc.extension.loadUrl
import br.com.vieirateam.tcc.model.Product
import br.com.vieirateam.tcc.model.Shopping
import br.com.vieirateam.tcc.model.User
import br.com.vieirateam.tcc.util.*
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.adapter_app_bar_layout.*
import kotlinx.android.synthetic.main.adapter_app_toolbar.*
import kotlinx.android.synthetic.main.adapter_shopping.view.*
import kotlinx.android.synthetic.main.fragment_product_detail.*
import org.jetbrains.anko.contentView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ProductDetailActivity : AppCompatActivity(),
        DialogInterface.OnClickListener {

    private var position = 0
    private lateinit var user: User
    private lateinit var product: Product
    private var shopping: MutableList<Shopping> = mutableListOf()
    private lateinit var stringAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val bundle = intent.getBundleExtra("bundle")
        this.user = bundle.getSerializable("user") as User
        this.product = bundle.getSerializable("product") as Product

        title = product.name + " " + product.measure_name
        configureAdapter(product)
        getShopping()

        fab_shopping.setOnClickListener {
            if (this.shopping.isEmpty()) {
                configureDialog(true)
            } else {
                configureDialog(false)
            }
        }
    }

    private fun configureAdapter(product: Product) {

        textViewName.text = getString(R.string.text_concat).format(product.name, product.measure_name)
        textViewValue.text = DoubleFormatUtil.doubleToString(product.value)

        if (product.validate != null) {
            if (product.offer) {
                val date = product.validate.toString()
                textViewOfferValue.text = getString(R.string.text_validate)
                textViewOffer.text = DateFormatUtil.formatDate(date)
            } else {
                val date = product.validate.toString()
                textViewOfferValue.text = getString(R.string.text_validate_value)
                textViewOffer.text = DateFormatUtil.formatDate(date)
            }
        } else {
            cardView.removeAllViews()
        }

        var listStore = ""

        for (store in product.store_name) {
            listStore += store + "\n"
        }

        textViewStore.text = listStore
        imageView.loadUrl(this, product.image, progressBar)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getShopping() {
        this.shopping.clear()
        val shopping = DatabaseService.selectShoppingList(user.email)
        this.shopping.addAll(shopping)
    }

    private fun configureDialog(empty: Boolean) {
        if (empty) {
            val view = LayoutInflater.from(this).inflate(R.layout.adapter_shopping, null)
            val alertDialog = AlertDialog.Builder(this)

            alertDialog.setCancelable(true)
            alertDialog.setTitle(getString(R.string.nav_shopping))
            alertDialog.setView(view)
            alertDialog.setPositiveButton(getString(R.string.app_save)) { _, _ ->
                onClick(view)
            }
            alertDialog.setNegativeButton(getString(R.string.app_back)) { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = alertDialog.create()
            dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            dialog.show()
        } else {
            val alertDialog = AlertDialog.Builder(this)
            if (!::stringAdapter.isInitialized) {
                stringAdapter = ArrayAdapter(this, R.layout.select_dialog_singlechoice_material)
            }
            stringAdapter.clear()

            for (shopping in this.shopping) {
                stringAdapter.add(shopping.name)
            }
            alertDialog.setTitle(getString(R.string.nav_shopping))
            alertDialog.setSingleChoiceItems(this.stringAdapter, this.position, this)
            alertDialog.setPositiveButton(getString(R.string.text_new_shopping)) { _, _ ->
                configureDialog(true)
            }
            alertDialog.setCancelable(true)
            alertDialog.setNegativeButton(getString(R.string.app_back)) { dialog, _ ->
                dialog.dismiss()
            }
            alertDialog.show()
        }
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        this.position = which
        addToCart(this.position)
        dialog?.dismiss()
    }

    private fun onClick(view: View) {
        val name = view.textInputEditTextName.text.toString().trim()
        when {
            name.isEmpty() -> {
                showSnackBar(getString(R.string.app_error_shopping_alert))
            }
            DatabaseService.selectShoppingName(name) -> {
                showSnackBar(getString(R.string.app_error_shopping_exits))
            }
            else -> {
                val instance = getCurrentDateTime()
                val date = instance.toString("dd/MM/yyyy")
                val shopping = Shopping(name = name, date = date, token = user.email)
                DatabaseService.insertShopping(shopping)
                shopping.id = DatabaseService.selectShoppingId()
                this.shopping.add(shopping)
                this.shopping.sortBy {
                    it.name
                }

                if (!::stringAdapter.isInitialized)
                    stringAdapter = ArrayAdapter(this, R.layout.select_dialog_singlechoice_material)

                for (item in this.shopping) {
                    this.stringAdapter.add(item.name)
                }
                this.position = this.stringAdapter.getPosition(shopping.name)
                addToCart(this.position)
            }
        }
    }

    private fun addToCart(position: Int) {
        doAsync {
            val productList = mutableListOf<Product>()
            productList.add(product)
            ShoppingUtil.addToCart(shopping, productList, stringAdapter, position)

            uiThread {
                showSnackBar(getString(R.string.text_product_to_list))
            }
        }
    }

    private fun showSnackBar(message: String) {
        try {
            this.contentView?.let {
                SnackBarUtil.show(it, message, Snackbar.LENGTH_LONG)
            }
        } catch (ex: IllegalStateException) {
            Log.e("tcc", ex.message.toString())
        }
    }
}