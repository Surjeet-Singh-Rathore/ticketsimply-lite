import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bitla.ts.R
import com.bitla.ts.domain.pojo.login_model.CounterDetails
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.user.User
import com.bitla.ts.utils.ResourceProvider
import com.bitla.ts.utils.SafeClickListener
import com.example.buscoach.service_details_response.SeatDetail
import kotlinx.coroutines.delay
import me.drakeet.support.toast.ToastCompat
import timber.log.Timber
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.temporal.WeekFields
import java.util.LinkedHashMap
import java.util.Locale

/**
 * toast message
 */
fun Context.toast(message: String?) {
//    toast issue is in android 7.1
//    ToastCompat Android library to hook and fix Toast BadTokenException
    if (message != null){
        if (Build.VERSION.SDK_INT == 25) {
            ToastCompat.makeText(this, message, Toast.LENGTH_SHORT)
                .setBadTokenListener { toast -> Timber.e("failed toast", message) }.show()
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}

/**
 * show progress bar
 */
fun ProgressBar.show() {
    visibility = View.VISIBLE
}

/**
 * hide progress bar
 */
fun ProgressBar.hide() {
    visibility = View.GONE
}

/**
 * visible view
 */
fun View.visible() {
    visibility = View.VISIBLE
}

/**
 * invisible view
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * gone view
 */
fun View.gone() {
    visibility = View.GONE
}

/**
 * show internet connection toast
 */
fun Context.noNetworkToast() {
    toast(getString(R.string.no_network_msg))
}

/**
 * show something went wrong toast
 */
fun Context.somethingWentWrongToast() {
    toast(getString(R.string.something_went_wrong))
}

/**
 * Gets network state.
 *
 * @return the network state
 */
fun Context.isNetworkAvailable(): Boolean {
    val connMgr =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connMgr.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isAvailable && activeNetwork.isConnected
}

fun AppCompatActivity.blockInput() {
    window.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    )
}

fun AppCompatActivity.unBlockInput() {
    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}


fun EditText.onChange(cb: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            cb(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}

fun EditText.setMaxLength(maxLength: Int) {
    filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
}

fun URL.toBitmap(): Bitmap? {
    return try {
        BitmapFactory.decodeStream(openStream())
    } catch (e: IOException) {
        null
    }
}


fun dpToPx(dp: Int, context: Context): Int =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),
        context.resources.displayMetrics
    ).toInt()

internal fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return context.layoutInflater.inflate(layoutRes, this, attachToRoot)
}

internal val Context.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(this)

internal val Context.inputMethodManager
    get() = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

internal inline fun Boolean?.orFalse(): Boolean = this ?: false

internal fun Context.getDrawableCompat(@DrawableRes drawable: Int) =
    ContextCompat.getDrawable(this, drawable)

internal fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)

internal fun TextView.setTextColorRes(@ColorRes color: Int) =
    setTextColor(context.getColorCompat(color))

fun daysOfWeekFromLocale(): Array<DayOfWeek> {
    val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    var daysOfWeek = DayOfWeek.values()
    // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
    // Only necessary if firstDayOfWeek != DayOfWeek.MONDAY which has ordinal 0.
    if (firstDayOfWeek != DayOfWeek.MONDAY) {
        val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
        val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
        daysOfWeek = rhs + lhs
    }
    return daysOfWeek
}

fun GradientDrawable.setCornerRadius(
    topLeft: Float = 0F,
    topRight: Float = 0F,
    bottomRight: Float = 0F,
    bottomLeft: Float = 0F
) {
    cornerRadii = arrayOf(
        topLeft, topLeft,
        topRight, topRight,
        bottomRight, bottomRight,
        bottomLeft, bottomLeft
    ).toFloatArray()
}

fun ResourceProvider.TextResource.asString(resources : Resources) : String = when (this) {
    is ResourceProvider.SimpleTextResource -> this.text
    is ResourceProvider.IdTextResource -> resources.getString(this.id)
}

fun EditText.addDecimalLimiter(maxLimit: Int = 2) {

    this.addTextChangedListener(object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            val str = this@addDecimalLimiter.text!!.toString()
            if (str.isEmpty()) return
            val str2 = decimalLimiter(str, maxLimit)

            if (str2 != str) {
                this@addDecimalLimiter.setText(str2)
                val pos = this@addDecimalLimiter.text!!.length
                this@addDecimalLimiter.setSelection(pos)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

    })
}

fun EditText.decimalLimiter(string: String, MAX_DECIMAL: Int): String {

    var str = string
    if (str[0] == '.') str = "0$str"
    val max = str.length

    var rFinal = ""
    var after = false
    var i = 0
    var up = 0
    var decimal = 0
    var t: Char

    val decimalCount = str.count{ ".".contains(it) }

    if (decimalCount > 1)
        return str.dropLast(1)

    while (i < max) {
        t = str[i]
        if (t != '.' && !after) {
            up++
        } else if (t == '.') {
            after = true
        } else {
            decimal++
            if (decimal > MAX_DECIMAL)
                return rFinal
        }
        rFinal += t
        i++
    }
    return rFinal
}

fun firstLetterWord(str: String): String {
    var result = ""
    var v = true
    for (i in 0 until str.length) {
        if (str[i] == ' ') {
            v = true
        } else if (str[i] != ' ' && v) {
            result += str[i]
            v = false
        }
    }
    return result
}

fun User.toLoginModel(): LoginModel = LoginModel(
    userName = this.username ?: "",
    name = this.name ?: "",
    api_key = this.apiKey ?: "",
    user_id = this.userId,
    travels_name = this.travelsName,
    language = this.language,
    phone_number = this.phoneNumber ?: "",
    email = this.email ?: "",
    logo_url = this.logoUrl,
    trackingo_api_key = this.trackingoApiKey,
    trackingo_url = this.trackingoUrl,
    role = this.role ?: "",
    account_balance = this.accountBalance,
    city_id = this.cityId,
    city_name = this.cityName,
    header = this.header,
    password = this.password,
    domainName = this.domainName,
    isEncryptionEnabled = this.isEncryptionEnabled,
    counter_details = CounterDetails(
        this.shiftName,
        this.counterName
    )
)

fun LoginModel.toUserModel(): User = User (
    username = this.userName,
    name = this.name,
    apiKey = this.api_key,
    userId = this.user_id ?: 0,
    travelsName = this.travels_name ?: "",
    language = this.language ?: "",
    phoneNumber = this.phone_number,
    email = this.email,
    logoUrl = this.logo_url ?: "",
    trackingoApiKey = this.trackingo_api_key ?: "",
    trackingoUrl = this.trackingo_url ?: "",
    role = this.role,
    accountBalance = this.account_balance ?: "",
    cityId = this.city_id ?: "",
    cityName = this.city_name ?: "",
    header = this.header ?: "",
    password = this.password,
    domainName = this.domainName,
    isEncryptionEnabled = this.isEncryptionEnabled,
    shiftName = this.counter_details?.shift_name,
    counterName = this.counter_details?.counter_name

)



fun String.convertTimeFormat(fromFormat: String, toFormat: String): String? {
    return try {
        val inputFormat = SimpleDateFormat(fromFormat, Locale.getDefault())
        val outputFormat = SimpleDateFormat(toFormat, Locale.getDefault())
        val date = inputFormat.parse(this)
        date?.let { outputFormat.format(it) }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


fun String.toPercentageValue(): Double? {
    return try {
        this.replace("%", "").toDouble()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun LinkedHashMap<SeatDetail, SeatDetail?>.removePNRGroupAndDeselectSeats(
    pnr: String,
    deSelectLeftSeat:((leftSideSeatDetail: SeatDetail) -> Unit),
    deSelectRightSeat:((rightSideSeatDetail: SeatDetail) -> Unit)
): LinkedHashMap<SeatDetail, SeatDetail?> {
    val tempList = mutableListOf<SeatDetail>()

    for((key, value ) in this) {
        if(key.passengerDetails?.ticketNo.equals(pnr, true)) {
            tempList.add(key)
        }
    }

    tempList.forEach {item->

        if(this.get(item) != null) {
            deSelectRightSeat.invoke(this.get(item)!!)
        }

        this.remove(item)
        deSelectLeftSeat.invoke(item)
    }

    return this
}

fun LinkedHashMap<SeatDetail, SeatDetail?>.removePNRGroupAndDeselectSeatsNew(
    pnr: String,
    deSelectLeftSeat:((leftSideSeatDetail: SeatDetail) -> Unit),
    deSelectRightSeat:((rightSideSeatDetail: SeatDetail) -> Unit)
): LinkedHashMap<SeatDetail, SeatDetail?> {
    val tempList = mutableListOf<SeatDetail>()

    for((key, value ) in this) {
        if(key.isMultiHop == true) {
            key.otherPnrNumber?.forEach {

                if (pnr.equals(it, true)) {
                    tempList.add(key)
                }
            }
        } else {
            if (key.passengerDetails?.ticketNo.equals(pnr, true)) {
                tempList.add(key)
            }
        }
    }

    tempList.forEach {item->

        if(this.get(item) != null) {
            deSelectRightSeat.invoke(this.get(item)!!)
        }

        this.remove(item)
        deSelectLeftSeat.invoke(item)
    }

    return this
}


fun LinkedHashMap<SeatDetail, SeatDetail?>.containsLeftSideSeatIfLeftSideSeatIsPassedInParam(
    leftSideSeatDetail: SeatDetail
    ): Boolean {

    var flag = false

    for((key, value ) in this) {
        if(key.passengerDetails?.ticketNo.equals(leftSideSeatDetail.passengerDetails?.ticketNo, true) && key.number.equals(leftSideSeatDetail.number, true)) {
            flag = true
            break
        }
    }

    return flag
}

fun LinkedHashMap<SeatDetail, SeatDetail?>.areAllSeatsShiftedOfGivenPNR(
    pnr: String
    ): Boolean {

    var flag = false

    for((key, value ) in this) {

        if (pnr.equals(key.passengerDetails?.ticketNo, true)) {
            if (value != null) {
                flag = true
            } else {
                flag = false
                break
            }
        }
    }

    return flag
}

fun LinkedHashMap<SeatDetail, SeatDetail?>.areAllSeatsNotShiftedOfGivenPNR(
    pnr: String
    ): Boolean {

    var flag = false

    for((key, value ) in this) {

        if (pnr.equals(key.passengerDetails?.ticketNo, true)) {
            if (value == null) {
                flag = true
            } else {
                flag = false
                break
            }
        }
    }

    return flag
}

fun LinkedHashMap<SeatDetail, SeatDetail?>.removeNonPairedSeats(
    deSelectLeftSeat:((leftSideSeatDetail: SeatDetail) -> Unit)
): LinkedHashMap<SeatDetail, SeatDetail?> {
    val tempList = mutableListOf<SeatDetail>()

    for((key, value ) in this) {
        if(value == null) {
            tempList.add(key)
        }
    }

    tempList.forEach {item->
        this.remove(item)
        deSelectLeftSeat.invoke(item)
    }

    return this
}


fun EditText.setMaxValueWithDecimal(maxValue: Double, decimalPlaces: Int) {
    val decimalPattern = "^\\d{0,${maxValue.toInt().toString().length}}(\\.\\d{0,$decimalPlaces})?$".toRegex()

    this.filters = arrayOf(InputFilter { source, start, end, dest, dStart, dEnd ->
        val newValue = dest.toString().substring(0, dStart) +
                source.subSequence(start, end) +
                dest.toString().substring(dEnd)

        // Allow pattern with decimal restriction
        if (!newValue.matches(decimalPattern)) return@InputFilter ""

        // Allow only numbers <= maxValue
        val parsed = newValue.toDoubleOrNull() ?: return@InputFilter ""
        if (parsed > maxValue) return@InputFilter ""

        null  // Accept input
    })
}

object ClickHandler {

    private var isClickable = true

    suspend fun runWithDelay(delayMillis: Long = 1000L, action: () -> Unit) {
        if (isClickable) {
            isClickable = false
            action()  // Perform the actual click action
            delay(delayMillis)
            isClickable = true
        }
    }
}