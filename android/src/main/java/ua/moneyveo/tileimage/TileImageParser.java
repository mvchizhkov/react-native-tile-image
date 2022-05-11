package ua.moneyveo.tileimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class TileImageParser extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;

    TileImageParser(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    @NonNull
    @Override
    public String getName() {
        return "TileImageParser";
    }

    @ReactMethod
    public void cropAndSave(final String uri, final int partHeight, final Callback partsCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bmpInitial = BitmapFactory.decodeFile(uri);
                int width = bmpInitial.getWidth(), overallHeight = bmpInitial.getHeight();
                Log.d("MY", "save and crop");
                int partsCount = (int) Math.ceil((double) overallHeight / partHeight);
                WritableArray array = Arguments.createArray();
                int tempOverallHeight = overallHeight;
                int currentHeight = 0;
                for (int i = 0; i < partsCount; i++) {
                    Log.d("MY", "save and crop: " + i);
                    Bitmap bib;
                    if (i != partsCount - 1) {
                        bib = Bitmap.createBitmap(bmpInitial, 0, currentHeight, width, partHeight);
                        currentHeight += partHeight;
                    } else {
                        bib = Bitmap.createBitmap(bmpInitial, 0, currentHeight, width, tempOverallHeight);
                    }
                    tempOverallHeight -= partHeight;

                    File path = new File(reactContext.getApplicationInfo().dataDir);

                    OutputStream outStream = null;
                    path.mkdirs();
                    Log.d("MY", "new file 4");

                    File file = new File(path, "agr_temp_" +  i + ".png");
                    String pathS = file.getPath();
                    array.pushString(pathS);
                    try {
                        outStream = new FileOutputStream(file);
                        bib.compress(Bitmap.CompressFormat.PNG, 85, outStream);
                        outStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        bib.recycle();
                    }
                }


                partsCallback.invoke(array);
            }
        }).start();


    }

}
