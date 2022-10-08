package com.example.geoquiz

import android.util.Log
import androidx.lifecycle.ViewModel

private  const val TAG = "QuizViewModel"

class QuizViewModel : ViewModel() {
    init {
        Log.d(TAG, "Экземпляр ViewModel был создан")
    }

    val questionBank = listOf(
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true),
        Question(R.string.question_australia, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_oceans, true)
    )

    var currentIndex = 0
    var questionsAnswered = 0
    var questionsRight = 0
    var isCheater = false
    var countCheatLimit = 3

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "Экземпляр ViewModel скоро будет уничтожен")
    }

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    val currentQuestionIsAnswered: Boolean
        get() = questionBank[currentIndex].wasAnswered

    val questionBankSize: Int
        get() = questionBank.size

    fun moveToNext() {
        if (currentIndex < questionBank.size - 1) {
            currentIndex++
        }
    }

    fun moveToPrevious() {
        if (currentIndex != 0) {
            currentIndex--
        }
    }

    fun answered() {
        questionBank[currentIndex].wasAnswered = true
    }
}