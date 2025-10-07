package com.bitla.ts.domain.pojo.self_audit_data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class OptionQuestion {
    @SerializedName("question")
    @Expose
    var question: String? = null

    @SerializedName("question_id")
    @Expose
    var questionId: String? = null

    @SerializedName("options")
    @Expose
    var options: List<Option>? = null

    @SerializedName("type")
    @Expose
    var type: String? = null
}
