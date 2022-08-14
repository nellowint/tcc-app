package br.com.vieirateam.tcc.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.*
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.adapter.TabAdapter
import br.com.vieirateam.tcc.fragment.FeedbackFragment
import br.com.vieirateam.tcc.model.Feedback
import br.com.vieirateam.tcc.model.Question
import br.com.vieirateam.tcc.model.User
import br.com.vieirateam.tcc.retrofit.RetrofitWebService
import br.com.vieirateam.tcc.util.KeyboardUtil
import br.com.vieirateam.tcc.util.SnackBarUtil
import com.pixelcan.inkpageindicator.InkPageIndicator
import kotlinx.android.synthetic.main.activity_feedback.*
import org.jetbrains.anko.contentView

class FeedbackActivity : AppCompatActivity() {

    private lateinit var user: User
    private lateinit var adapter: TabAdapter
    private lateinit var mViewPager: ViewPager
    private var isKeyboardOpen = false
    private var menuItemVisible = false
    private var message: String? = null
    private var answers: MutableList<Int> = mutableListOf()
    private var fragments: MutableList<FeedbackFragment> = mutableListOf()
    private var questions: MutableList<Question> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        configureViewPager()
        fab_previous_question.setOnClickListener { previousPage() }
        fab_next_question.setOnClickListener { nextPage() }

        val bundle = intent.getBundleExtra("bundle")
        user = bundle.getSerializable("user") as User
        getQuestions()
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

    override fun onDestroy() {
        super.onDestroy()
        hideKeyboard()
    }

    private fun getQuestions() {

        this.questions.clear()

        RetrofitWebService().getQuestions({
            this.questions.addAll(it)
            configureAdapter()
        }, {
            showSnackBar(getString(R.string.app_error))
        })
    }

    private fun configureAdapter() {

        fragments.clear()
        adapter = TabAdapter(supportFragmentManager)
        val pageIndicator = findViewById<InkPageIndicator>(R.id.pageIndicator)

        for (question in questions) {
            val bundle = Bundle()
            val feedbackFragment = FeedbackFragment()
            bundle.putSerializable("question", question)
            bundle.putBoolean("pageIndicator", false)
            feedbackFragment.arguments = bundle
            fragments.add(feedbackFragment)
            adapter.add(feedbackFragment, question.name)
        }

        if (questions.size == questions.lastIndex + 1) {
            val bundle = Bundle()
            val feedbackFragment = FeedbackFragment()
            bundle.putBoolean("pageIndicator", true)
            feedbackFragment.arguments = bundle
            fragments.add(feedbackFragment)
            adapter.add(feedbackFragment, getString(R.string.nav_feedback))
        }

        mViewPager = viewPager
        mViewPager.adapter = adapter
        pageIndicator.setViewPager(mViewPager)
    }

    private fun configureViewPager() {
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                if (!checkIfLastAnswer(position)) {
                    mViewPager.currentItem--
                    return
                }
                when (position) {
                    0 -> fab_previous_question.visibility = View.INVISIBLE
                    questions.size -> {
                        fab_previous_question.visibility = View.INVISIBLE
                        fab_next_question.visibility = View.INVISIBLE
                        val fragment = fragments[fragments.lastIndex]
                        fragment.showKeyboard()
                        isKeyboardOpen = true
                    }
                    else -> {
                        fab_previous_question.visibility = View.VISIBLE
                        fab_next_question.visibility = View.VISIBLE
                        hideKeyboard()
                    }
                }
            }
        })
    }

    private fun previousPage() {
        if (::mViewPager.isInitialized) {
            mViewPager.currentItem--
        }
    }

    private fun nextPage() {
        if (::mViewPager.isInitialized) {
            if (checkIfLastAnswer(mViewPager.currentItem)) {
                mViewPager.currentItem++
            }
        }
    }

    private fun checkIfLastAnswer(position: Int): Boolean {
        if (position == questions.size) {
            answers.clear()
            getFragmentsInformation()
            if (answers.size != questions.size) {
                showSnackBar(getString(R.string.app_error_questions))
                answers.clear()
                return false
            } else {
                menuItemVisible = true
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.send, menu)
        menu.findItem(R.id.send).isVisible = menuItemVisible
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when {
            item?.itemId == android.R.id.home -> {
                hideKeyboard()
                finish()
                return true
            }
            item?.itemId == R.id.send -> {
                this.answers.clear()
                getFragmentsInformation()
                if (this.answers.size == this.questions.size) {
                    hideKeyboard()
                    sendFeedback()
                } else {
                    showSnackBar(getString(R.string.app_error_questions))
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getFragmentsInformation() {

        for (fragment in fragments) {
            val position = fragment.getPosition()
            if (position != null) {
                answers.add(position)
            } else {
                val message = fragment.getMessage()
                if (message != null) {
                    this.message = message
                }
            }
        }
    }

    private fun sendFeedback() {

        val feedback = Feedback(user.email, message, answers)

        RetrofitWebService().postFeedback(feedback, {
            showSnackBar(getString(R.string.text_thanks))
            val handler = Handler()
            handler.postDelayed({
                finish()
            }, 1500)

        }, {
            showSnackBar(getString(R.string.app_error))
        })
    }

    private fun hideKeyboard() {
        try {
            this.contentView?.let {
                KeyboardUtil.hideKeyboard(this, it)
            }
        } catch (ex: IllegalStateException) {
            Log.e("tcc", ex.message.toString())
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
