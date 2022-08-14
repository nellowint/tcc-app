package br.com.vieirateam.tcc.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.extension.loadUrl
import br.com.vieirateam.tcc.model.Entertainment
import br.com.vieirateam.tcc.util.SnackBarUtil
import br.com.vieirateam.tcc.util.UriUtil
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_entertaiment_detail.*
import kotlinx.android.synthetic.main.adapter_app_bar_layout.*
import kotlinx.android.synthetic.main.adapter_app_toolbar.*
import kotlinx.android.synthetic.main.fragment_entertainment_detail.*
import org.jetbrains.anko.contentView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EntertainmentDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entertaiment_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val bundle = intent.getBundleExtra("bundle")
        val entertainment = bundle.getSerializable("entertainment") as Entertainment

        title = entertainment.category
        imageView.loadUrl(this, entertainment.image, progressBar)

        configureAdapter(entertainment)

        fab_share.setOnClickListener {
            configureIntent(entertainment)
        }
    }

    private fun configureIntent(entertainment: Entertainment) {

        doAsync {

            val uri: Uri?
            val intent = Intent(Intent.ACTION_SEND)

            uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val url = getString(R.string.app_server) + entertainment.image
                val bitmap = Picasso.with(this@EntertainmentDetailActivity).load(url).get()
                UriUtil.getUri(this@EntertainmentDetailActivity, bitmap)
            } else {
                UriUtil.getUri(imageView)
            }

            if (uri != null) {

                val message =
                        entertainment.name + "\n\n" +
                                entertainment.category + "\n\n" +
                                entertainment.desc + "\n\n" +
                                entertainment.sub_desc

                intent.putExtra(Intent.EXTRA_SUBJECT, entertainment.category)
                intent.putExtra(Intent.EXTRA_TEXT, message)
                intent.type = "plain/text"

                intent.putExtra(Intent.EXTRA_STREAM, uri)
                intent.type = "image/*"
            }
            uiThread {
                try {
                    startActivity(Intent.createChooser(intent, getString(R.string.text_choose)))
                } catch (exception: android.content.ActivityNotFoundException) {
                    showSnackBar(getString(R.string.app_error_start_intent))
                }
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

    private fun configureAdapter(entertainment: Entertainment) {
        val category = entertainment.category

        if (category == "Receitas") {
            cardView.visibility = View.VISIBLE
            textViewName.text = entertainment.name
            textViewDesc.text = entertainment.desc
            textViewSubDesc.text = entertainment.sub_desc
            textViewTitleDesc.text = getString(R.string.text_ingredient)
            textViewTitleSubDesc.text = getString(R.string.text_preparation)
        } else {
            textViewName.text = entertainment.name
            textViewDesc.text = entertainment.desc
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
