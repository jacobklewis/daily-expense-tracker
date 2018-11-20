package me.jacoblewis.dailyexpense.adapters

import android.content.Context
import android.support.design.widget.Snackbar
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.jklcore.components.recyclerview.RBRecyclerAdapter
import me.jacoblewis.jklcore.components.recyclerview.RBRecyclerViewHolder

object CategoryController {

    fun createAdapter(context: Context?, callback: ItemDelegate<Category>): RBRecyclerAdapter<Category, ItemDelegate<Category>> {
        return CategoryItemAdapter(context, callback)
    }


    // List Adapter
    class CategoryItemAdapter(context: Context?, delegate: ItemDelegate<Category>) : RBRecyclerAdapter<Category, ItemDelegate<Category>>(context, delegate) {
        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RBRecyclerViewHolder<*, *> = CategoryViewHolder(viewGroup)
        override fun getItemViewType(position: Int): Int = 0
    }

    // Payment View Holder (UI)
    class CategoryViewHolder(viewGroup: ViewGroup) : RBRecyclerViewHolder<Category, ItemDelegate<Category>>(viewGroup, R.layout.viewholder_category) {
        @BindView(R.id.txt_category)
        lateinit var categoryTextView: TextView

        init {
            ButterKnife.bind(this, itemView)
        }

        override fun setUpView(itemView: View, item: Category, position: Int, delegate: ItemDelegate<Category>) {
            categoryTextView.text = item.name
        }

        override fun onClick(itemView: View, item: Category, position: Int, delegate: ItemDelegate<Category>) {
            delegate.onItemClicked(item)
        }

        override fun onLongClick(itemView: View, item: Category, position: Int, delegate: ItemDelegate<Category>) {
            Snackbar.make(itemView, "${item.name} held", Snackbar.LENGTH_LONG).show()
        }
    }
}