package me.jacoblewis.dailyexpense.extensions

import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import me.jacoblewis.dailyexpense.commons.SwipeCallback

/**
 * Swipe Listener
 */
fun RecyclerView.addSwipeListener(direction: Int, @DrawableRes icon: Int, staticTop: Boolean, onSwipedListener: (pos: Int) -> Unit) {
    val swipeHandler = object : SwipeCallback(context!!, ContextCompat.getDrawable(context!!, icon)!!, direction, staticTop) {
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            onSwipedListener(viewHolder.adapterPosition)
        }
    }
    ItemTouchHelper(swipeHandler).attachToRecyclerView(this)
}