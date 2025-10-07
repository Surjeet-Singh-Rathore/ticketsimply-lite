package com.bitla.ts.domain.pojo.recent_search

data class RecentSearch(
    val created_date: String,
    val dest_id: String,
    val dest_name: String,
    val origin_id: String,
    val origin_name: String,
    val updated_date: String
)