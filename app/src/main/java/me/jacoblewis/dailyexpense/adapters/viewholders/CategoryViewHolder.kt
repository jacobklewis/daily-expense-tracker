package me.jacoblewis.dailyexpense.adapters.viewholders

import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.commons.CategoryBalancer
import me.jacoblewis.dailyexpense.commons.asCurrency
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.jklcore.components.recyclerview.RBRecyclerViewHolder

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