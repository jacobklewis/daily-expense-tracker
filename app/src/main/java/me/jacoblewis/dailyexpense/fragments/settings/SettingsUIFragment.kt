package me.jacoblewis.dailyexpense.fragments.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import me.jacoblewis.dailyexpense.BuildConfig
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.commons.PREFS_SETTINGS
import me.jacoblewis.dailyexpense.commons.asCurrency
import me.jacoblewis.dailyexpense.mainActivity.interfaces.NavigationController
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.managers.BalanceManager
import me.jacoblewis.dailyexpense.managers.ExportManager
import me.jacoblewis.dailyexpense.managers.SyncManager
import org.koin.android.ext.android.inject

class SettingsUIFragment : PreferenceFragmentCompat() {
    val TAG = SettingsUIFragment::class.java.name

    val syncManager: SyncManager by inject()

    val exportManager: ExportManager by inject()

    val balanceManager: BalanceManager by inject()

    private lateinit var navigationController: NavigationController
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private var syncPref: Preference? = null
    private var restorePref: Preference? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        navigationController = context as NavigationController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.SIGN_IN_ID_TOKEN)
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Set the shared preferences file name
        preferenceManager.sharedPreferencesName = PREFS_SETTINGS
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onBindPreferences() {
        // Change the summary
        findPreference<Preference>("budget")?.setSummaryProvider { balanceManager.currentBudget.asCurrency }
        findPreference<Preference>("budget")?.setOnPreferenceChangeListener { preference, newValue ->
            balanceManager.currentBudget = (newValue as? String ?: "0").toFloat()
            true
        }

        // Override Category preference and use our custom one instead
        findPreference<Preference>("edit_cats")?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            navigationController.navigateTo(NavScreen.EditCategories)
            return@OnPreferenceClickListener true
        }

        // Override Category preference and use our custom one instead
        findPreference<Preference>("export_csv")?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            Toast.makeText(context, "Exporting CSV Now...", Toast.LENGTH_SHORT).show()
            exportManager.exportNow()
            return@OnPreferenceClickListener true
        }

        // Override Category preference and use our custom one instead
        findPreference<Preference>("edit_profile")?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            Toast.makeText(context, "Attempting Sign in...", Toast.LENGTH_SHORT).show()
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, 1203)
            return@OnPreferenceClickListener true
        }

        syncPref = findPreference("sync_now")
        syncPref?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            //            Toast.makeText(context, "Attempting Sync", Toast.LENGTH_SHORT).show()
            syncManager.syncNow()
            return@OnPreferenceClickListener true
        }

        restorePref = findPreference("restore_now")
        restorePref?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            //            Toast.makeText(context, "Attempting Sync", Toast.LENGTH_SHORT).show()
            syncManager.restoreNow { error ->
                if (error == null) {
                    Toast.makeText(context, "Restore Completed Successfully!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Error Restoring: ${error.issue}", Toast.LENGTH_LONG).show()
                }
            }
            return@OnPreferenceClickListener true
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            enableLoggedInFeatures(true)
        } else {
            Toast.makeText(context, "No user signed in...", Toast.LENGTH_SHORT).show()
            enableLoggedInFeatures(false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 1203) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.i(TAG, "Attempting to auth with Firebase")
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }
    }

    fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                val currentUser = auth.currentUser
                Toast.makeText(context, "${currentUser?.email} is signed in!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Firebase Auth failed!", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun enableLoggedInFeatures(enabled: Boolean) {
        syncPref?.isEnabled = enabled
        restorePref?.isEnabled = enabled
    }

}