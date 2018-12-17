package me.jacoblewis.dailyexpense.adapters

interface ItemDelegate<T> {
    fun onItemClicked(item: T)
}