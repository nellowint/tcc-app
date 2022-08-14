package br.com.vieirateam.tcc.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.model.Question
import kotlinx.android.synthetic.main.fragment_feedback.*
import br.com.vieirateam.tcc.util.KeyboardUtil

class FeedbackFragment : Fragment() {

    private var position: Int = -1
    private lateinit var mView: View
    private var lastPage: Boolean? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_feedback, container, false)
        setHasOptionsMenu(true)
        return mView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        lastPage = arguments?.getBoolean("pageIndicator") as Boolean

        if (lastPage == true) {
            textInputEditTextFeedback.visibility = View.VISIBLE
        } else {
            linearLayoutQuestions.visibility = View.VISIBLE
            val question = arguments?.getSerializable("question") as Question
            textViewQuestion.text = question.name

            if (savedInstanceState != null) {
                this.position = savedInstanceState.getInt("position")
                instanceStateClickListener(this.position)
            }
            configureClickListener()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("position", this.position)
    }

    override fun onResume() {
        super.onResume()

        if (lastPage == true) {
            linearLayoutQuestions.visibility = View.INVISIBLE
            textInputEditTextFeedback.visibility = View.VISIBLE
        } else {
            linearLayoutQuestions.visibility = View.VISIBLE
            textInputEditTextFeedback.visibility = View.INVISIBLE

            if (this.position > -1) {
                instanceStateClickListener(this.position)
            }
        }
    }

    fun showKeyboard() {
        activity?.let {
            KeyboardUtil.showKeyboard(it, textInputEditTextFeedback)
        }
    }

    private fun configureClickListener() {

        imageViewNo.setOnClickListener {
            this.position = 0
            instanceStateClickListener(this.position)
        }

        imageViewNeutral.setOnClickListener {
            this.position = 1
            instanceStateClickListener(this.position)
        }

        imageViewYes.setOnClickListener {
            this.position = 2
            instanceStateClickListener(this.position)
        }
    }

    private fun instanceStateClickListener(position: Int) {

        when (position) {
            0 -> {
                imageViewNo.setColorFilter(ContextCompat.getColor(mView.context, R.color.colorPrimaryDark))
                imageViewNeutral.setColorFilter(ContextCompat.getColor(mView.context, R.color.colorPrimaryLight))
                imageViewYes.setColorFilter(ContextCompat.getColor(mView.context, R.color.colorPrimaryLight))
            }
            1 -> {
                imageViewNo.setColorFilter(ContextCompat.getColor(mView.context, R.color.colorPrimaryLight))
                imageViewNeutral.setColorFilter(ContextCompat.getColor(mView.context, R.color.colorPrimaryDark))
                imageViewYes.setColorFilter(ContextCompat.getColor(mView.context, R.color.colorPrimaryLight))
            }
            2 -> {
                imageViewNo.setColorFilter(ContextCompat.getColor(mView.context, R.color.colorPrimaryLight))
                imageViewNeutral.setColorFilter(ContextCompat.getColor(mView.context, R.color.colorPrimaryLight))
                imageViewYes.setColorFilter(ContextCompat.getColor(mView.context, R.color.colorPrimaryDark))
            }
        }
    }

    fun getMessage(): String? {
        if (lastPage == true) {
            return try {
                textInputEditTextFeedback.text.toString().trim()
            } catch (ex: IllegalStateException) {
                null
            }
        }
        return null
    }

    fun getPosition(): Int? {
        if (lastPage == false && this.position > -1) {
            return this.position
        }
        return null
    }
}