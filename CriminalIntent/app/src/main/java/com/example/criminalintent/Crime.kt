package com.example.criminalintent

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity
data class Crime(@PrimaryKey
                 val id: UUID = UUID.randomUUID(),
                 var title: String = "",
                 var date: Date = Date(),
                 var time: String = "",
                 var isSolved: Boolean = false,
                 var requiresPolice: Boolean = false,
                 var suspect: String = "",
                 var phoneNumber: String = "") {
    val photoFileName
        get() = "IMG_$id.jpg"
}