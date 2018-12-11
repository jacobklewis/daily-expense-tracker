package me.jacoblewis.dailyexpense.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import me.jacoblewis.dailyexpense.data.models.Payment
import me.jacoblewis.dailyexpense.data.models.PaymentCategory

@Dao
interface PaymentsDao {

    @Transaction
    @Query("SELECT * FROM payments ORDER BY creation_date DESC")
    fun getAllPayments(): LiveData<List<PaymentCategory>>

    @Insert
    fun insertPayment(payment: Payment)

    @Query("DELETE FROM payments WHERE category_id = :categoryId")
    fun deleteByCategory(categoryId: Long)
}