package me.jacoblewis.dailyexpense.fragments.enterCategory

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import butterknife.BindView
import butterknife.ButterKnife
import com.madrapps.pikolo.HSLColorPicker
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.data.BalancesDB
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import me.jacoblewis.dailyexpense.extensions.asColorInt
import javax.inject.Inject

class EnterCategoryDialogFragment : androidx.fragment.app.DialogFragment() {

    @Inject
    lateinit var db: BalancesDB

    @BindView(R.id.colorPicker)
    lateinit var categoryColor: HSLColorPicker
    @BindView(R.id.editText)
    lateinit var categoryText: EditText
    @BindView(R.id.previewImage)
    lateinit var previewImage: ImageView

    var config: Config? = null
    var currentColor: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        MyApp.graph.inject(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val ctx = context ?: return super.onCreateDialog(savedInstanceState)

        val inflater = LayoutInflater.from(activity)
        val rootView = inflater.inflate(R.layout.fragment_enter_category, null, false)
        ButterKnife.bind(this, rootView)

        config = arguments?.getParcelable(Config::class.java.name)
        // Populate title and editText
        val titleRes = config?.category?.let { cat ->
            val color = cat.color.asColorInt
            categoryText.setText(cat.name)
            categoryColor.setColor(color)
            previewImage.setColorFilter(color)
            R.string.title_edit_category
        } ?: R.string.title_create_category
        val dialogBuilder = AlertDialog.Builder(ctx)
        with(dialogBuilder) {
            setView(rootView)
            setTitle(titleRes)
            setPositiveButton(R.string.label_save, onSave)
            setNegativeButton(R.string.label_cancel, null)
        }
        categoryColor.setColorSelectionListener(object : SimpleColorSelectionListener() {
            override fun onColorSelected(color: Int) {
                currentColor = color
                previewImage.setColorFilter(color)
            }
        })

        return dialogBuilder.create()
    }

    val onSave = DialogInterface.OnClickListener { dialog, which ->
        val categoryTitle = categoryText.text.toString()
        GlobalScope.launch {
            config?.category?.let {
                val categoryToSave = it
                categoryToSave.name = categoryTitle
                categoryToSave.color = String.format("#%06X", 0xFFFFFF and currentColor)
                db.categoriesDao().updateCategory(categoryToSave)
            } ?: run {
                val categoryToSave = Category(categoryTitle, String.format("#%06X", 0xFFFFFF and currentColor))
                db.categoriesDao().insertCategory(categoryToSave)
            }
        }
    }

    @Parcelize
    data class Config(val category: Category? = null) : Parcelable
}