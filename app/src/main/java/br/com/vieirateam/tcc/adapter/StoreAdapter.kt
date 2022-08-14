package br.com.vieirateam.tcc.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.extension.loadUrl
import br.com.vieirateam.tcc.model.Store
import kotlinx.android.synthetic.main.adapter_recycler_view.view.*

class StoreAdapter(var stores: List<Store>,
                   val onClick: (Store) -> Unit) : RecyclerView.Adapter<StoreAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_recycler_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = stores.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val store = stores[position]
        val view = holder.itemView

        with(view) {
            textViewName.text = store.name
            textViewValue.text = store.phone
            imageView.loadUrl(context, store.image, progressBar)
            setOnClickListener {
                onClick(store)
            }
        }
    }
}