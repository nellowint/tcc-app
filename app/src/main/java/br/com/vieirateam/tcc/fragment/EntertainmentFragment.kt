package br.com.vieirateam.tcc.fragment

import android.app.AlertDialog
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
import android.support.v7.widget.SearchView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.activity.EntertainmentDetailActivity
import br.com.vieirateam.tcc.adapter.EntertainmentAdapter
import br.com.vieirateam.tcc.database.DatabaseService
import br.com.vieirateam.tcc.model.Category
import br.com.vieirateam.tcc.model.Entertainment
import br.com.vieirateam.tcc.retrofit.RetrofitWebService
import br.com.vieirateam.tcc.util.SnackBarUtil
import kotlinx.android.synthetic.main.fragment_recycler_view.*

class EntertainmentFragment : Fragment(),
        SearchView.OnQueryTextListener,
        DialogInterface.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private var positionCategory = 0
    private lateinit var stringAdapter: ArrayAdapter<String>

    private var categories: MutableList<Category> = mutableListOf()
    private var entertainments: MutableList<Entertainment> = mutableListOf()
    private var entertainmentsFilter: MutableList<Entertainment> = mutableListOf()
    private var adapter = EntertainmentAdapter(entertainments) { onClick(it) }
    private lateinit var mView: View

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
        recyclerView.adapter = adapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getEntertainments()
        getCategories()
    }

    override fun onRefresh() {

        val handler = Handler()
        handler.postDelayed({
            try {
                swipeRefresh.isRefreshing = false
                getEntertainments()
                getCategories()
            } catch (ex: IllegalStateException) {
                activity?.finish()
            }
        }, 2000)
    }

    private fun getEntertainments() {

        this.entertainments.clear()
        this.entertainmentsFilter.clear()

        RetrofitWebService().getEntertainments({

            DatabaseService.deleteEntertainments()
            DatabaseService.insertEntertainmentList(it)

            this.entertainments.addAll(it)
            this.entertainmentsFilter = this.entertainments
            configureAdapter(this.entertainments)
        }, {
            val entertainment = DatabaseService.selectEntertainmentList()
            this.entertainments.addAll(entertainment)
            this.entertainmentsFilter = this.entertainments
            configureAdapter(this.entertainments)
        })
    }

    private fun getCategories() {

        this.categories.clear()

        RetrofitWebService().getEntertainmentCategory({

            DatabaseService.deleteCategories()
            DatabaseService.insertCategoryList(it)

            this.categories.addAll(it)
        }, {
            val category = DatabaseService.selectCategoryList()
            this.categories.addAll(category)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.search, menu)
        inflater?.inflate(R.menu.filter, menu)

        if (menu != null) {
            menu.findItem(R.id.menu_category).isVisible = false
            menu.findItem(R.id.menu_store).isVisible = false
            menu.findItem(R.id.menu_minimum).isVisible = false
            menu.findItem(R.id.menu_maximum).isVisible = false
        }

        val search = menu?.findItem(R.id.search)?.actionView as SearchView
        search.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (!swipeRefresh.isRefreshing) {

            if (item?.itemId == R.id.search) {
                return true
            } else if (item?.itemId == R.id.menu_filter) {
                configureDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun configureDialog() {

        val alertDialog = AlertDialog.Builder(activity)

        if (!::stringAdapter.isInitialized)
            stringAdapter = ArrayAdapter(activity, R.layout.select_dialog_singlechoice_material)

        stringAdapter.clear()
        stringAdapter.add(getString(R.string.text_all))

        for (category in this.categories) {
            stringAdapter.add(category.name)
        }

        alertDialog.setTitle(getString(R.string.text_categories))
        alertDialog.setSingleChoiceItems(this.stringAdapter, this.positionCategory, this)
        alertDialog.setCancelable(true)
        alertDialog.setNegativeButton(getString(R.string.app_back)) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {

        this.entertainmentsFilter = this.entertainments
        configureAdapter(this.entertainmentsFilter)

        if (which == 0) {
            positionCategory = 0
            dialog?.dismiss()
            return
        }

        this.positionCategory = which
        val filter: MutableList<Entertainment> = mutableListOf()

        for (entertainment in this.entertainmentsFilter) {
            if (entertainment.category == stringAdapter.getItem(this.positionCategory)) {
                filter.add(entertainment)
            }
        }

        if (filter.isEmpty()) {
            showSnackBar(getString(R.string.text_empty_filter))
        } else {
            this.entertainmentsFilter = filter
            configureAdapter(filter)
        }
        dialog?.dismiss()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val query = newText?.toLowerCase()
        val filter: MutableList<Entertainment> = mutableListOf()
        for (entertainment in this.entertainmentsFilter) {
            val name = entertainment.name.toLowerCase()
            val category = entertainment.category.toLowerCase()
            if (name.contains(query.toString()) || category.contains(query.toString())) {
                filter.add(entertainment)
            }
        }
        configureAdapter(filter)
        return true
    }

    private fun configureAdapter(entertainments: List<Entertainment>) {
        this.adapter.entertainment = entertainments
        this.adapter.notifyDataSetChanged()
    }

    private fun onClick(entertainment: Entertainment) {
        if (!swipeRefresh.isRefreshing) {
            val intent = Intent(activity, EntertainmentDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("entertainment", entertainment)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
    }

    private fun showSnackBar(message: String) {
        try {
            SnackBarUtil.show(mView, message, Snackbar.LENGTH_LONG)
        } catch (ex: IllegalStateException) {
            Log.e("tcc", ex.message.toString())
        }
    }
}