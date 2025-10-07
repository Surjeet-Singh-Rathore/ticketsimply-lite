package com.bitla.ts.domain.pojo

import com.bitla.ts.domain.pojo.update_route.StageDetailsData

data class BoardingDroppingStage( val name: String,
                                  val id: Int,
                                  val stage_details: List<StageDetailsData>)


