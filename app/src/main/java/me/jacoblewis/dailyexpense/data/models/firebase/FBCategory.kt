package me.jacoblewis.dailyexpense.data.models.firebase

import com.google.firebase.firestore.DocumentReference

data class FBCategory(
        val user: DocumentReference?,
        val name: String?,
        val color: String?,
        val notes: String?,
        val categoryId: String
) {
    constructor(map: Map<String, Any>, categoryId: String) : this(
            map["user"] as? DocumentReference,
            map["name"] as? String,
            map["color"] as? String,
            map["notes"] as? String,
            categoryId
    )
}