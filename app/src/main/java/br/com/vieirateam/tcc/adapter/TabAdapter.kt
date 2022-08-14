package br.com.vieirateam.tcc.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class TabAdapter(fragment: FragmentManager?) : FragmentPagerAdapter(fragment) {

    private var listFragments = ArrayList<Fragment>()
    private var listFragmentsTitle = ArrayList<String>()

    fun add(fragment: Fragment, title: String) {
        this.listFragments.add(fragment)
        this.listFragmentsTitle.add(title)
    }

    override fun getItem(position: Int): Fragment {
        return listFragments[position]
    }

    override fun getCount(): Int {
        return listFragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return listFragmentsTitle[position]
    }
}