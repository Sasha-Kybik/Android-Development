package com.example.geoquiz

import androidx.lifecycle.ViewModel

private  const val TAG = "QuizViewModel"

class QuizViewModel : ViewModel() {

    private val questionBank = listOf(
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true),
        Question(R.string.question_australia, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_oceans, true)
    )

    var currentIndex = 0
    var countQuestionsAnswered = 0
    var questionsRight = 0
    var isCheater = false
    var cheatCountLimit = 3
    var answersOfUser = BooleanArray(questionBankSize)

    override fun onCleared() {
        super.onCleared()
    }

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    var currentQuestionIsAnswered: Boolean
        get() = answersOfUser[currentIndex]
        set(value) { answersOfUser[currentIndex] = value }

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
}