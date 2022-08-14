package br.com.vieirateam.tcc.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.activity.StoreDetailActivity
import br.com.vieirateam.tcc.adapter.StoreAdapter
import br.com.vieirateam.tcc.database.DatabaseService
import br.com.vieirateam.tcc.model.Store
import br.com.vieirateam.tcc.retrofit.RetrofitWebService
import kotlinx.android.synthetic.main.fragment_recycler_view.*

class LocationFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var mView: View
    private var stores: MutableList<Store> = mutableListOf()
    private var adapter = StoreAdapter(stores) { onClick(it) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_recycler_view, container, false)
        return mView
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getStores()
    }

    override fun onRefresh() {

        val handler = Handler()
        handler.postDelayed({
            try {
                swipeRefresh.isRefreshing = false
                getStores()
            } catch (ex: IllegalStateException) {
                activity?.finish()
            }
        }, 2000)
    }

    private fun getStores() {

        this.stores.clear()

        RetrofitWebService().getStores({

            DatabaseService.deleteStores()
            DatabaseService.deleteOfficeHours()

            DatabaseService.insertStoreList(it)
            this.stores.addAll(it)
            for (store in it) {
                getOfficeHour(store)
            }
            configureAdapter()

        }, {
            val store = DatabaseService.selectStoreList()
            this.stores.addAll(store)
            configureAdapter()
        })
    }

    private fun getOfficeHour(store: Store) {
        RetrofitWebService().getOfficeHour(store.id, {
            for (officeHour in it) {
                officeHour.store = store.id
                DatabaseService.insertOfficeHour(officeHour)
            }
        }, {

        })
    }

    private fun configureAdapter() {
        this.adapter.stores = stores
        this.adapter.notifyDataSetChanged()
    }

    private fun onClick(store: Store) {
        if (!swipeRefresh.isRefreshing) {
            val intent = Intent(activity, StoreDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("store", store)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
    }
}