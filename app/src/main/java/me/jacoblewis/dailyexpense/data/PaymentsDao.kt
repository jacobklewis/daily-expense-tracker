package me.jacoblewis.dailyexpense.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
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