package com.bitla.restaurant_app.presentation.utils

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bitla.restaurant_app.R

import timber.log.Timber
import java.net.URL

class DownloadPdf {
    companion object {

        fun checkPermission(permission: String, activity: Activity): Boolean {
            return ContextCompat.checkSelfPermission(
                activity,
                permission
            ) != PackageManager.PERMISSION_DENIED
        }

        fun onRequestPermissionsResult(
            requestCode: Int,
            permission: String,

            context: Activity
        ) {
            ActivityCompat.requestPermissions(context, arrayOf(permission), requestCode)
        }

        fun downloadReportPdf(context: Context, urlString: String?) {

            try {
                if (urlString != null && urlString.isNotEmpty()) {
                    val url = URL(urlString)
                    val file = url.file
                    val fileName: String = file.substring(file.lastIndexOf('/').plus(1))

                    val request = DownloadManager.Request(
                        Uri.parse(urlString)
                    )
                        .setTitle(fileName)
                        .setDescription("downloading...")
                        .setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS,
                            fileName
                        )
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setAllowedOverMetered(true)

                    val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    dm.enqueue(request)
                    context.toast(context.resources.getString(R.string.downloaded))
                } else
                    context.toast(context.resources.getString(R.string.pdfurl_error))
            } catch (e: Exception) {
                context.toast(context.resources.getString(R.string.something_went_wrong))
                Timber.d("exceptionMsg ${e.message}")
            }
        }
    }
}