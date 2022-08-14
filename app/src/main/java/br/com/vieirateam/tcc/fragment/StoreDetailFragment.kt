package br.com.vieirateam.tcc.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.database.DatabaseService
import br.com.vieirateam.tcc.model.OfficeHour
import br.com.vieirateam.tcc.model.Store
import br.com.vieirateam.tcc.util.DateFormatUtil
import kotlinx.android.synthetic.main.fragment_store_detail.*

class StoreDetailFragment : Fragment() {

    private var officeHour: MutableList<OfficeHour> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_store_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val store = arguments?.getSerializable("store") as Store
        getOfficeHour(store)
        configureAdapter(store)
    }

    private fun getOfficeHour(store: Store) {

        this.officeHour.clear()
        val hours = DatabaseService.selectOfficeHourList(store.id)
        this.officeHour.addAll(hours)
    }

    private fun configureAdapter(store: Store) {

        textViewName.text = store.name
        textViewAddress.text = store.address
        textViewNeighborhood.text = store.neighborhood
        textViewCity.text = store.city
        textViewPhone.text = store.phone

        if (store.email != null) {
            textViewEmail.text = store.email
        } else {
            cardViewEmail.visibility = View.INVISIBLE
            cardViewEmail.removeAllViews()
        }

        var hours = ""

        for (hour in this.officeHour) {
            if (hour.date != null) {
                hours += DateFormatUtil.formatDate(hour.date!!) + ", "
            }

            hours += hour.weekday + ": " +
                    DateFormatUtil.formatHour(hour.hour_start) + " até " +
                    DateFormatUtil.formatHour(hour.hour_final) + "\n"
        }
        textViewOfficeHour.text = hours
    }
}
