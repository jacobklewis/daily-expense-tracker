package me.jacoblewis.dailyexpense.adapters.viewholders

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.commons.asCurrency
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.jklcore.components.recyclerview.RBRecyclerViewHolder

class CategoryChooseViewHolder(viewGroup: ViewGroup, val budget: Float) : RBRecyclerViewHolder<Category, ItemDelegate<Category>>(viewGroup, R.layout.viewholder_category_choose) {
    @BindView(R.id.txt_category)
    lateinit var categoryTextView: TextView
    @BindView(R.id.txt_balance)
    lateinit var balanceTextView: TextView

    init {
        ButterKnife.bind(this, itemView)
    }

    override fun setUpView(itemView: View, item: Category, position: Int, delegate: ItemDelegate<Category>) {
        categoryTextView.text = item.name
        balanceTextView.text = (item.budget * budget).asCurrency
    }

    override fun onClick(itemView: View, item: Category, position: Int, delegate: ItemDelegate<Category>) {
        delegate.onItemClicked(item)
    }
}