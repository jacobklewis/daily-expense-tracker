package me.jacoblewis.dailyexpense.data.mappers

import com.google.firebase.firestore.DocumentReference
import me.jacoblewis.dailyexpense.commons.ifAll
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.data.models.firebase.FBCategory

class CategoryMapper(val userRef: DocumentReference) : BaseMapper<Category, FBCategory> {
    override fun toFirebase(roomModel: Category): FBCategory {
        return FBCategory(userRef, roomModel.name, roomModel.color, roomModel.notes, roomModel.categoryId)
    }

    override fun toRoom(firebaseModel: FBCategory): Category? {
        return ifAll(firebaseModel.name, firebaseModel.color, firebaseModel.notes ?: "") { name, color, notes ->
            Category(name, color, notes, categoryId = firebaseModel.categoryId, needsSync = false)
        }
    }

}