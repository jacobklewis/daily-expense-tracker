package me.jacoblewis.dailyexpense.adapters

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.commons.CategoryBalancer
import me.jacoblewis.dailyexpense.commons.asCurrency
import me.jacoblewis.dailyexpense.commons.fromCurrency
import me.jacoblewis.dailyexpense.data.BalancesDB
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.jklcore.components.recyclerview.RBRecyclerAdapter
import me.jacoblewis.jklcore.components.recyclerview.RBRecyclerViewHolder
import javax.inject.Inject

object CategoryController {

    // List Adapter
    class CategoryItemAdapter
    @Inject constructor(context: Context?, val db: BalancesDB, val sp: SharedPreferences) : RBRecyclerAdapter<Category, ItemDelegate<Category>>(context, null) {
        lateinit var recyclerView: RecyclerView
        var editable: Boolean = false

        val saveItemsDelegate: (pos: Int)->Unit = { pos ->
            GlobalScope.launch {
                val pinned = itemList.mapIndexedNotNull { index, category -> if (category.locked) index else null }.toMutableList().also { it.add(pos) }
                CategoryBalancer.balanceCategories(itemList, pinned)
                db.categoriesDao().updateCategories(itemList)
            }
        }

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            super.onAttachedToRecyclerView(recyclerView)
            this.recyclerView = recyclerView
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RBRecyclerViewHolder<*, *> {
            val budget = sp.getString("budget", "0").fromCurrency
            return if (i == 0) {
                CategoryViewHolder(viewGroup, saveItemsDelegate, budget)
            } else {
                CategoryChooseViewHolder(viewGroup, budget)
            }
        }

        override fun getItemViewType(position: Int): Int = if (editable) 0 else 1
    }

    // Category View Holder (UI)
    class CategoryViewHolder(viewGroup: ViewGroup, val saveItemsDelegate: (pos: Int) -> Unit, val budget: Float) : RBRecyclerViewHolder<Category, ItemDelegate<Category>>(viewGroup, R.layout.viewholder_category) {
        @BindView(R.id.txt_category)
        lateinit var categoryTextView: TextView
        @BindView(R.id.txt_balance)
        lateinit var balanceTextView: TextView
        @BindView(R.id.button_increase)
        lateinit var increaseButton: ImageButton
        @BindView(R.id.button_decrease)
        lateinit var decreaseButton: ImageButton

        init {
            ButterKnife.bind(this, itemView)
        }

        fun updateUI() {
            val normalizedBudget: Float = CategoryBalancer.normalizePrice(item.budget, budget)
            balanceTextView.text = (normalizedBudget * budget).asCurrency
            val colorInt = if (item.locked) R.color.colorAccent else R.color.trans_99_black
            balanceTextView.setTextColor(ContextCompat.getColor(itemView.context, colorInt))
            increaseButton.isEnabled = !item.locked
            increaseButton.alpha = if (!item.locked) 1f else 0.2f
            decreaseButton.isEnabled = !item.locked
            decreaseButton.alpha = if (!item.locked) 1f else 0.2f
        }

        override fun setUpView(itemView: View, item: Category, position: Int, delegate: ItemDelegate<Category>) {
            categoryTextView.text = item.name
            updateUI()
        }

        @OnClick(R.id.button_increase, R.id.button_decrease)
        fun onButtonsClicked(v: View) {
            when (v.id) {
                R.id.button_increase -> item.budget = CategoryBalancer.offsetPrice(item.budget, budget, offset = 1f)
                R.id.button_decrease -> item.budget = CategoryBalancer.offsetPrice(item.budget, budget, offset = -1f)
            }
            saveItemsDelegate(pos)
        }

        override fun onClick(itemView: View, item: Category, position: Int, delegate: ItemDelegate<Category>) {
            delegate.onItemClicked(item)
        }
    }

    // Category Choose View Holder (UI)
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
}