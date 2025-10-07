package com.bitla.ts.domain.pojo.bp_dp_details

import com.bitla.ts.domain.pojo.available_routes.BoardingPointDetail
import com.bitla.ts.domain.pojo.available_routes.DropOffDetail

data class BpDpDetails(
    val code : Int,
    val boarding_point_details: List<BoardingPointDetail>,
    val drop_off_details: List<DropOffDetail>
)