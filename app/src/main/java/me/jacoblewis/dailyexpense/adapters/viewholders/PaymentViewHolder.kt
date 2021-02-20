package me.jacoblewis.dailyexpense.adapters.viewholders

import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.viewholder_payment.view.*
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.commons.asCurrency
import me.jacoblewis.dailyexpense.commons.formatAs
import me.jacoblewis.dailyexpense.data.models.PaymentCategory
import me.jacoblewis.jklcore.components.recyclerview.RBRecyclerViewHolder
import java.util.*

// Payment View Holder (UI)
class PaymentViewHolder(viewGroup: ViewGroup) : RBRecyclerViewHolder<PaymentCategory, ItemDelegate<PaymentCategory>?>(viewGroup, LAYOUT_TYPE) {

    override fun setUpView(itemView: View, item: PaymentCategory, position: Int, delegate: ItemDelegate<PaymentCategory>?) {
        itemView.txt_category.text = if (item.category.isNotEmpty()) item.category[0].name.toUpperCase() else ""
        itemView.txt_cost.text = item.transaction?.cost?.asCurrency
        itemView.txt_date.text = Date(item.transaction?.creationDate?.timeInMillis
                ?: 0) formatAs "MMM, d - h:mm a"
    }

    override fun onClick(itemView: View, item: PaymentCategory, position: Int, delegate: ItemDelegate<PaymentCategory>?) {
        delegate?.onItemClicked(item)
    }

    override fun onLongClick(itemView: View, item: PaymentCategory, position: Int, delegate: ItemDelegate<PaymentCategory>?): Boolean {
        Snackbar.make(itemView, "${item.transaction?.cost} held", Snackbar.LENGTH_LONG).show()
        return true
    }

    companion object {
        const val LAYOUT_TYPE: Int = R.layout.viewholder_payment
    }
}