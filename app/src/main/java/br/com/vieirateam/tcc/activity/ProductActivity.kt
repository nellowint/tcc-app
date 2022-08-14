package br.com.vieirateam.tcc.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.extension.addFragment
import br.com.vieirateam.tcc.fragment.ProductFragment
import br.com.vieirateam.tcc.model.User
import kotlinx.android.synthetic.main.adapter_app_toolbar.*

class ProductActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val bundle = intent.getBundleExtra("bundle")
        val user = bundle.getSerializable("user") as User
        title = getString(R.string.nav_product)

        val fragment = ProductFragment()
        bundle.putBoolean("offer", false)
        bundle.putSerializable("user", user)
        fragment.arguments = bundle
        addFragment(R.id.frameLayout, fragment)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
