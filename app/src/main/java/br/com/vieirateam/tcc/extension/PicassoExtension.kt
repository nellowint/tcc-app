package br.com.vieirateam.tcc.extension

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.squareup.picasso.Picasso
import br.com.vieirateam.tcc.R

fun ImageView.loadUrl(context: Context, url: String?, progress: ProgressBar) {

    if (url != null) {
        progress.visibility = View.VISIBLE
        Picasso
                .with(context)
                .load(context.getString(R.string.app_server) + url)
                .fit()
                .error(R.mipmap.ic_launcher_round)
                .into(this, object : com.squareup.picasso.Callback {

                    override fun onSuccess() {
                        progress.visibility = View.GONE
                    }

                    override fun onError() {
                        progress.visibility = View.GONE
                    }
                })
    } else {
        progress.visibility = View.INVISIBLE
    }
}