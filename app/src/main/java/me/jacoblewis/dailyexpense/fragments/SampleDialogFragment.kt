package me.jacoblewis.dailyexpense.fragments

import android.app.Dialog
import android.os.Bundle
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import me.jacoblewis.constantk.NamedConstant
import me.jacoblewis.dailyexpense.fragments.SampleDialogFragmentNamedConst.ICON
import me.jacoblewis.dailyexpense.fragments.SampleDialogFragmentNamedConst.MESSAGE
import me.jacoblewis.dailyexpense.fragments.SampleDialogFragmentNamedConst.ARG_TITLE
import me.jacoblewis.dailyexpense.fragments.enterCategory.EnterCategoryDialogFragment

class SampleDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val ctx = context ?: return super.onCreateDialog(savedInstanceState)
        val rootView = LinearLayout(context)

        val titleStr = arguments?.getString(ARG_TITLE)
        val messageStr = arguments?.getString(MESSAGE)
        val icon = arguments?.getInt(ICON) ?: 0
        // Populate
        val dialogBuilder = AlertDialog.Builder(ctx)
        with(dialogBuilder) {
            setView(rootView)
            setTitle(titleStr)
            setMessage(messageStr)
            setIcon(icon)
            setPositiveButton(android.R.string.ok, null)
        }

        return dialogBuilder.create()
    }

    companion object {

        fun open(fm: FragmentManager,
                 @NamedConstant("argTitle") title: String,
                 @NamedConstant message: String,
                 @NamedConstant @DrawableRes icon: Int) {
            val enterCategoryFrag = EnterCategoryDialogFragment()
            enterCategoryFrag.arguments = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(MESSAGE, message)
                putInt(ICON, icon)
            }
            enterCategoryFrag.show(fm, EnterCategoryDialogFragment::class.java.name)
        }
    }
}
