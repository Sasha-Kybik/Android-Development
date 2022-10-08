package com.example.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

const val EXTRA_ANSWER_SHOWN = "com.example.geoquiz.answer_shown"
private const val EXTRA_ANSWER_IS_TRUE = "com.example.geoquiz.answer_is_true"

class CheatActivity : AppCompatActivity() {
    private lateinit var answerTextView: TextView
    private lateinit var showAnswerButton: Button
    private lateinit var versionAPITextView: TextView

    private var answerIsTrue = false
    private var answerWasShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        val answerWasShown = savedInstanceState?.getBoolean(EXTRA_ANSWER_SHOWN, false) ?: false

        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        answerTextView = findViewById(R.id.answer_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)
        versionAPITextView = findViewById(R.id.version_API_text_view)

        showAnswerButton.setOnClickListener {
            if (!answerWasShown) {
                showAnswer(answerIsTrue, true)
            }
        }

        showAnswer(answerIsTrue, answerWasShown)
        versionAndroidAPI()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(EXTRA_ANSWER_SHOWN, answerWasShown)
    }

    private fun showAnswer(answerIsTrue: Boolean, show: Boolean) {
        if (show) {
            val answerText = when {
                answerIsTrue -> R.string.true_button
                else -> R.string.false_button
            }
            answerTextView.setText(answerText)
        }
        answerWasShown = show
        setAnswerShownResult(show)
    }

    private fun versionAndroidAPI() {
        val versionAndroid = Build.VERSION.RELEASE
        val versionAPI = Build.VERSION.SDK_INT
        //versionAPITextView.setText("Android $versionAndroid (API level $versionAPI)")
        versionAPITextView.text = String.format("${resources.getString(R.string.android_text)} " +
                                                "$versionAndroid " +
                                                "(${resources.getString(R.string.api_level_text)} " +
                                                "$versionAPI)")
    }

    private fun setAnswerShownResult(answerWasShown: Boolean) {
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, answerWasShown)
        }
        setResult(Activity.RESULT_OK, data)
    }

    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }
}