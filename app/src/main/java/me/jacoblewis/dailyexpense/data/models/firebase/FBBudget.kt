package me.jacoblewis.dailyexpense.data.models.firebase

import com.google.firebase.firestore.DocumentReference
import me.jacoblewis.dailyexpense.commons.asNum

data class FBBudget(
        val user: DocumentReference?,
        val amount: Float?,
        val year: Int?,
        val month: Int?
) {
    constructor(map: Map<String, Any>) : this(
            map["user"] as? DocumentReference,
            map["amount"].asNum(),
            map["year"].asNum(),
            map["month"].asNum()
    )
}