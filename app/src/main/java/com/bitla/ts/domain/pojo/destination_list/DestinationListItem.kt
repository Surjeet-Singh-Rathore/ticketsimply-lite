package com.bitla.ts.domain.pojo.destination_list

import com.bitla.ts.domain.pojo.available_routes.DropOffDetail

data class DestinationListItem(
    val dropping_point: List<DropOffDetail>,
    val city: City
)