package br.com.vieirateam.tcc.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.*
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.activity.ShoppingDetailActivity
import br.com.vieirateam.tcc.adapter.ShoppingAdapter
import br.com.vieirateam.tcc.database.DatabaseService
import br.com.vieirateam.tcc.model.Shopping
import br.com.vieirateam.tcc.model.User
import br.com.vieirateam.tcc.util.*
import kotlinx.android.synthetic.main.adapter_shopping.view.*
import kotlinx.android.synthetic.main.fragment_recycler_view.*

class ShoppingFragment : Fragment() {

    private lateinit var user: User
    private lateinit var mView: View
    private var shopping = mutableListOf<Shopping>()

    private var adapter = ShoppingAdapter(shopping, { onClick(it) }, this)

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
        fab_add.visibility = View.VISIBLE
        fab_add.setImageResource(R.drawable.ic_menu_add)
        fab_add.setOnClickListener {
            configureDialog()
        }
        swipeRefresh.isEnabled = false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.user = arguments?.getSerializable("user") as User
    }

    override fun onResume() {
        super.onResume()
        getShopping()
    }

    private fun getShopping() {
        this.shopping.clear()
        val shoppingList = DatabaseService.selectShoppingList(user_token = user.email)
        this.shopping.addAll(shoppingList)
        configureAdapter()
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

    private fun configureAdapter() {
        this.adapter.shopping = shopping
        this.adapter.notifyDataSetChanged()
        if (shopping.isEmpty()) {
            showSnackBar(resources.getString(R.string.app_error_shopping))
        }
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
                configureAdapter()
            }
        }
    }

    private fun onClick(shopping: Shopping) {
        val intent = Intent(activity, ShoppingDetailActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("shopping", shopping)
        bundle.putSerializable("user", user)
        intent.putExtra("bundle", bundle)
        startActivity(intent)
    }

    fun startActionMode(mode: Boolean) {
        if (mode) {
            fab_add.visibility = View.VISIBLE
        } else {
            fab_add.visibility = View.INVISIBLE
        }
    }

    private fun showSnackBar(message: String) {
        try {
            SnackBarUtil.show(mView, message, Snackbar.LENGTH_LONG)
        } catch (ex: IllegalStateException) {
            Log.e("tcc", ex.message.toString())
        }
    }

    fun deleteShopping(shoppingSelected: List<Shopping>) {
        for (shopping in shoppingSelected) {
            this.shopping.remove(shopping)
            DatabaseService.deleteShopping(shopping)
        }
        if (shoppingSelected.size > 1) {
            showSnackBar(getString(R.string.text_lists_delete))
        } else {
            showSnackBar(getString(R.string.text_list_delete))
        }
    }
}