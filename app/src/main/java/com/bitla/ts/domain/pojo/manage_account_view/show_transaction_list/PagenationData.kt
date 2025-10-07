package com.bitla.ts.domain.pojo.manage_account_view.show_transaction_list

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PagenationData {

    @SerializedName("position")
    @Expose
    var position : Int = 0

    @SerializedName("is_selected")
    @Expose
    var isSelected: Boolean = false


}