package com.bitla.ts.domain.pojo.destination_list

import com.bitla.ts.domain.pojo.announcement_model.response.BoardingPoint
import com.bitla.ts.domain.pojo.available_routes.BoardingPointDetail

data class DestinationList(
    val cityList : ArrayList<DestinationListItem>,
    val boardingDetails : BoardingPointDetail?)