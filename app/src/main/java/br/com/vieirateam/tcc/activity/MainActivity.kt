package br.com.vieirateam.tcc.activity

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.database.DatabaseService
import br.com.vieirateam.tcc.preference.UserPreference
import br.com.vieirateam.tcc.extension.addFragment
import br.com.vieirateam.tcc.fragment.*
import br.com.vieirateam.tcc.model.Notification
import br.com.vieirateam.tcc.model.User
import br.com.vieirateam.tcc.service.NotificationService
import br.com.vieirateam.tcc.util.InternetUtil
import br.com.vieirateam.tcc.util.SnackBarUtil
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.adapter_app_toolbar.*
import kotlinx.android.synthetic.main.activity_main_nav_header.*
import kotlinx.android.synthetic.main.adapter_about.view.*
import org.jetbrains.anko.contentView
import java.io.Serializable
import java.util.*

class MainActivity : AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener {

    private var notification: Serializable? = null
    private var fragment: Fragment = Fragment()
    private var commit = false
    private var bundle = Bundle()
    private var userIsInitialized: Boolean = false

    private lateinit var user: User
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    private lateinit var mFirebaseAuthListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        title = getString(R.string.app_name)

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.app_name, R.string.app_name)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

        val state = savedInstanceState?.getBoolean("userIsInitialized")
        if (state != null) {
            setProgressVisibility(false)
            userIsInitialized = state
        } else {
            setProgressVisibility(true)
        }

        startFirebase()

        val savedTitle = savedInstanceState?.getCharSequence("title")
        if (title != null) {
            title = savedTitle
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean("userIsInitialized", userIsInitialized)
        outState?.putCharSequence("title", title)
    }

    override fun onStart() {
        super.onStart()
        if (::mFirebaseAuth.isInitialized) {
            mFirebaseAuth.addAuthStateListener(mFirebaseAuthListener)
        }
    }

    override fun onStop() {
        super.onStop()
        if (::mFirebaseAuth.isInitialized) {
            mFirebaseAuth.removeAuthStateListener(mFirebaseAuthListener)
        }
    }

    override fun onResume() {
        super.onResume()
        notification = intent.getSerializableExtra("notification")
    }

    override fun onRestart() {
        super.onRestart()
        if (!commit) {
            startFragment()
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
            finish()
        }
    }

    private fun startFirebase() {

        val mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions)
                .build()

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseAuthListener = FirebaseAuth.AuthStateListener {

            val firebaseUser = mFirebaseAuth.currentUser
            if (firebaseUser != null) {
                updateUI(firebaseUser)
            } else {
                logout()
            }
        }
    }

    private fun updateUI(firebaseUser: FirebaseUser) {

        val handler = Handler()

        handler.postDelayed({

            val name = firebaseUser.displayName.toString()
            val email = firebaseUser.email.toString()
            val image = firebaseUser.photoUrl.toString()

            val checkUser = DatabaseService.selectToken(email)

            if (checkUser) {
                user = DatabaseService.selectUser(email)
            } else {
                user = User(name = name, email = email)
                DatabaseService.insertUser(user)
            }

            textViewUser.text = name
            textViewEmail.text = email
            Picasso.with(this).load(image).into(imageViewUser)

            if (::user.isInitialized && !userIsInitialized) {
                startFragment()
                setProgressVisibility(false)
                startNotificationsBackground()
                userIsInitialized = true
            }
        }, 2000)
    }

    private fun setProgressVisibility(visibility: Boolean) {
        if (visibility) {
            progressBar.visibility = View.VISIBLE
            textView.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.INVISIBLE
            textView.visibility = View.INVISIBLE
        }
    }

    private fun startFragment() {
        if (::user.isInitialized) {
            if (notification == null) {
                startProductFragment(true)
            } else {
                startNotificationFragment(notification as Notification)
                intent.removeExtra("notification")
                notification = null
            }
        }
    }

    private fun startNotificationsBackground() {
        if (::user.isInitialized) {

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 8)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)

            val intent = Intent(this, NotificationService::class.java)
            val intervalMillis: Long = 60000
            bundle.putSerializable("user", user)
            intent.putExtra("bundle", bundle)

            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
            alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    intervalMillis,
                    pendingIntent)
        }
    }

    private fun startShoppingFragment() {
        if (::user.isInitialized) {
            fragment = ShoppingFragment()
            bundle.putSerializable("user", user)
            fragment.arguments = bundle
            title = getString(R.string.nav_shopping)
            logEvent(fragment)
            commit = addFragment(R.id.frameLayout, fragment)
        }
    }

    private fun startProductFragment(offer: Boolean) {
        if (InternetUtil.checkInternet(this)) {
            startShoppingFragment()
            showSnackBar(getString(R.string.app_error_internet))
        } else {
            if (::user.isInitialized) {
                fragment = ProductFragment()
                bundle.putBoolean("offer", offer)
                bundle.putSerializable("user", user)
                fragment.arguments = bundle

                title = if (offer) {
                    getString(R.string.nav_offer)
                } else {
                    getString(R.string.nav_product)
                }
                logEvent(fragment)
                commit = addFragment(R.id.frameLayout, fragment)
            }
        }
    }

    private fun startNotificationFragment(notification: Notification?) {
        if (::user.isInitialized) {
            fragment = NotificationFragment()
            bundle.putSerializable("user", user)
            bundle.putBoolean("internet", InternetUtil.checkInternet(this))
            if (notification != null) {
                bundle.putSerializable("notification", notification)
            }
            fragment.arguments = bundle
            title = getString(R.string.nav_notification)
            logEvent(fragment)
            commit = addFragment(R.id.frameLayout, fragment)
        }
    }

    private fun startEntertainmentFragment() {
        fragment = EntertainmentFragment()
        title = getString(R.string.nav_entertainment)
        logEvent(fragment)
        commit = addFragment(R.id.frameLayout, fragment)
    }

    private fun startLocationFragment() {
        fragment = LocationFragment()
        title = getString(R.string.nav_location)
        logEvent(fragment)
        commit = addFragment(R.id.frameLayout, fragment)
    }

    private fun startFeedbackActivity() {
        if (InternetUtil.checkInternet(this)) {
            showSnackBar(getString(R.string.app_error_internet))
        } else {
            if (::user.isInitialized) {
                val intent = Intent(this, FeedbackActivity::class.java)
                bundle.putSerializable("user", user)
                intent.putExtra("bundle", bundle)
                startActivity(intent)
            }
        }
    }

    private fun startAlertDialogAbout() {
        val view = LayoutInflater.from(this).inflate(R.layout.adapter_about, null)
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setCancelable(true)
        alertDialog.setTitle(getString(R.string.text_about))
        alertDialog.setView(view)

        var arrayAbout = ""

        for (about in this.resources.getStringArray(R.array.app_developers)) {
            arrayAbout += about + "\n"
        }
        view.textViewAbout.text = arrayAbout
        alertDialog.setNegativeButton(getString(R.string.app_ok)) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_offer -> {
                startProductFragment(true)
            }
            R.id.nav_product -> {
                startProductFragment(false)
            }
            R.id.nav_entertainment -> {
                startEntertainmentFragment()
            }
            R.id.nav_shopping -> {
                startShoppingFragment()
            }
            R.id.nav_location -> {
                startLocationFragment()
            }
            R.id.nav_notification -> {
                startNotificationFragment(null)
            }
            R.id.nav_about -> {
                startAlertDialogAbout()
            }
            R.id.nav_feedback -> {
                startFeedbackActivity()
            }
            R.id.nav_close -> {
                revoke()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logout() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun revoke() {
        if (::user.isInitialized) {
            UserPreference.notification = true
            DatabaseService.deleteUser(user)
            if (::alarmManager.isInitialized) {
                alarmManager.cancel(pendingIntent)
            }
        }

        mFirebaseAuth.signOut()
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback {
            if (it.isSuccess) {
                logout()
            }
        }
    }

    private fun logEvent(fragment: Fragment) {
        mFirebaseAnalytics.setCurrentScreen(this, fragment.javaClass.simpleName, fragment.javaClass.simpleName)
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

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.e("tcc", p0.errorMessage)
        showSnackBar(getString(R.string.app_error))
    }
}
