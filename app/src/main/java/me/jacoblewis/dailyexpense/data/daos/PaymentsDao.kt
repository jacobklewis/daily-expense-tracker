package me.jacoblewis.dailyexpense.data.daos

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.room.*
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.data.models.Payment
import me.jacoblewis.dailyexpense.data.models.PaymentCategory
import java.util.*

@Dao
interface PaymentsDao {

    @Transaction
    @Query("SELECT * FROM payments ORDER BY creation_date DESC")
    fun getAllPayments(): LiveData<List<PaymentCategory>>

    @Transaction
    @Query("SELECT * FROM payments WHERE creation_date >= :date ORDER BY creation_date DESC")
    fun getAllPaymentsSince(date: Calendar): LiveData<List<PaymentCategory>>

    @Transaction
    @Query("SELECT * FROM payments WHERE creation_date >= :from AND creation_date <= :to ORDER BY creation_date DESC")
    fun getAllPaymentsBetween(from: Calendar, to: Calendar): LiveData<List<PaymentCategory>>

    @WorkerThread
    @Query("SELECT * FROM payments WHERE needsSync = 1")
    fun getAllToSync(): List<Payment>

    @WorkerThread
    @Query("DELETE FROM payments WHERE category_id = :categoryId")
    fun deleteByCategory(categoryId: String)

    @WorkerThread
    @Insert
    fun insertPayment(payment: Payment)

    @WorkerThread
    @Delete
    fun deletePayment(payment: Payment)

    @WorkerThread
    @Query("UPDATE payments SET needsSync = 0")
    fun setAllSync()
}