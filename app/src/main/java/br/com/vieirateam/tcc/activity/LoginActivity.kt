package br.com.vieirateam.tcc.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.database.DatabaseService
import br.com.vieirateam.tcc.model.User
import br.com.vieirateam.tcc.retrofit.RetrofitWebService
import br.com.vieirateam.tcc.util.InternetUtil
import br.com.vieirateam.tcc.util.SnackBarUtil
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import me.leolin.shortcutbadger.ShortcutBadger
import org.jetbrains.anko.contentView

class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    lateinit var mGoogleApiClient: GoogleApiClient
    lateinit var mFirebaseAuth: FirebaseAuth
    lateinit var mFirebaseAuthListener: FirebaseAuth.AuthStateListener
    private val REQUEST_CODE_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_login)

        if (InternetUtil.checkInternet(this)) {
            showSnackBar(getString(R.string.app_error_internet))
        }

        ShortcutBadger.removeCount(this)
        startFirebase()
        getInstanceFirebase()

        buttonLogin.setOnClickListener {
            signIn()
        }

        buttonClose.setOnClickListener {
            this.finishAffinity()
        }
    }


    override fun onStart() {
        super.onStart()
        mFirebaseAuth.addAuthStateListener(mFirebaseAuthListener)
    }

    override fun onStop() {
        super.onStop()
        if (::mFirebaseAuth.isInitialized) {
            mFirebaseAuth.removeAuthStateListener(mFirebaseAuthListener)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finishAffinity()
    }

    private fun startFirebase() {
        val mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions)
                .build()
    }

    private fun getInstanceFirebase() {
        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseAuthListener = FirebaseAuth.AuthStateListener {
            val firebaseUser = mFirebaseAuth.currentUser
            if (firebaseUser != null) {
                configureUser(firebaseUser)
            }
        }
    }

    private fun configureUser(firebaseUser: FirebaseUser) {

        val name = firebaseUser.displayName.toString()
        val email = firebaseUser.email.toString()

        val user = User(name = name, email = email)

        DatabaseService.insertUser(user)
        RetrofitWebService().postUser(user, {
            Log.d("tcc", "Cadastrado com sucesso")
        }, {
            Log.e("tcc", "Erro ao cadastrar: ${it.message}")
        })

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun signIn() {
        val intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(intent, REQUEST_CODE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            updateUI(result)
        }
    }

    private fun updateUI(result: GoogleSignInResult) {
        if (result.isSuccess) {
            progressBar.visibility = View.VISIBLE
            buttonLogin.visibility = View.INVISIBLE
            buttonClose.visibility = View.INVISIBLE
            result.signInAccount?.let {
                firebaseWithGoogle(it)
            }
        } else {
            Log.e("tcc", result.isSuccess.toString())
            showSnackBar(getString(R.string.app_error))
        }
    }

    private fun firebaseWithGoogle(signInAccount: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(signInAccount.idToken, null)
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (!it.isSuccessful) {
                showSnackBar(getString(R.string.app_error))
                progressBar.visibility = View.INVISIBLE
                buttonLogin.visibility = View.VISIBLE
                buttonClose.visibility = View.VISIBLE
            }
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

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.e("tcc", p0.errorMessage)
        showSnackBar(getString(R.string.app_error))
    }
}
