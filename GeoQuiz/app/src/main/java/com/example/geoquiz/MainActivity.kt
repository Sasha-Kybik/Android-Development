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

class MainActivity : AppCompatActivity() {
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var cheatButton: Button
    private lateinit var questionTextView: TextView
    private lateinit var countHintTextView: TextView

    // Свойство для хранения экземпляров QuizViewModel
    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProvider(this).get(QuizViewModel::class.java)
    }

    // Современное решение взамен метода startActivityForResult(intent, REQUEST_CODE_CHEAT), внедрённое с Андроид11
    // Получение информации из дочерней активности
    private val cheatResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // Была использована подсказка? ДА/НЕТ
        if (result.resultCode == RESULT_OK) {
            quizViewModel.isCheater =
                result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }

        // Счётчик подсказок
        if (quizViewModel.isCheater && quizViewModel.countCheatLimit > 0) {
            quizViewModel.countCheatLimit--
            cheatBlock()
        }
    }

    // Создание экземпляра активности
    @SuppressLint("Ограничение по API")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) вызван")
        setContentView(R.layout.activity_main)

        // Загрузка № вопроса, хранящегося в ключе KEY_INDEX и устанавливка его
        // в качестве текущего значения. Если индекс не сохранён, то устанавливается значение = 0
        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

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

        // Переход к следующему вопросу при нажатии на текст вопроса
        questionTextView.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }

        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }

        previousButton.setOnClickListener {
            quizViewModel.moveToPrevious()
            updateQuestion()
        }

        cheatButton.setOnClickListener { view ->
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)

            // Вид анимации в зависимости от версии Андроида, для API23
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val options = ActivityOptionsCompat
                    .makeClipRevealAnimation(view, 0, 0, view.width, view.height)
                cheatResult.launch(intent, options)
            } else {
                cheatResult.launch(intent)
            }
            updateQuestion()
        }

        // Необходимо в случае поворота экрана и приостановки активности
        updateQuestion()
        trueFalseBlock()
        cheatBlock()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() вызван")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() вызван")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() вызван")
    }

    // Сохраняет данные когда приложение приостанавливается (onPause или onStop)
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() вызван")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() вызван")
    }

    // Получение текущего значения и отображение вопроса под данным индексом в коллекции
    private fun updateQuestion() {
        Log.d(TAG, "Обновление текста вопроса", Exception())
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
        trueFalseBlock()
    }

    // Сравнение ответов пользователя с ответами из коллекции
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

        quizViewModel.questionsAnswered++
        quizViewModel.answered()
        trueFalseBlock()
        quizScore()
    }

    // Проверяет, был ли дан ответ на вопрос, если да - блокирует нажатие кнопок "ДА" и "НЕТ" для повторного ответа
    private fun trueFalseBlock() {
        if (quizViewModel.currentQuestionIsAnswered) {
            trueButton.isEnabled = false
            falseButton.isEnabled = false
        } else {
            trueButton.isEnabled = true
            falseButton.isEnabled = true
        }
    }

    // Выводит доступное количество подсказок, в случае исчерпание лимита - блокирует кнопку подсказки
    private fun cheatBlock() {
        if (quizViewModel.countCheatLimit in 3 downTo 1 step 1) {
            cheatButton.isEnabled = true
        }
        else {
            cheatButton.isEnabled = false
        }

        countHintTextView.text = getString(R.string.count_hint_text)
        countHintTextView.append(" " + quizViewModel.countCheatLimit.toString())
    }

    // Проверяет количество введённых ответов с количеством вопросов, в случае равенства вычисляется % правильных ответов
    private fun quizScore() {
        if (quizViewModel.questionsAnswered == quizViewModel.questionBankSize) {
            val totalScore =
                ((quizViewModel.questionsRight * 100) / quizViewModel.questionsAnswered).toDouble()
            Toast.makeText(this, "${resources.getString(R.string.your_score_toast)} $totalScore %", Toast.LENGTH_LONG)
                .show()
        }
    }
}