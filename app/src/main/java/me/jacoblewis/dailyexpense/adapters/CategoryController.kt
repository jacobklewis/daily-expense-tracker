package me.jacoblewis.dailyexpense.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.TextView
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
        lateinit var recyclerView: RecyclerView

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

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
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
        @BindView(R.id.seekBar)
        lateinit var seekBar: SeekBar
        @BindView(R.id.checkbox_lock)
        lateinit var lockedCheckBox: CheckBox

        init {
            ButterKnife.bind(this, itemView)
        }

        fun updateSlider() {
            balanceTextView.text = (item.budget * BUDGET).asCurrency
            seekBar.progress = (CategoryBalancer.mapToExpo(item.budget) * 1000).toInt()
            seekBar.isEnabled = !item.locked
        }

        override fun setUpView(itemView: View, item: Category, position: Int, delegate: ItemDelegate<Category>) {
            categoryTextView.text = item.name
            seekBar.setOnSeekBarChangeListener(null)
            lockedCheckBox.isChecked = item.locked
            updateSlider()
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    moveSliderDelegate(position, CategoryBalancer.mapFromExpo(progress / 1000f))
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    saveSliderPosDelegate()
                }

            })
        }

        @OnCheckedChanged(R.id.checkbox_lock)
        fun onChecked(v: CompoundButton) {
            item.locked = v.isChecked
            updateSlider()
        }

        @OnClick(R.id.button_remove)
        fun onRemove(v: View) {
            delegate.onItemRemoved(item)
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