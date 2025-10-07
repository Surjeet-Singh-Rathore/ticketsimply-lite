package com.bitla.ts.koin.networkModule

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import com.bitla.ts.app.base.TsApplication
import com.bitla.ts.utils.common.convertLongToTime
import com.bitla.ts.utils.common.firebaseLogEvent
import com.bitla.ts.utils.common.getLogFileName
import com.bitla.ts.utils.common.getTodayDate
import com.bitla.ts.utils.constants.API_CRASH
import com.bitla.ts.utils.constants.DATE_FORMAT_D_M_Y
import com.bitla.ts.utils.sharedPref.PREF_DOMAIN
import com.bitla.ts.utils.sharedPref.PREF_EXCEPTION
import com.bitla.ts.utils.sharedPref.PREF_FCM_TOKEN
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import io.sentry.BuildConfig
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.internal.http2.ConnectionShutdownException
import okio.Buffer
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Taiyab Ali on 05-Aug-21.
 */
class ApiResponseInterceptor : Interceptor {
    private val apiLogFileName: String = "${getLogFileName()}_${getTodayDate()}.txt"

    //    private val apiLogFileName: String = "${getLogFileName()}_26-10-2022.txt"
    private val deviceInfoLogFileName: String = "${getLogFileName()}_device_info.txt"
    private var fileList = ArrayList<String>()
    private val fileNameList = ArrayList<String>()
    // private lateinit var fileOutputStream: FileOutputStream
    // private var deviceCount = 0

    override fun intercept(chain: Interceptor.Chain): Response {
        writeOnFile(getSystemDetail(), deviceInfoLogFileName, true)
        //Timber.d(getSystemDetail())

        val request: Request = chain.request()
        val data =
            "Request Time: ${convertLongToTime(System.currentTimeMillis())} \n Api Url - ${
                request.url.toString().replace(
                    "mba.ticketsimply.com", PreferenceUtils.getPreference(
                        PREF_DOMAIN, ""
                    )!!
                )
            } \n Method - ${request.method} \nRequest Body: ${
                bodyToString(
                    request
                )
            }\n"

        // Timber.e("logging $data")
        writeOnFile(data, apiLogFileName, false)

        return try {
            val response = chain.proceed(request)
            val bodyString = response.body

            // log only a safe preview of response
            val preview = getResponsePreview(bodyString, 2048) // limit 2KB
            writeOnFile("Response Preview: $preview\n", apiLogFileName, false)

            response.newBuilder()
                .body(bodyString)
                .build()

        } catch (e: Exception) {
            val data =
                "Response Time: ${convertLongToTime(System.currentTimeMillis())} \nException Message: ${e.message} \n\n"
            writeOnFile(data, apiLogFileName, false)
            //  Timber.d("serverException ${e.message}")

//            val loginModelPref = PreferenceUtils.getLogin()
//            val response = chain.proceed(request)
//            val bodyString = response.body?.string()


//            firebaseLogEvent(TsApplication.getAppContext(),API_CRASH, loginModelPref?.userName,
//                loginModelPref?.travels_name,
//                loginModelPref?.role, API_CRASH,"Api - ${request.url} \nResponse - $bodyString")

            var msg = ""
            val interceptorCode = 408
            when (e) {
                is SocketTimeoutException -> {
                    msg = "Timeout - Please check your internet connection"
                }
                is UnknownHostException -> {
                    msg = "Please enter a valid domain" //as discussed with sourabh, we are modifying this toast
                }
                is ConnectionShutdownException -> {
                    msg = "Connection shutdown. Please check your internet"
                }
                is IOException -> {
                    msg = "Server is unreachable, please try again later."
                }
                is IllegalStateException -> {
                    msg = "${e.message}"
                }
                else -> {

                    msg = "${e.message}"

                }
            }

            PreferenceUtils.putString(PREF_EXCEPTION,msg)


            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(interceptorCode)
                .message(msg)
                .body("{${msg}}".toResponseBody(null)).build()
        }
    }

    private fun getResponsePreview(responseBody: ResponseBody?, maxChars: Int): String {
        return try {
            val source = responseBody?.source()
            source?.request(Long.MAX_VALUE)
            val buffer = source?.buffer?.clone()
            buffer?.readUtf8()?.take(maxChars) ?: ""
        } catch (e: Exception) {
            "Unable to preview response"
        }
    }

    private fun writeOnFile(data: String, logFileName: String, isPrivateMode: Boolean) {
        if (data.length > 50 * 1024) { // 50KB max safeguard
            Timber.w("Skipping large log entry for $logFileName (size=${data.length})")
            return
        }

        if (!fileList.contains(logFileName)) {
            //  logFileName.removeSuffix(".txt")

            fileList.add(logFileName)
            PreferenceUtils.putLogFileNames(fileList)

        }
        try {
            val fileOutputStream =
                TsApplication.getAppContext().openFileOutput(
                    logFileName,
                    if (isPrivateMode) Context.MODE_PRIVATE else Context.MODE_APPEND
                )
            fileOutputStream.write(data.toByteArray())
            TsApplication.getAppContext().fileList()
            deleteFrmLocal()
        } catch (e: Exception) {
            Timber.d("serverException ${e.message}")
        }
    }

    private fun deleteFrmLocal() {

        TsApplication.getAppContext().fileList().forEach {
            if (it.endsWith(".txt"))
                if (!fileNameList.contains(it))
                    fileNameList.add(it)

        }
        //Timber.i("allFiles : ${fileNameList.toString()}")

        fileNameList.forEach { fileString ->
            try {
                if (!fileString.endsWith("device_info.txt")) {
                    if (checkFilesLastFiveDays(fileString)) {
                        val file = File(TsApplication.getAppContext().filesDir, fileString)
                        if (file.exists() && file.isFile) {
                            file.delete()
                        }
                    } else {
                        Timber.i("LogFlow: Nothing to delete")
                    }
                }
            } catch (e: Exception) {
                Timber.d(e, "LogFlow: Error while processing file $fileString")
            }
        }
        fileNameList.clear()

    }

    private fun checkFilesLastFiveDays(fileName: String): Boolean {
        var isTrue = false
        val fileSplit = fileName.split("_")
        if (fileSplit.size < 4) return false

        val fileSplit2 = fileSplit[3].split(".")

        val sdf = SimpleDateFormat(DATE_FORMAT_D_M_Y, Locale.getDefault())
        val fileDate = sdf.parse(fileSplit2[0]) ?: return false

        val calendar = Calendar.getInstance()
        calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -5)

        val dayFiveDaysBack = calendar.time

        if (fileDate < dayFiveDaysBack) {
            isTrue = true
        }

        return isTrue
    }


    private fun readFileData() {
        try {
            val fin: FileInputStream = TsApplication.getAppContext().openFileInput(apiLogFileName)
            var a: Int
            val temp = StringBuilder()
            while (fin.read().also { a = it } != -1) {
                temp.append(a.toChar())
            }
            Timber.d("reading Logs $temp")
            fin.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun bodyToString(request: Request): String? {
        return try {
            val copy: Request = request.newBuilder().build()
            val buffer = Buffer()
            copy.body?.writeTo(buffer)

            val requestBodyString = buffer.readUtf8()
            val preview = requestBodyString.take(5 * 1024) // 5KB limit
            val size = requestBodyString.length

            if (size > 5 * 1024) {
                "Request Body (size=${size} chars, showing first 5KB):\n$preview"
            } else {
                "Request Body (size=${size} chars):\n$requestBodyString"
            }
        } catch (e: IOException) {
            "Unable to execute"
        }
    }

    @SuppressLint("HardwareIds")
    private fun getSystemDetail(): String {
        return "Brand: ${Build.BRAND} \n" +
                "DeviceID: ${
                    Settings.Secure.getString(
                        TsApplication.getAppContext().contentResolver,
                        Settings.Secure.ANDROID_ID
                    )
                } \n" +
                "Model: ${Build.MODEL} \n" +
                "ID: ${Build.ID} \n" +
                "SDK: ${Build.VERSION.SDK_INT} \n" +
                "Manufacture: ${Build.MANUFACTURER} \n" +
                "Brand: ${Build.BRAND} \n" +
                "User: ${Build.USER} \n" +
                "Type: ${Build.TYPE} \n" +
                "Incremental: ${Build.VERSION.INCREMENTAL} \n" +
                "Host: ${Build.HOST} \n" +
                "Android Version: ${Build.VERSION.RELEASE} \n" +
                "App Version: ${BuildConfig.VERSION_NAME} \n" +
                "Device Language: ${Locale.getDefault().displayLanguage} \n" +
                "FCM Token : ${PreferenceUtils.getString(PREF_FCM_TOKEN)}"
    }
}





