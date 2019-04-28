package me.jacoblewis.dailyexpense.data.models

import androidx.room.Embedded
import androidx.room.Relation
import me.jacoblewis.dailyexpense.commons.asCurrency
import me.jacoblewis.jklcore.components.recyclerview.IdItem

class PaymentCategory : IdItem<String> {

    @Embedded
    var transaction: Payment? = null

    @Relation(parentColumn = "category_id", entityColumn = "id")
    var category: List<Category> = listOf()

    override fun getIdentifier(): String = transaction?.identifier ?: ""
    override fun getHash(): Int = hashCode()

    override fun equals(other: Any?): Boolean {
        return other is PaymentCategory && transaction == other.transaction && category.firstOrNull() == other.category.firstOrNull()
    }

    override fun toString(): String {
        return "${transaction?.cost?.asCurrency ?: ""} cat: ${transaction?.categoryId}"
    }

    override fun hashCode(): Int {
        var result = transaction?.hashCode() ?: 0
        result = 31 * result + category.hashCode()
        return result
    }
}