package com.bitla.ts.domain.pojo.starred_reports

data class Data(
    val starred_reports: List<StarredReport>,
    val recently_generated_reports: MutableList<StarredReport>?
)