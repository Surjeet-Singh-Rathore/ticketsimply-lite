package com.bitla.ts.domain.pojo.stage_summary_details

import com.bitla.ts.domain.pojo.stage_summary_details.BoardingStageDetails
import com.bitla.ts.domain.pojo.stage_summary_details.BoardingSummary
import com.bitla.ts.domain.pojo.stage_summary_details.DroppingStageDetails
import com.google.gson.annotations.SerializedName


data class Result (

  @SerializedName("boarding_summary"       ) var boardingSummary      : BoardingSummary?  = BoardingSummary(),
  @SerializedName("boarding_stage_summary" ) var boardingStageSummary : ArrayList<BoardingStageDetails> = arrayListOf(),
  @SerializedName("dropping_stage_summary" ) var droppingStageSummary : ArrayList<DroppingStageDetails> = arrayListOf()

)