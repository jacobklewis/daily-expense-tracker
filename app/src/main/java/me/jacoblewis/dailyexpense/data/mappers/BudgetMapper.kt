package me.jacoblewis.dailyexpense.data.mappers

import com.google.firebase.firestore.DocumentReference
import me.jacoblewis.dailyexpense.commons.ifAll
import me.jacoblewis.dailyexpense.data.models.Budget
import me.jacoblewis.dailyexpense.data.models.firebase.FBBudget

class BudgetMapper(val userRef: DocumentReference) : BaseMapper<Budget, FBBudget> {
    override fun toFirebase(roomModel: Budget): FBBudget {
        return FBBudget(userRef, roomModel.amount, roomModel.year, roomModel.month)
    }

    override fun toRoom(firebaseModel: FBBudget): Budget? {
        return ifAll(firebaseModel.amount, firebaseModel.year, firebaseModel.month) { amount, year, month ->
            Budget(amount, year, month, needsSync = false)
        }
    }

}