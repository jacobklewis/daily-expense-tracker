package me.jacoblewis.dailyexpense.data.mappers

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import me.jacoblewis.dailyexpense.commons.ifAll
import me.jacoblewis.dailyexpense.data.models.Payment
import me.jacoblewis.dailyexpense.data.models.firebase.FBPayment
import java.util.*

class PaymentMapper(val userRef: DocumentReference) : BaseMapper<Payment, FBPayment> {
    override fun toFirebase(roomModel: Payment): FBPayment {
        val categoryRef = FirebaseFirestore.getInstance().document("categories/${roomModel.categoryId}")
        return FBPayment(userRef, roomModel.cost, Timestamp(roomModel.creationDate.time), roomModel.notes, categoryRef)
    }

    override fun toRoom(firebaseModel: FBPayment): Payment? {
        return ifAll(firebaseModel.cost, firebaseModel.creationDate, firebaseModel.notes, firebaseModel.category) { cost, date, notes, cat ->
            Payment(cost, Calendar.getInstance().also { it.time = date.toDate() }, notes, categoryId = cat.id, needsSync = false)
        }
    }

}