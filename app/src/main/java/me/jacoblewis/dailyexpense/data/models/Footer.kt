package me.jacoblewis.dailyexpense.data.models

import me.jacoblewis.jklcore.components.recyclerview.IdItem

data class Footer(val text: String) : IdItem<String> {
    override fun getIdentifier(): String = "footer"
}