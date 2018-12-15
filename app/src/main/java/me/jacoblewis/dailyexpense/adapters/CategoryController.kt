package me.jacoblewis.dailyexpense.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnCheckedChanged
import butterknife.OnClick
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.commons.CategoryBalancer
import me.jacoblewis.dailyexpense.commons.asCurrency
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.jklcore.components.recyclerview.RBRecyclerAdapter
import me.jacoblewis.jklcore.components.recyclerview.RBRecyclerViewHolder
import kotlin.math.roundToInt

object CategoryController {
    val BUDGET = 500f

    fun createAdapter(context: Context?, callback: ItemDelegate<Category>, saveSliderPosDelegate: () -> Unit): RBRecyclerAdapter<Category, ItemDelegate<Category>> {
        return CategoryItemAdapter(context, callback, saveSliderPosDelegate)
    }

    fun createChooseAdapter(context: Context?, callback: ItemDelegate<Category>): RBRecyclerAdapter<Category, ItemDelegate<Category>> {
        return CategoryItemAdapter(context, callback, null)
    }


    // List Adapter
    class CategoryItemAdapter(context: Context?, delegate: ItemDelegate<Category>, val saveSliderPosDelegate: (() -> Unit)?) : RBRecyclerAdapter<Category, ItemDelegate<Category>>(context, delegate) {
        lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView

        val moveSliderDelegate: (Int, Float) -> Unit = { pos, ratio ->
            itemList[pos].budget = ratio
            val pinned = itemList.mapIndexedNotNull { index, category -> if (category.locked) index else null }.toMutableList().also { it.add(pos) }
            CategoryBalancer.balanceCategories(itemList, pinned)

            (0 until recyclerView.childCount).forEach {
                val v = recyclerView.getChildAt(it)
                val vh = recyclerView.getChildViewHolder(v) as CategoryViewHolder
                vh.updateSlider()
            }
        }

        override fun onAttachedToRecyclerView(recyclerView: androidx.recyclerview.widget.RecyclerView) {
            super.onAttachedToRecyclerView(recyclerView)
            this.recyclerView = recyclerView
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RBRecyclerViewHolder<*, *> = saveSliderPosDelegate?.let {
            CategoryViewHolder(viewGroup, moveSliderDelegate, it)
        } ?: CategoryChooseViewHolder(viewGroup)

        override fun getItemViewType(position: Int): Int = 0
    }

    // Category View Holder (UI)
    class CategoryViewHolder(viewGroup: ViewGroup, val moveSliderDelegate: (Int, Float) -> Unit, val saveSliderPosDelegate: () -> Unit) : RBRecyclerViewHolder<Category, ItemDelegate<Category>>(viewGroup, R.layout.viewholder_category) {
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

        fun updateSlider() {
            val normalizedBudget: Float = CategoryBalancer.normalizePrice(item.budget, BUDGET)
            balanceTextView.text = (normalizedBudget * BUDGET).asCurrency
            val colorInt = if (item.locked) R.color.colorAccent else R.color.trans_99_black
            balanceTextView.setTextColor(ContextCompat.getColor(itemView.context, colorInt))
            increaseButton.isEnabled = !item.locked
            increaseButton.alpha = if (!item.locked) 1f else 0.2f
            decreaseButton.isEnabled = !item.locked
            decreaseButton.alpha = if (!item.locked) 1f else 0.2f
        }

        override fun setUpView(itemView: View, item: Category, position: Int, delegate: ItemDelegate<Category>) {
            categoryTextView.text = item.name
            updateSlider()
        }

        @OnClick(R.id.button_increase, R.id.button_decrease)
        fun onButtonsClicked(v: View) {
            when(v.id) {
                R.id.button_increase -> moveSliderDelegate(pos, CategoryBalancer.offsetPrice(item.budget, BUDGET, offset = 1f))
                R.id.button_decrease -> moveSliderDelegate(pos, CategoryBalancer.offsetPrice(item.budget, BUDGET, offset = -1f))
            }
        }

        override fun onClick(itemView: View, item: Category, position: Int, delegate: ItemDelegate<Category>) {
            delegate.onItemClicked(item)
        }
    }

    // Category Choose View Holder (UI)
    class CategoryChooseViewHolder(viewGroup: ViewGroup) : RBRecyclerViewHolder<Category, ItemDelegate<Category>>(viewGroup, R.layout.viewholder_category_choose) {
        @BindView(R.id.txt_category)
        lateinit var categoryTextView: TextView
        @BindView(R.id.txt_balance)
        lateinit var balanceTextView: TextView

        init {
            ButterKnife.bind(this, itemView)
        }

        override fun setUpView(itemView: View, item: Category, position: Int, delegate: ItemDelegate<Category>) {
            categoryTextView.text = item.name
            balanceTextView.text = (item.budget * BUDGET).asCurrency
        }

        override fun onClick(itemView: View, item: Category, position: Int, delegate: ItemDelegate<Category>) {
            delegate.onItemClicked(item)
        }
    }
}