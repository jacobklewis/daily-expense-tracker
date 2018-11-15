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
    @Query("SELECT * FROM payments")
    fun getAllPayments(): LiveData<List<PaymentCategory>>

    @Insert
    fun insertPayment(payment: Payment)
}