package com.beebetter.wifer.util;

import android.util.Log;
import androidx.databinding.ObservableArrayList;
import androidx.lifecycle.MutableLiveData;
import okhttp3.ResponseBody;

import java.io.*;

import static android.content.ContentValues.TAG;
import static com.beebetter.wifer.AppConfig.BYTE;
import static com.beebetter.wifer.AppConfig.MEGABIT;

public class ProgressHelper {

    public static void measureDownloadSpeed(ResponseBody body, MutableLiveData<String> downloadSpeed,
                                            long startDownloadTime, ObservableArrayList<Double> allDownloadSpeeds) {
        try {

            InputStream is = null;

            try {
                Log.d(TAG, "File Size=" + body.contentLength());

                is = body.byteStream();

                byte data[] = new byte[4096];
                int count;
                int progress = 0;
                long passedTime =0;
                double speed = 0;
                while ((count = is.read(data)) != -1) {
                    progress +=count;
                    passedTime = System.currentTimeMillis() - startDownloadTime;
                    if(passedTime!=0) {
                        speed =(( Double.valueOf(progress)*BYTE/MEGABIT) / (Double.valueOf(passedTime)/1000));
                        downloadSpeed.postValue(String.valueOf(String.format("%.2f", speed)));
                        allDownloadSpeeds.add(speed);
                    }
                    Log.d(TAG, "Progress M: " + ( progress/ MEGABIT) + "/" + passedTime + " >>>> " + String.valueOf(speed));
                }

                Log.d(TAG, "File saved successfully!");
                return;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Failed to save the file!");
                return;
            } finally {
                if (is != null) is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
}
