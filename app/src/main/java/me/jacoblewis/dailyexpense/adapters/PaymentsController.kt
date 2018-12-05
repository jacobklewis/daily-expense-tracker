package me.jacoblewis.dailyexpense.adapters

import android.content.Context
import android.support.design.widget.Snackbar
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.commons.asCurrency
import me.jacoblewis.dailyexpense.commons.formatAs
import me.jacoblewis.dailyexpense.data.models.PaymentCategory
import me.jacoblewis.jklcore.components.recyclerview.RBRecyclerAdapter
import me.jacoblewis.jklcore.components.recyclerview.RBRecyclerViewHolder
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object PaymentsController {

    fun createAdapter(context: Context?, callback: ItemDelegate<PaymentCategory>): RBRecyclerAdapter<PaymentCategory, ItemDelegate<PaymentCategory>> {
        return PaymentItemAdapter(context, callback)
    }


    // List Adapter
    class PaymentItemAdapter(context: Context?, delegate: ItemDelegate<PaymentCategory>) : RBRecyclerAdapter<PaymentCategory, ItemDelegate<PaymentCategory>>(context, delegate) {
        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RBRecyclerViewHolder<*, *> = PaymentViewHolder(viewGroup)
        override fun getItemViewType(position: Int): Int = 0
    }

    // Payment View Holder (UI)
    class PaymentViewHolder(viewGroup: ViewGroup) : RBRecyclerViewHolder<PaymentCategory, ItemDelegate<PaymentCategory>>(viewGroup, R.layout.viewholder_payment) {
        @BindView(R.id.txt_category)
        lateinit var categoryTextView: TextView
        @BindView(R.id.txt_cost)
        lateinit var costTextView: TextView
        @BindView(R.id.txt_date)
        lateinit var dateTextView: TextView

        init {
            ButterKnife.bind(this, itemView)
        }

        override fun setUpView(itemView: View, item: PaymentCategory, position: Int, delegate: ItemDelegate<PaymentCategory>) {
            categoryTextView.text = if (item.category.isNotEmpty()) item.category[0].name else ""
            costTextView.text = item.transaction?.cost?.asCurrency
            dateTextView.text = Date(item.transaction?.creationDate?.timeInMillis ?: 0) formatAs "MMM, d - h:mm a"
        }

        override fun onClick(itemView: View, item: PaymentCategory, position: Int, delegate: ItemDelegate<PaymentCategory>) {
            Snackbar.make(itemView, "${item.transaction?.cost} selected", Snackbar.LENGTH_LONG).show()
        }

        override fun onLongClick(itemView: View, item: PaymentCategory, position: Int, delegate: ItemDelegate<PaymentCategory>): Boolean {
            Snackbar.make(itemView, "${item.transaction?.cost} held", Snackbar.LENGTH_LONG).show()
            return true
        }
    }
}