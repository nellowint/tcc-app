package br.com.vieirateam.tcc.activity

import agency.tango.materialintroscreen.MaterialIntroActivity
import agency.tango.materialintroscreen.MessageButtonBehaviour
import agency.tango.materialintroscreen.SlideFragment
import agency.tango.materialintroscreen.SlideFragmentBuilder
import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.fragment.IntroFragment
import br.com.vieirateam.tcc.preference.UserPreference

class IntroActivity : MaterialIntroActivity() {

    private lateinit var introFragment: SlideFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity()

        addSlide(SlideFragmentBuilder()
                .backgroundColor(R.color.colorAccent)
                .buttonsColor(R.color.colorButtonSlide)
                .image(R.drawable.ic_launcher_foreground)
                .title(getString(R.string.app_intro_title1))
                .description(getString(R.string.app_intro_desc1))
                .build())

        addSlide(SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.colorButtonSlide)
                .image(R.drawable.ic_menu_shopping_item)
                .title(getString(R.string.app_intro_title2))
                .description(getString(R.string.app_intro_desc2))
                .build())

        addSlide(SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimaryDark)
                .buttonsColor(R.color.colorButtonSlide)
                .image(R.drawable.ic_menu_share)
                .title(getString(R.string.app_intro_title3))
                .description(getString(R.string.app_intro_desc3))
                .build())

        addSlide(SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.colorButtonSlide)
                .image(R.drawable.ic_menu_send)
                .title(getString(R.string.app_intro_title4))
                .description(getString(R.string.app_intro_desc4))
                .build())

        val neededPermissions = arrayOf(
                Manifest.permission.CALL_PHONE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)

        addSlide(SlideFragmentBuilder()
                .backgroundColor(R.color.colorAccent)
                .buttonsColor(R.color.colorButtonSlide)
                .image(R.drawable.ic_launcher_foreground)
                .title(getString(R.string.app_intro_title5))
                .description(getString(R.string.app_intro_desc5))
                .neededPermissions(neededPermissions)
                .build(),
                MessageButtonBehaviour(View.OnClickListener {
                    showMessage(getString(R.string.app_intro_message5))
                }, getString(R.string.app_intro_button5)))

        introFragment = IntroFragment()
        addSlide(introFragment)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (::introFragment.isInitialized) {
            val fragment = introFragment as IntroFragment
            fragment.uncheckBox()
        }
    }

    private fun startActivity() {
        if (UserPreference.intro) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}