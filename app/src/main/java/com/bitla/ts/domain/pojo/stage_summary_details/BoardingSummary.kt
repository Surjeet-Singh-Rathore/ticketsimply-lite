package com.bitla.ts.domain.pojo.stage_summary_details

import com.google.gson.annotations.SerializedName


data class BoardingSummary (

  @SerializedName("total_seats"        ) var totalSeats      : Int?    = null,
  @SerializedName("total_boarded"      ) var boardedCount    : Int?    = null,
  @SerializedName("yet_to_board_seats" ) var totalYetToBoard : ArrayList<String> = ArrayList(),
  @SerializedName("empty_seats"        ) var emptySeats      : ArrayList<String> = ArrayList(),
  @SerializedName("total_empty_seat"   ) var totalEmptySeat  : Int?    = null

)