package br.com.vieirateam.tcc.activity

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.MenuItem
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.adapter.TabAdapter
import br.com.vieirateam.tcc.extension.loadUrl
import br.com.vieirateam.tcc.fragment.StoreDetailFragment
import br.com.vieirateam.tcc.fragment.StoreMapFragment
import br.com.vieirateam.tcc.model.Store
import br.com.vieirateam.tcc.util.SnackBarUtil
import kotlinx.android.synthetic.main.activity_store_detail.*
import kotlinx.android.synthetic.main.adapter_app_bar_layout.*
import kotlinx.android.synthetic.main.adapter_app_toolbar.*
import kotlinx.android.synthetic.main.fragment_tab_layout.*
import org.jetbrains.anko.contentView

class StoreDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val bundle = intent.getBundleExtra("bundle")
        val store = bundle.getSerializable("store") as Store

        title = store.name
        imageView.loadUrl(this, store.image, progressBar)
        configureAdapter(bundle, store)

        fab_call.setOnClickListener {
            val uri = Uri.parse("tel:${store.phone}")
            val intent = Intent(Intent.ACTION_DIAL, uri)
            try {
                startActivity(intent)
            } catch (exception: android.content.ActivityNotFoundException) {
                showSnackBar(getString(R.string.app_error_start_intent))
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun configureAdapter(bundle: Bundle, store: Store) {

        val adapter = TabAdapter(supportFragmentManager)
        val storeDetailFragment = StoreDetailFragment()
        val storeMapFragment = StoreMapFragment()

        bundle.putSerializable("store", store)

        storeDetailFragment.arguments = bundle
        storeMapFragment.arguments = bundle

        adapter.add(storeDetailFragment, getString(R.string.text_about))
        adapter.add(storeMapFragment, getString(R.string.text_location))
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
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
