package br.com.vieirateam.tcc.model

data class SearchHistory(
        var token: String = "",
        var search: MutableList<String> = mutableListOf(),
        var product_name: String = "",
        var product_category: String = "",
        var product_visualized: Boolean = false) {

    fun addSearch(query: String) {
        this.search.add(query)
    }
}