package me.jacoblewis.dailyexpense.data.models.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

data class FBPayment(
        val user: DocumentReference?,
        val cost: Float?,
        val creationDate: Timestamp?,
        val notes: String?,
        val category: DocumentReference?
)