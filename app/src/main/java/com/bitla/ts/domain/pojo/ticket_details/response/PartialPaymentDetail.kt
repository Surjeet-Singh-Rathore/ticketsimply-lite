import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Surjeet Rathore on 12/07/22.
 */
class PartialPaymentDetail {
    @SerializedName("total_amount")
    @Expose
    var totalAmount: Any? = null

    @SerializedName("paid_amount")
    @Expose
    var paidAmount: Any? = null

    @SerializedName("remaining_amount")
    @Expose
    var remainingAmount: Any? = null
}