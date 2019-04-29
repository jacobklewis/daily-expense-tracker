package me.jacoblewis.dailyexpense.adapters

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import me.jacoblewis.jklcore.components.recyclerview.IdItem

class GeneralDiffUtil : DiffUtil.ItemCallback<IdItem<*>>() {
    override fun areItemsTheSame(oldItem: IdItem<*>, newItem: IdItem<*>): Boolean {
        return oldItem.identifier == newItem.identifier
    }

    override fun areContentsTheSame(oldItem: IdItem<*>, newItem: IdItem<*>): Boolean {
        if (oldItem.hash == newItem.hash) {
            return true
        }
        Log.e("DIFF UTILs", "Old: $oldItem, New: $newItem")
        return false
    }

}