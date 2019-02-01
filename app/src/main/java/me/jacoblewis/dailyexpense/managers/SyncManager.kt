package me.jacoblewis.dailyexpense.managers

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.jacoblewis.dailyexpense.data.BalancesDB
import me.jacoblewis.dailyexpense.data.mappers.BudgetMapper
import me.jacoblewis.dailyexpense.data.mappers.CategoryMapper
import me.jacoblewis.dailyexpense.data.mappers.PaymentMapper
import javax.inject.Inject

class SyncManager
@Inject constructor(val context: Context, val db: BalancesDB) {
    val TAG = SyncManager::class.java.name

    // working fields
    private lateinit var currentUser: FirebaseUser

    @UiThread
    fun syncNow() {
        // Check to make sure signed in
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            currentUser = user
        } else {
            Log.w(TAG, "Unable to sync because of authentication issue")
            return
        }

        // Then Firebase User Get User
        getUser()
    }

    @UiThread
    private fun getUser() {
        val fbdb = FirebaseFirestore.getInstance()
        fbdb.collection("users").whereEqualTo("email", currentUser.email).get().addOnSuccessListener { remoteUsers ->
            if (remoteUsers.isEmpty) {
                Toast.makeText(context, "You do not exist in our db... Unable to add you at the moment", Toast.LENGTH_LONG).show()
            } else {
                GlobalScope.launch {
                    // Finally Sync
                    syncNewAndUpdated(remoteUsers.first().reference)
                }
            }
        }.addOnFailureListener {
            Log.e(TAG, "check failed", it)
        }
    }

    @WorkerThread
    private suspend fun syncNewAndUpdated(userRef: DocumentReference) {
        val fbdb = FirebaseFirestore.getInstance()

        val budgetMapper = BudgetMapper(userRef)
        val categoryMapper = CategoryMapper(userRef)
        val paymentMapper = PaymentMapper(userRef)
        // Get Data to Sync
        val budgets = db.budgetsDao().getAllToSync()
        val categories = db.categoriesDao().getAllToSync()
        val payments = db.paymentsDao().getAllToSync()
        if (budgets.isEmpty() && categories.isEmpty() && payments.isEmpty()) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "No Sync Needed...", Toast.LENGTH_LONG).show()
            }
            return
        }


        val batch = fbdb.batch()
        // BUDGETS
        budgets.map { Pair(it.budgetId, budgetMapper.toFirebase(it)) }.forEach {
            val ref = fbdb.collection("budgets").document(it.first)
            batch.set(ref, it.second)
        }

        // CATEGORIES
        categories.map { Pair(it.categoryId, categoryMapper.toFirebase(it)) }.forEach {
            val ref = fbdb.collection("categories").document(it.first)
            batch.set(ref, it.second)
        }

        // PAYMENTS
        payments.map { Pair(it.id, paymentMapper.toFirebase(it)) }.forEach {
            val ref = fbdb.collection("payments").document(it.first)
            batch.set(ref, it.second)
        }
        batch.commit().addOnSuccessListener {
            Toast.makeText(context, "Sync Complete", Toast.LENGTH_LONG).show()
            GlobalScope.launch {
                db.budgetsDao().setAllSync()
                db.categoriesDao().setAllSync()
                db.paymentsDao().setAllSync()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Sync FAILED!!!!!!!!!!!!!", Toast.LENGTH_LONG).show()
            it.printStackTrace()
        }
    }
}