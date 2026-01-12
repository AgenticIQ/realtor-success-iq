package com.realtorsuccessiq.updates

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.realtorsuccessiq.BuildConfig
import java.io.File

object NightlyUpdateManager {
    private val NIGHTLY_APK_URL: String
        get() = "https://github.com/AgenticIQ/realtor-success-iq/releases/download/${BuildConfig.UPDATE_TAG}/${BuildConfig.UPDATE_APK_NAME}"

    private val APK_FILE_NAME: String
        get() = BuildConfig.UPDATE_APK_NAME

    fun startDownloadAndInstall(context: Context): Result {
        val appContext = context.applicationContext

        // Android requires user consent for installs from "unknown sources" unless installed via Play Store.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canInstall = appContext.packageManager.canRequestPackageInstalls()
            if (!canInstall) {
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:${appContext.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                appContext.startActivity(intent)
                return Result.NeedsUnknownSourcesPermission
            }
        }

        val downloadsDir = appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            ?: return Result.Error("No external files directory available")
        val destinationFile = File(downloadsDir, APK_FILE_NAME)
        if (destinationFile.exists()) destinationFile.delete()

        val request = DownloadManager.Request(Uri.parse(NIGHTLY_APK_URL))
            .setTitle("RealtorSuccessIQ update")
            .setDescription("Downloading latest Nightly build…")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setDestinationUri(Uri.fromFile(destinationFile))

        val dm = appContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = dm.enqueue(request)

        // When download completes, prompt install.
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                val completedId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
                if (completedId != downloadId) return
                try {
                    ctx.unregisterReceiver(this)
                } catch (_: Exception) {
                    // ignore
                }

                promptInstall(ctx, destinationFile)
            }
        }

        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        if (Build.VERSION.SDK_INT >= 33) {
            ContextCompat.registerReceiver(appContext, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            appContext.registerReceiver(receiver, filter)
        }

        return Result.Started(downloadId)
    }

    private fun promptInstall(context: Context, apkFile: File) {
        if (!apkFile.exists()) return

        val apkUri = FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.fileprovider",
            apkFile
        )

        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(installIntent)
    }

    sealed class Result {
        data class Started(val downloadId: Long) : Result()
        data object NeedsUnknownSourcesPermission : Result()
        data class Error(val message: String) : Result()
    }
}


