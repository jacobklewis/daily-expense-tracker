package me.jacoblewis.dailyexpense.adapters.viewholders

import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.viewholder_category_edit.view.*
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.extensions.asColorInt
import me.jacoblewis.jklcore.components.recyclerview.RBRecyclerViewHolder

class CategoryEditViewHolder(viewGroup: ViewGroup) : RBRecyclerViewHolder<Category, ItemDelegate<Category>>(viewGroup, LAYOUT_TYPE) {

    override fun setUpView(itemView: View, item: Category, position: Int, delegate: ItemDelegate<Category>) {
        itemView.txt_category.text = item.name.toUpperCase()
        itemView.txt_category_payments.text = itemView.context.getString(R.string.label_x_linked_payments, item.payments.size)
        itemView.view_color.setBackgroundColor(item.color.asColorInt)
    }

    override fun onClick(itemView: View, item: Category, position: Int, delegate: ItemDelegate<Category>?) {
        delegate?.onItemClicked(item)
    }

    companion object {
        const val LAYOUT_TYPE: Int = R.layout.viewholder_category_edit
    }
}