package com.bitla.ts.domain.pojo.collection_details


data class CollectionDetailsResponse(
    val code: Int,
    val collection_summary: List<CollectionSummary>,
    val result: Results?
)

class Results(
    val message: String?
)