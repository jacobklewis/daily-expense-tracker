package me.jacoblewis.dailyexpense.fragments.enterCategory

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.EditText
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.data.BalancesDB
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import javax.inject.Inject

class EnterCategoryDialogFragment : DialogFragment() {

    @Inject
    lateinit var db: BalancesDB

    val categoryText: EditText by lazy { EditText(context) }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        MyApp.graph.inject(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val ctx = context ?: return super.onCreateDialog(savedInstanceState)
        val dialogBuilder = AlertDialog.Builder(ctx)
        with(dialogBuilder) {
            setView(categoryText)
            setTitle(R.string.title_create_category)
            setPositiveButton(R.string.label_save, onSave)
            setNegativeButton(R.string.label_cancel, null)
        }
        return dialogBuilder.create()
    }

    val onSave = DialogInterface.OnClickListener { dialog, which ->
        val newCategoryTitle = categoryText.text.toString()
        val categoryToSave = Category(newCategoryTitle, "#333333")
        GlobalScope.launch {
            db.categoriesDao().insertCategory(categoryToSave)
        }
    }
}