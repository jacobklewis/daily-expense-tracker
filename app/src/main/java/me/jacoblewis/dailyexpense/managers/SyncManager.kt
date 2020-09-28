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
import kotlinx.coroutines.*
import me.jacoblewis.dailyexpense.data.BalancesDB
import me.jacoblewis.dailyexpense.data.mappers.BudgetMapper
import me.jacoblewis.dailyexpense.data.mappers.CategoryMapper
import me.jacoblewis.dailyexpense.data.mappers.PaymentMapper
import me.jacoblewis.dailyexpense.data.models.firebase.FBBudget
import me.jacoblewis.dailyexpense.data.models.firebase.FBCategory
import me.jacoblewis.dailyexpense.data.models.firebase.FBPayment

class SyncManager(val context: Context, val db: BalancesDB) {
    val TAG = SyncManager::class.java.name

    open class SyncException(val issue: String) : Exception("Restore Exception")
    class RestoreException(issue: String) : SyncException(issue)

    // working fields
    private lateinit var currentUser: FirebaseUser

    private fun setupCurrentUser(): Boolean {
        // Check to make sure signed in
        val user = FirebaseAuth.getInstance().currentUser
        return if (user != null) {
            currentUser = user
            true
        } else {
            Log.w(TAG, "Unable to sync because of authentication issue")
            false
        }
    }

    @Throws(SyncException::class)
    @UiThread
    fun syncNow(complete: (SyncException?)->Unit = {}) {
        if (setupCurrentUser()) {
            // Then Firebase User Get User
            getUser(action = SyncAction.Sync, complete = complete)
        }
    }

    @Throws(SyncException::class)
    @UiThread
    fun restoreNow(complete: (SyncException?)->Unit = {}) {
        if (setupCurrentUser()) {
            // Then Firebase User Get User
            getUser(action = SyncAction.Restore, complete = complete)
        }
    }

    sealed class SyncAction {
        object Sync : SyncAction()
        object Restore : SyncAction()
    }

    @Throws(SyncException::class)
    @UiThread
    private fun getUser(action: SyncAction, complete: (SyncException?)->Unit) {
        val fbdb = FirebaseFirestore.getInstance()
        fbdb.collection("users").whereEqualTo("email", currentUser.email).get().addOnSuccessListener { remoteUsers ->
            if (remoteUsers.isEmpty) {
                Toast.makeText(context, "You do not exist in our db... Unable to add you at the moment", Toast.LENGTH_LONG).show()
            } else {
                GlobalScope.launch(Dispatchers.Main) {
                    // Finally Sync
                    val remoteUserRef = remoteUsers.first().reference
                    try {
                        when (action) {
                            is SyncAction.Sync -> syncNewAndUpdated(remoteUserRef)
                            is SyncAction.Restore -> restoreAllDocuments(remoteUserRef)
                        }
                        // Complete successfully
                        complete(null)
                    } catch (ex: SyncException) {
                        // Complete with errors
                        complete(ex)
                    }

                }
            }
        }.addOnFailureListener {
            Log.e(TAG, "check failed", it)
            throw SyncException("User Not Found")
        }
    }

    @WorkerThread
    private suspend fun syncNewAndUpdated(userRef: DocumentReference) {
        val fbdb = FirebaseFirestore.getInstance()

        val budgetMapper = BudgetMapper(userRef)
        val categoryMapper = CategoryMapper(userRef)
        val paymentMapper = PaymentMapper(userRef)
        // Get Data to Sync
        val budgets = withContext(Dispatchers.IO) { db.budgetsDao().getAllToSync() }
        val categories = withContext(Dispatchers.IO) { db.categoriesDao().getAllToSync() }
        val payments = withContext(Dispatchers.IO) { db.paymentsDao().getAllToSync() }
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


    @Throws(RestoreException::class)
    @UiThread
    private suspend fun restoreAllDocuments(userRef: DocumentReference) {
        deleteAll()

        val fbdb = FirebaseFirestore.getInstance()

        val budgetMapper = BudgetMapper(userRef)
        val categoryMapper = CategoryMapper(userRef)
        val paymentMapper = PaymentMapper(userRef)

        val budgetsFinished: CompletableDeferred<Boolean> = CompletableDeferred()
        val categoriesFinished: CompletableDeferred<Boolean> = CompletableDeferred()
        val paymentsFinished: CompletableDeferred<Boolean> = CompletableDeferred()

        fbdb.collection("budgets").whereEqualTo("user", userRef).get().addOnSuccessListener { budgets ->
            GlobalScope.launch(Dispatchers.IO) {
                val fbBudgets = budgets.map { FBBudget(it.data) }
                val roomBudgets = fbBudgets.mapNotNull { budgetMapper.toRoom(it) }
                db.budgetsDao().insertBudgets(roomBudgets)
                budgetsFinished.complete(true)
            }
        }.addOnFailureListener {
            throw RestoreException("Could not restore budgets: ${it.message}")
        }
        fbdb.collection("categories").whereEqualTo("user", userRef).get().addOnSuccessListener { categories ->
            GlobalScope.launch(Dispatchers.IO) {
                val fbCategories = categories.map {
                    FBCategory(it.data, it.id)
                }
                val roomCategories = fbCategories.mapNotNull { categoryMapper.toRoom(it) }
                db.categoriesDao().insertCategories(roomCategories)
                categoriesFinished.complete(true)
            }
        }.addOnFailureListener {
            throw RestoreException("Could not restore categories: ${it.message}")
        }

        // Wait for all to finish
        budgetsFinished.await()
        categoriesFinished.await()
        // Then Complete Payments
        fbdb.collection("payments").whereEqualTo("user", userRef).get().addOnSuccessListener { payments ->
            GlobalScope.launch(Dispatchers.IO) {
                val fbPayments = payments.map { FBPayment(it.data) }
                val roomPayments = fbPayments.mapNotNull { paymentMapper.toRoom(it) }
                db.paymentsDao().insertPayments(roomPayments)
                paymentsFinished.complete(true)
            }
        }.addOnFailureListener {
            throw RestoreException("Could not restore payments: ${it.message}")
        }
        paymentsFinished.await()
    }

    private suspend fun deleteAll() = withContext(Dispatchers.IO) {
        db.budgetsDao().deleteAll()
        db.categoriesDao().deleteAll()
        db.paymentsDao().deleteAll()
    }
}