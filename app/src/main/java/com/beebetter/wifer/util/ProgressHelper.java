package com.beebetter.wifer.util;

import android.os.Environment;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.beebetter.api.StsService;
import com.beebetter.base.util.RxUtil;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Response;

import java.io.*;

import static android.content.ContentValues.TAG;
import static com.beebetter.wifer.AppConfig.MEGABYTE;

public class ProgressHelper {

    public static void measureDownloadSpeed(ResponseBody body, MutableLiveData<String> downloadSpeed,long startDownloadTime) {
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
                        speed =(( Double.valueOf(progress)/Double.valueOf(MEGABYTE)) / (Double.valueOf(passedTime)/1000));
                        downloadSpeed.postValue(String.valueOf(speed));
                    }
                    Log.d(TAG, "Progress M: " + ( progress/MEGABYTE) + "/" + passedTime + " >>>> " + String.valueOf(speed));
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
            Log.d(TAG, "Failed to save the file!");
            return;
        }
    }
}
