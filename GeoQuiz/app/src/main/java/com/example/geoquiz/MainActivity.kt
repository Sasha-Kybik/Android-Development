package com.example.geoquiz

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val CHEAT_COUNT_LIMIT = "com.example.geoquiz.cheatCount"
private const val ANSWERS = "com.example.geoquiz.answers"
private const val QUESTIONS_ANSWERED = "com.example.geoquiz.questionsAnswered"

class MainActivity : AppCompatActivity() {
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var cheatButton: Button
    private lateinit var questionTextView: TextView
    private lateinit var countHintTextView: TextView

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProvider(this).get(QuizViewModel::class.java)
    }

    @SuppressLint("Ограничение по API")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

        val cheatCount = savedInstanceState?.getInt(CHEAT_COUNT_LIMIT, 3) ?: 3
        quizViewModel.cheatCountLimit = cheatCount

        val answers = (savedInstanceState?.getSerializable(ANSWERS) ?: BooleanArray(quizViewModel.questionBankSize)) as BooleanArray
        quizViewModel.answersOfUser = answers

        val questionsAnswered = savedInstanceState?.getInt(QUESTIONS_ANSWERED, 0) ?: 0
        quizViewModel.countQuestionsAnswered = questionsAnswered

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        previousButton = findViewById(R.id.back_button)
        cheatButton = findViewById(R.id.cheat_button)
        questionTextView = findViewById(R.id.question_text_view)
        countHintTextView = findViewById(R.id.count_hint_text_view)

        trueButton.setOnClickListener {
            checkAnswer(true)
        }

        falseButton.setOnClickListener {
            checkAnswer(false)
        }

        questionTextView.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
            trueFalseBlock()
        }

        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
            trueFalseBlock()
        }

        previousButton.setOnClickListener {
            quizViewModel.moveToPrevious()
            updateQuestion()
            trueFalseBlock()
        }

        cheatButton.setOnClickListener { view ->
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val options = ActivityOptionsCompat
                    .makeClipRevealAnimation(view, 0, 0, view.width, view.height)
                cheatResult.launch(intent, options)
            } else {
                cheatResult.launch(intent)
            }
            updateQuestion()
            trueFalseBlock()
        }

        updateQuestion()
        trueFalseBlock()
        cheatBlock()
    }

    private val cheatResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            quizViewModel.isCheater = result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }

        if (quizViewModel.isCheater && quizViewModel.cheatCountLimit > 0) {
            quizViewModel.cheatCountLimit--
            cheatBlock()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        savedInstanceState.putInt(CHEAT_COUNT_LIMIT, quizViewModel.cheatCountLimit)
        savedInstanceState.putBooleanArray(ANSWERS, quizViewModel.answersOfUser)
        savedInstanceState.putInt(QUESTIONS_ANSWERED, quizViewModel.countQuestionsAnswered)
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        when {
            quizViewModel.isCheater -> {
                Toast.makeText(this, R.string.judgment_toast, Toast.LENGTH_SHORT)
                    .show()
                quizViewModel.isCheater = false
            }
            userAnswer == correctAnswer -> {
                Snackbar
                    .make(trueButton, R.string.correct_snackbar, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.argb(255, 150, 255, 150))
                    .setTextColor(Color.BLACK)
                    .show()
                quizViewModel.questionsRight++
            }
            else -> {
                Snackbar
                    .make(falseButton, R.string.incorrect_snackbar, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.argb(255, 255, 140, 140))
                    .setTextColor(Color.BLACK)
                    .show()
            }
        }

        quizViewModel.countQuestionsAnswered++
        quizViewModel.currentQuestionIsAnswered = true
        trueFalseBlock()
        quizScore()
    }

    private fun trueFalseBlock() {
       if (quizViewModel.currentQuestionIsAnswered) {
            trueButton.isEnabled = false
            falseButton.isEnabled = false

        } else {
            trueButton.isEnabled = true
            falseButton.isEnabled = true
        }
    }

    private fun cheatBlock() {
        if (quizViewModel.cheatCountLimit in 3 downTo 1 step 1) {
            cheatButton.isEnabled = true
        }
        else {
            cheatButton.isEnabled = false
        }

        countHintTextView.text = getString(R.string.count_hint_text)
        Log.d(TAG, "@${hashCode()}, cheatBlock(), countCheatLimit = ${quizViewModel.cheatCountLimit}")
        countHintTextView.append(" " + quizViewModel.cheatCountLimit.toString())
    }

    private fun quizScore() {
        if (quizViewModel.countQuestionsAnswered == quizViewModel.questionBankSize) {
            val totalScore =
                ((quizViewModel.questionsRight * 100) / quizViewModel.countQuestionsAnswered).toDouble()
            Toast.makeText(this, "${resources.getString(R.string.your_score_toast)} $totalScore %", Toast.LENGTH_LONG)
                .show()
        }
    }
}