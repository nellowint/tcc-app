package br.com.vieirateam.tcc.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.extension.loadUrl
import br.com.vieirateam.tcc.model.Entertainment
import kotlinx.android.synthetic.main.adapter_product.view.*

class EntertainmentAdapter(var entertainment: List<Entertainment>,
                           val onClick: (Entertainment) -> Unit) : RecyclerView.Adapter<EntertainmentAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_product, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = entertainment.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val entertainment = entertainment[position]
        val view = holder.itemView

        with(view) {
            textViewNameProduct.text = entertainment.name
            textViewValueProduct.text = entertainment.category
            imageViewProduct.loadUrl(context, entertainment.image, progressBarProduct)
            setOnClickListener {
                onClick(entertainment)
            }
        }
    }
}