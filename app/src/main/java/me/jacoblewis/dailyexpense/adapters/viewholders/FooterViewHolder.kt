package me.jacoblewis.dailyexpense.adapters.viewholders

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.data.models.Footer
import me.jacoblewis.jklcore.components.recyclerview.RBRecyclerViewHolder

// Footer View Holder (UI)
class FooterViewHolder(viewGroup: ViewGroup) : RBRecyclerViewHolder<Footer, ItemDelegate<Footer>?>(viewGroup, R.layout.viewholder_footer) {
    @BindView(R.id.txt_title)
    lateinit var footerTitleText: TextView

    init {
        ButterKnife.bind(this, itemView)
    }

    override fun setUpView(itemView: View, item: Footer, position: Int, delegate: ItemDelegate<Footer>?) {
        footerTitleText.text = item.text
    }
}