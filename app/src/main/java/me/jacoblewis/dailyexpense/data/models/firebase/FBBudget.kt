package me.jacoblewis.dailyexpense.data.models.firebase

import com.google.firebase.firestore.DocumentReference

data class FBBudget(
        val user: DocumentReference?,
        val amount: Float?,
        val year: Int?,
        val month: Int?
)