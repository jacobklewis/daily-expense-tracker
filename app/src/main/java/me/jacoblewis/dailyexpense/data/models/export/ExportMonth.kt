package me.jacoblewis.dailyexpense.data.models.export

data class ExportMonth(
        val month: String,
        val year: String,
        val exportCategories: List<ExportCategory>
)