package com.bitla.ts.domain.pojo.all_reports.new_response.group_by_branch_report_data.group_by_branch_report_response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GroupByBranchReportBranchData(

    @SerializedName("branch_name")
    @Expose
    val branchName: String = "",

    @SerializedName("fare")
    @Expose
    val fare: String = "",

    @SerializedName("detail")
    @Expose
    val detail: ArrayList<GroupByBranchReportDetailData>

)
