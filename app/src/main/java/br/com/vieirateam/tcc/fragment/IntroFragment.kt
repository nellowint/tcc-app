package br.com.vieirateam.tcc.fragment

import agency.tango.materialintroscreen.SlideFragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.activity.MainActivity
import br.com.vieirateam.tcc.preference.UserPreference
import kotlinx.android.synthetic.main.fragment_intro.*

class IntroFragment : SlideFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_intro, container, false)
    }

    override fun canMoveFurther(): Boolean {
        if (checkbox.isChecked) {
            UserPreference.intro = true
            val intent = Intent(activity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            activity?.finish()
        }
        return checkbox.isChecked
    }

    override fun cantMoveFurtherErrorMessage(): String {
        return getString(R.string.app_intro_label)
    }

    override fun backgroundColor(): Int {
        return R.color.colorAccent
    }

    override fun buttonsColor(): Int {
        return R.color.colorButtonSlide
    }

    fun uncheckBox() {
        checkbox.isChecked = false
    }
}