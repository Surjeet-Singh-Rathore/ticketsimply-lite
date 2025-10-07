package com.bitla.ts.domain.pojo.recent_search

data class RecentSearchModel(
    val code: Int = 0,
    val message: String = "",
    val recent_search: MutableList<RecentSearch>
)