/*
 * Copyright 2016 Keval Patel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rogeliorb.camara2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.CameraError;
import com.androidhiddencamera.HiddenCameraService;
import com.androidhiddencamera.HiddenCameraUtils;
import com.androidhiddencamera.config.CameraFacing;
import com.androidhiddencamera.config.CameraImageFormat;
import com.androidhiddencamera.config.CameraResolution;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Keval on 11-Nov-16.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class DemoCamService extends HiddenCameraService {

    String TAG = "TIMELAPSE";
    int cont = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            if (HiddenCameraUtils.canOverDrawOtherApps(this)) {
                final CameraConfig cameraConfig = new CameraConfig()
                        .getBuilder(this)
                        .setCameraFacing(CameraFacing.REAR_FACING_CAMERA)
                        .setCameraResolution(CameraResolution.HIGH_RESOLUTION)
                        .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                        .build();

                startCamera(cameraConfig);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            startCamera(cameraConfig);
                            takePicture();
                            cont++;
                            Log.i(TAG, "Foto " + cont + " hecha");
                            handler.postDelayed(this, 10000); //una vez iniciada, cada 2 seg
                        } else
                            Log.i(TAG, "No tiene permiso");

                    }
                }, 0); //Se inicia al momento
            } else {

                //Open settings to grant permission for "Draw other apps".
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
            }
        } else {
            //TODO Ask your parent activity for providing runtime permission
            Toast.makeText(this, "Camera permission not available", Toast.LENGTH_SHORT).show();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onImageCapture(@NonNull File imageFile) {
        String dir = imageFile.getAbsolutePath();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

        copyImageToSD(bitmap);

        stopSelf();
    }

    private void copyImageToSD(Bitmap bitmapImage) {

        String FORMAT_DATE = "dd-MM-yyyy_HHmmss";
        String timeStamp = new SimpleDateFormat(FORMAT_DATE).format(Calendar.getInstance().getTime());
        String filename = "IMG_" + timeStamp + ".jpg";

        // File ruta_sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //File ruta_sd = Environment.getExternalStorageDirectory();
        File ruta_sd = getPathSD();

        // Create imageDir
        File fileImage = new File(ruta_sd, filename);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileImage);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            Log.i(TAG, "Foto " + cont + " copiada");
            Log.d(TAG, "Tamaño: " + fileImage.length());
            Log.d(TAG, "RutaA: " + fileImage.getAbsolutePath());
            Log.d(TAG, "Ruta: " + fileImage.getPath());

            fos.close();
        } catch (Exception e) {
            Log.e(TAG, "Error copiando archivo");

            e.printStackTrace();
        }

    }

    public String[] getStorageDirectories() {
        String [] storageDirectories;
        String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            List<String> results = new ArrayList<String>();
            Context ctxt = this;
            File[] externalDirs = ctxt.getExternalFilesDirs(null);
            for (File file : externalDirs) {
                String path = file.getPath().split("/Android")[0];
                if((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Environment.isExternalStorageRemovable(file))
                        || rawSecondaryStoragesStr != null && rawSecondaryStoragesStr.contains(path)){
                    results.add(path);
                }
            }
            storageDirectories = results.toArray(new String[0]);
        }else{
            final Set<String> rv = new HashSet<String>();

            if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
                final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
                Collections.addAll(rv, rawSecondaryStorages);
            }
            storageDirectories = rv.toArray(new String[rv.size()]);
        }
        return storageDirectories;
    }

    public File getPathSD() {
        //String[] storage = getStorageDirectories();
        String path = "";
        File ruta_sd = new File("/storage/sdcard1/trianasat");

        /*if (new File("/storage/sdcard1/Pictures/").exists()) {
            path = "/storage/sdcard1/Pictures/";
            ruta_sd = new File(path);
        } else if (new File("/storage/extSdCard/").exists()) {
            path = "/storage/extSdCard/";
            ruta_sd = new File(path);
        } else if (new File("/storage/usbcard1/").exists()) {
            path = "/storage/usbcard1/";
            ruta_sd = new File(path);
        } else if (new File("/storage/sdcard0/").exists()) {
            path = "/storage/sdcard0/";
            ruta_sd = new File(path);
        } else if (new File("/storage/extSdCard/").exists()) {
            path = "/storage/extSdCard/";
            ruta_sd = new File(path);
        } else if (new File("/storage/sdcard1/").exists()) {
            path = "/storage/sdcard1/";
            ruta_sd = new File(path);
        } else if (new File("/storage/usbcard1/").exists()) {
            path = "/storage/usbcard1/";
            ruta_sd = new File(path);
        } else if (new File("/storage/sdcard0/").exists()) {
            path = "/storage/sdcard0/";
            ruta_sd = new File(path);
        } else if (new File("/sdcard/").exists()) {//emulador
            path = "/sdcard/";
            ruta_sd = new File(path);
        } else {
            ruta_sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);//Sino mandamos el directorio publico del teléfono
        }

        //ruta_sd = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        ruta_sd = new File("/mnt/ext_card/trianasat/");
        */

        return ruta_sd;
    }

    @Override
    public void onCameraError(@CameraError.CameraErrorCodes int errorCode) {
        switch (errorCode) {
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
                //Camera open failed. Probably because another application
                //is using the camera
                Toast.makeText(this, "Cannot open camera.", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                //camera permission is not available
                //Ask for the camra permission before initializing it.
                Toast.makeText(this, "Camera permission not available.", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION:
                //Display information dialog to the user with steps to grant "Draw over other app"
                //permission for the app.
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
                Toast.makeText(this, "Your device does not have front camera.", Toast.LENGTH_LONG).show();
                break;
        }

        stopSelf();
    }


}
