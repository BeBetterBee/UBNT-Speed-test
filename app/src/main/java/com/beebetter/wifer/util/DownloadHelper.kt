package com.beebetter.wifer.util

import android.content.ContentValues.TAG
import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import com.beebetter.wifer.AppConfig.Companion.BYTE
import com.beebetter.wifer.AppConfig.Companion.MEGABIT
import okhttp3.ResponseBody
import java.io.IOException
import java.io.InputStream

object DownloadHelper {

    fun measureDownloadSpeed(
        body: ResponseBody, downloadSpeed: MutableLiveData<String>,
        startDownloadTime: Long, allDownloadSpeeds: ObservableArrayList<Double>
    ) {
        try {

            var inputStream: InputStream? = null

            try {
                Log.d(TAG, "File Size=" + body.contentLength())

                inputStream = body.byteStream()

                val data = ByteArray(4096)
                var count: Int
                var progress = 0
                var passedTime: Long = 0
                var speed = 0.0
                count = inputStream.read(data)
                while (count != -1) {
                    progress += count
                    passedTime = System.currentTimeMillis() - startDownloadTime
                    if (passedTime != 0L) {
                        speed =
                            java.lang.Double.valueOf(progress.toDouble()) * BYTE / MEGABIT / (java.lang.Double.valueOf(
                                passedTime.toDouble()
                            ) / 1000)
                        downloadSpeed.postValue(String.format("%.2f", speed))
                        allDownloadSpeeds.add(speed)
                    }
                    count = inputStream.read(data)
                    Log.d(TAG, "Progress M: " + progress / MEGABIT + "/" + passedTime + " >>>> " + speed.toString())
                }

                return
            } catch (e: IOException) {
                e.printStackTrace()
                return
            } finally {
                inputStream?.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }

    }
}
