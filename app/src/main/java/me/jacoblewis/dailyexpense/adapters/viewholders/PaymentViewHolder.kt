package me.jacoblewis.dailyexpense.adapters.viewholders

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.snackbar.Snackbar
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.commons.asCurrency
import me.jacoblewis.dailyexpense.commons.formatAs
import me.jacoblewis.dailyexpense.data.models.PaymentCategory
import me.jacoblewis.jklcore.components.recyclerview.RBRecyclerViewHolder
import java.util.*

// Payment View Holder (UI)
class PaymentViewHolder(viewGroup: ViewGroup) : RBRecyclerViewHolder<PaymentCategory, ItemDelegate<PaymentCategory>?>(viewGroup, R.layout.viewholder_payment) {
    @BindView(R.id.txt_category)
    lateinit var categoryTextView: TextView
    @BindView(R.id.txt_cost)
    lateinit var costTextView: TextView
    @BindView(R.id.txt_date)
    lateinit var dateTextView: TextView

    init {
        ButterKnife.bind(this, itemView)
    }

    override fun setUpView(itemView: View, item: PaymentCategory, position: Int, delegate: ItemDelegate<PaymentCategory>?) {
        categoryTextView.text = if (item.category.isNotEmpty()) item.category[0].name.toUpperCase() else ""
        costTextView.text = item.transaction?.cost?.asCurrency
        dateTextView.text = Date(item.transaction?.creationDate?.timeInMillis
                ?: 0) formatAs "MMM, d - h:mm a"
    }

    override fun onClick(itemView: View, item: PaymentCategory, position: Int, delegate: ItemDelegate<PaymentCategory>?) {
        Snackbar.make(itemView, "${item.transaction?.cost} selected", Snackbar.LENGTH_LONG).show()
    }

    override fun onLongClick(itemView: View, item: PaymentCategory, position: Int, delegate: ItemDelegate<PaymentCategory>?): Boolean {
        Snackbar.make(itemView, "${item.transaction?.cost} held", Snackbar.LENGTH_LONG).show()
        return true
    }
}