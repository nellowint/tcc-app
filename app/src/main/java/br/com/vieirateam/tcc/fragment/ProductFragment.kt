package br.com.vieirateam.tcc.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import android.widget.ArrayAdapter
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.activity.ProductDetailActivity
import br.com.vieirateam.tcc.adapter.ProductAdapter
import br.com.vieirateam.tcc.retrofit.RetrofitWebService
import kotlinx.android.synthetic.main.fragment_recycler_view.*
import android.util.Log
import br.com.vieirateam.tcc.database.DatabaseService
import br.com.vieirateam.tcc.model.*
import br.com.vieirateam.tcc.preference.UserPreference
import br.com.vieirateam.tcc.util.*
import kotlinx.android.synthetic.main.adapter_about.view.*
import kotlinx.android.synthetic.main.adapter_shopping.view.*
import org.jetbrains.anko.*

class ProductFragment : Fragment(),
        SearchView.OnQueryTextListener,
        DialogInterface.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private var offer = false
    private var positionCategory = 0
    private var positionStore = 0
    private var positionShopping = 0
    private var filterType = 0

    private var products: MutableList<Product> = mutableListOf()
    private var productsFilter: MutableList<Product> = mutableListOf()
    private var adapter = ProductAdapter(products, { onClick(it) }, this)
    private var categories: MutableList<Category> = mutableListOf()
    private var stores: MutableList<Store> = mutableListOf()
    private var shopping: MutableList<Shopping> = mutableListOf()

    private lateinit var user: User
    private lateinit var mView: View
    private val searchHistory = SearchHistory()
    private lateinit var stringAdapter: ArrayAdapter<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_recycler_view, container, false)
        setHasOptionsMenu(true)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefresh.setOnRefreshListener(this)
        swipeRefresh.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.colorAccent))

        val configuration = resources.configuration

        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        } else {
            recyclerView.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        }

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = this.adapter
        fab_add.setImageResource(R.drawable.ic_menu_shopping_item)
        fab_add.setOnClickListener {
            if (this.shopping.isEmpty()) {
                configureDialog()
            } else {
                configureDialog(2)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val offer = arguments?.getBoolean("offer")
        this.user = arguments?.getSerializable("user") as User

        if (offer != null) {
            this.offer = offer
        }

        if (!UserPreference.product) {
            noteDialog()
        }

        getProducts(this.offer)
        getCategories()
        getStores()
    }

    private fun noteDialog() {
        val view = LayoutInflater.from(activity).inflate(R.layout.adapter_about, null)
        val alertDialog = AlertDialog.Builder(activity)

        val check = view.checkbox
        check.visibility = View.VISIBLE
        check.isChecked = UserPreference.product
        view.textViewAbout.text = getString(R.string.text_show_note_product)

        alertDialog.setView(view)
        alertDialog.setCancelable(true)
        alertDialog.setTitle(getString(R.string.app_note))
        alertDialog.setPositiveButton(getString(R.string.app_ok)) { dialog, _ ->
            UserPreference.product = check.isChecked
            dialog.dismiss()
        }
        alertDialog.show()
    }

    override fun onResume() {
        super.onResume()
        searchHistory.search.clear()
        getShopping()
    }

    override fun onPause() {
        super.onPause()
        adapter.finishActionMode()
    }

    override fun onRefresh() {

        val handler = Handler()
        handler.postDelayed({
            try {
                swipeRefresh.isRefreshing = false
                getProducts(this.offer)
                getCategories()
                getStores()
                getShopping()
                searchHistory.search.clear()
                adapter.finishActionMode()
            } catch (ex: IllegalStateException) {
                activity?.finish()
            }
        }, 2000)
    }

    private fun getProducts(offer: Boolean) {

        this.products.clear()
        this.productsFilter.clear()

        RetrofitWebService().getProducts(offer, {
            this.products.addAll(it)
            this.productsFilter = this.products
            configureAdapter(this.products)
        }, {
            showSnackBar(getString(R.string.app_error))
        })
    }

    private fun getCategories() {

        this.categories.clear()

        RetrofitWebService().getCategories({
            this.categories.addAll(it)
        }, {
            showSnackBar(getString(R.string.app_error))
        })
    }

    private fun getStores() {

        this.stores.clear()

        RetrofitWebService().getStores({
            this.stores.addAll(it)
        }, {
            showSnackBar(getString(R.string.app_error))
        })
    }

    private fun getShopping() {
        this.shopping.clear()
        val shopping = DatabaseService.selectShoppingList(user.email)
        this.shopping.addAll(shopping)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.search, menu)
        inflater?.inflate(R.menu.filter, menu)

        val search = menu?.findItem(R.id.search)?.actionView as SearchView
        search.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        this.searchHistory.addSearch(query.toString())
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        val query = newText?.toLowerCase()
        val filter: MutableList<Product> = mutableListOf()
        for (product in this.productsFilter) {
            val name = product.name.toLowerCase()
            val category = product.category_name.toLowerCase()
            if (name.contains(query.toString()) || category.contains(query.toString())) {
                filter.add(product)
            }
        }
        configureAdapter(filter)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (!swipeRefresh.isRefreshing) {
            when (item?.itemId) {
                R.id.search -> {
                    return true
                }
                R.id.menu_category -> {
                    configureDialog(0)
                    return true
                }
                R.id.menu_store -> {
                    configureDialog(1)
                    return true
                }
                R.id.menu_minimum -> {
                    this.productsFilter.sortBy {
                        it.value
                    }
                    configureAdapter(this.productsFilter)
                    return true
                }
                R.id.menu_maximum -> {
                    this.productsFilter.sortByDescending {
                        it.value
                    }
                    configureAdapter(this.productsFilter)
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun configureDialog() {

        val view = LayoutInflater.from(activity).inflate(R.layout.adapter_shopping, null)
        val alertDialog = AlertDialog.Builder(activity)

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
    }

    private fun configureDialog(filter: Int) {

        val alertDialog = AlertDialog.Builder(activity)

        if (!::stringAdapter.isInitialized)
            stringAdapter = ArrayAdapter(activity, R.layout.select_dialog_singlechoice_material)

        stringAdapter.clear()

        when (filter) {
            0 -> {
                stringAdapter.add(getString(R.string.text_all))

                for (category in categories) {
                    stringAdapter.add(category.name)
                }

                filterType = 0
                alertDialog.setTitle(getString(R.string.text_category))
                alertDialog.setSingleChoiceItems(this.stringAdapter, this.positionCategory, this)
            }
            1 -> {
                stringAdapter.add(getString(R.string.text_all))

                for (store in stores) {
                    stringAdapter.add(store.name)
                }

                filterType = 1
                alertDialog.setTitle(getString(R.string.text_store))
                alertDialog.setSingleChoiceItems(this.stringAdapter, this.positionStore, this)
            }
            2 -> {
                for (shopping in shopping) {
                    stringAdapter.add(shopping.name)
                }

                filterType = 2
                alertDialog.setTitle(getString(R.string.nav_shopping))
                alertDialog.setSingleChoiceItems(this.stringAdapter, this.positionShopping, this)
                alertDialog.setPositiveButton(getString(R.string.text_new_shopping)) { _, _ ->
                    configureDialog()
                }
            }
        }
        alertDialog.setCancelable(true)
        alertDialog.setNegativeButton(getString(R.string.app_back)) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun configureAdapter(products: List<Product>) {
        this.adapter.products = products
        this.adapter.notifyDataSetChanged()
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
                    stringAdapter = ArrayAdapter(activity, R.layout.select_dialog_singlechoice_material)

                for (item in this.shopping) {
                    this.stringAdapter.add(item.name)
                }
                this.positionShopping = this.stringAdapter.getPosition(shopping.name)
                addToCart(this.positionShopping)
            }
        }
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        if (filterType == 2) {
            this.positionShopping = which
            addToCart(which)
        } else {
            if (which == 0) {
                positionCategory = 0
                positionStore = 0
                this.productsFilter = this.products
                configureAdapter(this.productsFilter)
                dialog?.dismiss()
                return
            }

            val filter: MutableList<Product> = mutableListOf()
            this.productsFilter = this.products
            configureAdapter(this.productsFilter)

            when (filterType) {
                0 -> {
                    this.positionCategory = which
                    for (product in this.productsFilter) {
                        if (product.category_name == this.stringAdapter.getItem(positionCategory)) {
                            filter.add(product)
                        }
                    }
                }
                1 -> {
                    this.positionStore = which
                    for (product in this.productsFilter) {
                        for (store in product.store_name) {
                            if (store == this.stringAdapter.getItem(positionStore)) {
                                filter.add(product)
                            }
                        }
                    }
                }
            }
            if (filter.isEmpty()) {
                showSnackBar(getString(R.string.text_empty_filter))
            } else {
                this.productsFilter = filter
                configureAdapter(filter)
            }
        }
        dialog?.dismiss()
    }

    private fun addToCart(position: Int) {
        doAsync {
            ShoppingUtil.addToCart(shopping, adapter.getProductsSelected(), stringAdapter, position)

            uiThread {
                if (adapter.getProductsSelected().size > 1) {
                    showSnackBar(getString(R.string.text_products_to_list))
                } else {
                    showSnackBar(getString(R.string.text_product_to_list))
                }
                startActionMode(false)
                adapter.finishActionMode()
            }
        }
    }

    private fun onClick(product: Product) {
        if (!swipeRefresh.isRefreshing) {
            postResult(product)
            val intent = Intent(activity, ProductDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("product", product)
            bundle.putSerializable("user", user)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
    }

    private fun postResult(product: Product) {
        doAsync {
            searchHistory.token = user.email
            searchHistory.product_name = product.name + " " + product.measure_name
            searchHistory.product_category = product.category_name
            for (result in searchHistory.search) {
                val productName = product.name + " " + product.measure_name.toLowerCase()
                if (productName.contains(result.toLowerCase())) {
                    searchHistory.product_visualized = true
                    break
                }
            }
            RetrofitWebService().postSearch(
                    searchHistory,
                    {
                        Log.d("tcc", "Dados enviados com sucesso.")
                    }, {
                Log.e("tcc", it.message)
            })
        }
    }

    fun startActionMode(mode: Boolean) {
        if (mode) {
            fab_add.visibility = View.VISIBLE
        } else {
            fab_add.visibility = View.INVISIBLE
        }
    }

    fun showSnackBar(message: String) {
        try {
            SnackBarUtil.show(mView, message, Snackbar.LENGTH_LONG)
        } catch (ex: IllegalStateException) {
            Log.e("tcc", ex.message.toString())
        }
    }
}