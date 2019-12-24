package me.jacoblewis.dailyexpense.data.models.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import me.jacoblewis.dailyexpense.commons.asNum

data class FBPayment(
        val user: DocumentReference?,
        val cost: Float?,
        val creationDate: Timestamp?,
        val notes: String?,
        val category: DocumentReference?
) {
    constructor(map: Map<String, Any>) : this(
            map["user"] as? DocumentReference,
            map["cost"].asNum(),
            map["creationDate"] as? Timestamp,
            map["notes"] as? String,
            map["category"] as? DocumentReference
    )
}