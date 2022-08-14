package br.com.vieirateam.tcc.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.MenuItem
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.extension.addFragment
import br.com.vieirateam.tcc.fragment.ShoppingDetailFragment
import br.com.vieirateam.tcc.model.Shopping
import br.com.vieirateam.tcc.model.User
import br.com.vieirateam.tcc.util.InternetUtil
import br.com.vieirateam.tcc.util.SnackBarUtil
import kotlinx.android.synthetic.main.activity_shopping_detail.*
import kotlinx.android.synthetic.main.adapter_app_toolbar.*
import org.jetbrains.anko.contentView

class ShoppingDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val bundle = intent.getBundleExtra("bundle")
        val shopping = bundle.getSerializable("shopping") as Shopping
        val user = bundle.getSerializable("user") as User
        title = shopping.name

        val fragment = ShoppingDetailFragment()
        bundle.putSerializable("shopping", shopping)
        bundle.putSerializable("user", user)
        fragment.arguments = bundle
        addFragment(R.id.frameLayout, fragment)

        fab_add.setOnClickListener {
            if (InternetUtil.checkInternet(this)) {
                showSnackBar(getString(R.string.app_error_internet))
                val handler = Handler()
                handler.postDelayed({
                    fragment.updateTotalShopping(shopping.value)
                }, 3000)
            } else {
                configureIntent(user)
            }
        }
    }

    private fun configureIntent(user: User) {
        val intent = Intent(this, ProductActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("user", user)
        intent.putExtra("bundle", bundle)
        startActivity(intent)
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
