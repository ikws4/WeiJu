package io.ikws4.weiju.utilities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.ikws4.weiju.R
import java.io.File


object LogcatManager {

    fun show(context: Context, t: Throwable) {
        val message = Log.getStackTraceString(t)
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.error_message)
            .setMessage(message)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.copy) { _, _ ->
                val cbm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("LogcatManager", message)
                cbm.primaryClip = clipData
            }.show()
        saveToFile(context, t)
    }

    fun show(context: Context, s: String, isShowClearButton: Boolean = false) {
        val builder = MaterialAlertDialogBuilder(context)
            .setTitle(R.string.error_message)
            .setMessage(s)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.copy) { _, _ ->
                val cbm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("LogcatManager", s)
                cbm.primaryClip = clipData
            }
        if (isShowClearButton) {
            builder.setNeutralButton(R.string.clear) { _, _ ->
                deleteLogFile(context)
            }
        }
        builder.show()
    }

    private fun deleteLogFile(context: Context) {
        val file = File(context.filesDir, "logcat.txt")
        if (file.exists()) {
            file.delete()
        }
    }

    fun saveToFile(context: Context, t: Throwable, showLog: Boolean = false) {
        if (showLog) {
            t.printStackTrace()
        }
        val file = File(context.filesDir, "logcat.txt")
        with(file) {
            if (!exists()) {
                createNewFile()
            }
            appendText(Log.getStackTraceString(t))
        }
    }

    fun getSavedLog(context: Context): String {
        val file = File(context.filesDir, "logcat.txt")
        with(file) {
            if (!exists()) {
                createNewFile()
            }
            return file.readText()
        }
    }
}