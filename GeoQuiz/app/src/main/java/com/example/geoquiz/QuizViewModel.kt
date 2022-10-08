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

    var currentIndex = 0 // Текущий № вопроса
    var questionsAnswered = 0 // Количество отвеченных вопросов
    var questionsRight = 0 // Количество правильных ответов
    var isCheater = false // Подсматривал пользователь правильный ответ?
    var countCheatLimit = 3 // Количество доступных подсказок

    // Уничтожение класса ViewModel
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "Экземпляр ViewModel скоро будет уничтожен")
    }

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer // Возвращает ответ на данный вопрос

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId // Возвращает вопрос

    val currentQuestionIsAnswered: Boolean
        get() = questionBank[currentIndex].wasAnswered // Возвращает ответ, что на данный вопрос был дан ответ пользователем

    val questionBankSize: Int
        get() = questionBank.size // Возвращает размер коллекции

    // Переход к следующему вопросу
    fun moveToNext() {
        if (currentIndex < questionBank.size - 1) {
            currentIndex++
        }
    }

    // Переход к предыдущему вопросу
    fun moveToPrevious() {
        if (currentIndex != 0) {
            currentIndex--
        }
    }

    // Вопрос помечается, что на него был получен ответ от пользователя
    fun answered() {
        questionBank[currentIndex].wasAnswered = true
    }
}