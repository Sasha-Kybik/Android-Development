package com.example.geoquiz

import androidx.annotation.StringRes

data class Question(@StringRes // Использование строкового идентификатора ресурса
                    val textResId: Int,
                    val answer: Boolean,
                    var wasAnswered: Boolean = false) // Был ли дан ответ на данный вопрос