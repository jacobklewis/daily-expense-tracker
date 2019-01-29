package me.jacoblewis.dailyexpense.fragments.enterCategory

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcelable
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.data.BalancesDB
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import javax.inject.Inject

class EnterCategoryDialogFragment : androidx.fragment.app.DialogFragment() {

    @Inject
    lateinit var db: BalancesDB

    val categoryText: EditText by lazy { EditText(context) }

    var config: Config? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        MyApp.graph.inject(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val ctx = context ?: return super.onCreateDialog(savedInstanceState)
        config = arguments?.getParcelable(Config::class.java.name)
        // Populate title and editText
        val titleRes = config?.category?.let { cat ->
            categoryText.setText(cat.name)
            R.string.title_edit_category
        } ?: R.string.title_create_category
        val dialogBuilder = AlertDialog.Builder(ctx)
        with(dialogBuilder) {
            setView(categoryText)
            setTitle(titleRes)
            setPositiveButton(R.string.label_save, onSave)
            setNegativeButton(R.string.label_cancel, null)
        }
        return dialogBuilder.create()
    }

    val onSave = DialogInterface.OnClickListener { dialog, which ->
        val categoryTitle = categoryText.text.toString()
        GlobalScope.launch {
            config?.category?.let {
                val categoryToSave = it
                categoryToSave.name = categoryTitle
                db.categoriesDao().updateCategory(categoryToSave)
            } ?: run {
                val categoryToSave = Category(categoryTitle, "#333333", 0f)
                db.categoriesDao().insertCategory(categoryToSave)
            }
        }
    }

    @Parcelize
    data class Config(val category: Category? = null) : Parcelable
}