package me.jacoblewis.dailyexpense.fragments.enterCategory

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_enter_category.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.data.BalancesDB
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.extensions.asColorInt
import org.koin.android.ext.android.inject

class EnterCategoryDialogFragment : androidx.fragment.app.DialogFragment() {

    val db: BalancesDB by inject()

    var config: Config? = null
    var currentColor: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val ctx = context ?: return super.onCreateDialog(savedInstanceState)

        val inflater = LayoutInflater.from(activity)
        val rootView = inflater.inflate(R.layout.fragment_enter_category, null, false)

        config = arguments?.getParcelable(CATEGORY_CONFIG)
        // Populate title and editText
        val titleRes = config?.category?.let { cat ->
            val color = cat.color.asColorInt
            rootView.textCategory.setText(cat.name)
            currentColor = color
            rootView.colorPicker.setColor(color)
            rootView.previewImage.setColorFilter(color)
            R.string.title_edit_category
        } ?: R.string.title_create_category
        val dialogBuilder = AlertDialog.Builder(ctx)
        with(dialogBuilder) {
            setView(rootView)
            setTitle(titleRes)
            setPositiveButton(R.string.label_save, onSave)
            setNegativeButton(R.string.label_cancel, null)
        }
        rootView.colorPicker.setColorSelectionListener(object : SimpleColorSelectionListener() {
            override fun onColorSelected(color: Int) {
                currentColor = color
                rootView.previewImage.setColorFilter(color)
            }
        })

        return dialogBuilder.create()
    }

    val onSave = DialogInterface.OnClickListener { dialog, which ->
        val categoryTitle = view?.textCategory?.text.toString()
        GlobalScope.launch {
            config?.category?.let {
                val categoryToSave = it
                categoryToSave.name = categoryTitle
                categoryToSave.color = String.format("#%06X", 0xFFFFFF and currentColor)
                categoryToSave.needsSync = true
                db.categoriesDao().updateCategory(categoryToSave)
            } ?: run {
                val categoryToSave = Category(categoryTitle, String.format("#%06X", 0xFFFFFF and currentColor))
                db.categoriesDao().insertCategory(categoryToSave)
            }
        }
    }

    @Parcelize
    data class Config(val category: Category? = null) : Parcelable

    companion object {
        const val CATEGORY_CONFIG = "categoryConfig"

        fun open(fm: FragmentManager, categoryConfig: Config? = null) {
            val enterCategoryFrag = EnterCategoryDialogFragment()
            enterCategoryFrag.arguments = Bundle().apply {
                putParcelable(CATEGORY_CONFIG, categoryConfig)
            }
            enterCategoryFrag.show(fm, EnterCategoryDialogFragment::class.java.name)
        }
    }
}