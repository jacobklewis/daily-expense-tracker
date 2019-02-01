package me.jacoblewis.dailyexpense.data.models.firebase

import com.google.firebase.firestore.DocumentReference

data class FBCategory(
        val user: DocumentReference?,
        val name: String?,
        val color: String?,
        val notes: String?
)