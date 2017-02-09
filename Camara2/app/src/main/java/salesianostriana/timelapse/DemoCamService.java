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

package salesianostriana.timelapse;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import salesianostriana.timelapse.HiddenCamera.CameraConfig;
import salesianostriana.timelapse.HiddenCamera.CameraError;
import salesianostriana.timelapse.HiddenCamera.HiddenCameraService;
import salesianostriana.timelapse.HiddenCamera.HiddenCameraUtils;
import salesianostriana.timelapse.HiddenCamera.config.CameraFacing;
import salesianostriana.timelapse.HiddenCamera.config.CameraImageFormat;
import salesianostriana.timelapse.HiddenCamera.config.CameraResolution;
import salesianostriana.timelapse.Interfaces.ITrianaSatAPI;
import salesianostriana.timelapse.Pojos.Foto;
import salesianostriana.timelapse.Pojos.Preferencia;

/**
 * Created by Keval on 11-Nov-16.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class DemoCamService extends HiddenCameraService {

    String TAG = "TIMELAPSE_INFO";
    int cont = 1;
    Preferencia preferencia;
    int bateria;
    FotosDatabase fotosDB;
    File ruta_sd = new File("/storage/emulated/0/Android/data/salesianostriana.timelapse/files");

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        fotosDB = new FotosDatabase(this);
        preferencia = getPreferencia();

        Log.i(TAG, preferencia.toString());

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
                            registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

                            int memoriaActual = getMemoriaActual();

                            if (memoriaActual <= preferencia.getMemoria()) {
                                switch (preferencia.getCalidad()) {
                                    case "Alta":
                                        Log.i("CALIDÁ", "ALTA");
                                        cameraConfig.getBuilder(getApplicationContext()).setCameraResolution(CameraResolution.HIGH_RESOLUTION);
                                        break;
                                    case "Media":
                                        Log.i("CALIDÁ", "MEDIA");
                                        cameraConfig.getBuilder(getApplicationContext()).setCameraResolution(CameraResolution.MEDIUM_RESOLUTION);
                                        break;
                                    case "Baja":
                                        Log.i("CALIDÁ", "BAJA");
                                        cameraConfig.getBuilder(getApplicationContext()).setCameraResolution(CameraResolution.LOW_RESOLUTION);
                                        break;
                                    default:
                                        cameraConfig.getBuilder(getApplicationContext()).setCameraResolution(CameraResolution.HIGH_RESOLUTION);
                                        break;
                                }
                            }

                            startCamera(cameraConfig);

                            try {
                                Thread.sleep(5000);//5 segundos para que se pueda preparar la cámara
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if (bateria <= preferencia.getBateria() && bateria != 0) {

                                Log.i("Frec:", "Ha entrao en el configurao");
                                takePicture();
                                Log.i(TAG, "Foto " + cont + " hecha");

                                handler.postDelayed(this, preferencia.getFrecuencia() * 1000);//Convierte los segundos guardados en preferencias en milisegundos

                            } else {
                                Log.i("Frec:", "Ha entrao en el por defecto");
                                takePicture();
                                Log.i(TAG, "Foto " + cont + " hecha");

                                handler.postDelayed(this, 5000);//5 Segundos por defecto
                            }


                        } else
                            Log.i(TAG, "No tiene permiso");

                    }
                }, 0); //Se inicia al momento
            } else {
                //Open settings to grant permission for "Draw other apps".
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
            }
        } else {
            Toast.makeText(this, "Camera permission not available", Toast.LENGTH_SHORT).show();
        }


        return START_NOT_STICKY;
    }

    @Override
    public void onImageCapture(@NonNull File imageFile) {
        String dir = imageFile.getAbsolutePath();
        Matrix matrix = new Matrix();
        matrix.postRotate(90);


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        copyImageToSD(bitmap);

        if (imageFile.delete()) {
            Log.i(TAG, "DELETE: " + imageFile.getAbsolutePath());
        } else
            Log.e(TAG, "NO DELETE: " + imageFile.getAbsolutePath());

        stopSelf();
    }

    private void copyImageToSD(Bitmap bitmapImage) {

        Log.i(TAG, "Intento de copia de Foto " + cont);

        String FORMAT_DATE = "dd-MM-yy_HH:mm:ss";
        String timeStamp = new SimpleDateFormat(FORMAT_DATE).format(Calendar.getInstance().getTime());
        long milisegundos = System.currentTimeMillis();
        String filename = "IMG_" + timeStamp + ".jpg";

        if (!ruta_sd.exists()) {
            ruta_sd.mkdir();
        }

        // Create imageDir
        File fileImage = new File(ruta_sd, filename);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileImage);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            insertFoto(new Foto(fileImage.getName(), milisegundos, (double) bateria, 0));
            Log.i(TAG, "Foto " + cont + " copiada");

            cont++;

            if (checkInternet(getApplicationContext()))
                subirFotosNoSubidas();

            fos.close();
        } catch (Exception e) {
            Log.e(TAG, "Error copiando archivo");

            e.printStackTrace();
        }
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


    public Preferencia getPreferencia() {
        Preferencia preferencia;

        SharedPreferences shaPref = PreferenceManager.getDefaultSharedPreferences(this);

        String bateria = shaPref.getString("bateria", "");
        String calidad = shaPref.getString("calidad", "");
        String memoria = shaPref.getString("memoria", "");
        String frecuencia = shaPref.getString("frecuencia", "");

        preferencia = new Preferencia(bateria, calidad, memoria, frecuencia);

        return preferencia;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

        try {
            if (mBatInfoReceiver != null)
                unregisterReceiver(mBatInfoReceiver);

        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();

    }

    public void subirFotosNoSubidas() {
        List<Foto> listFotos = getFotosNoSubidas();
        //getAllFotos();

        for (Foto foto : listFotos) {
            subirFoto(foto);
        }
    }

    public void subirFoto(final Foto foto) {
        final File fileFoto = new File(ruta_sd, foto.getNombre());

        //Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ITrianaSatAPI.ENDPOINT_SALESIANOS)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // create upload service client
        ITrianaSatAPI service =
                retrofit.create(ITrianaSatAPI.class);

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), fileFoto);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("fichero", fileFoto.getName(), requestFile);

        // finally, execute the request
        Call<ResponseBody> call = service.subirDatosFoto(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    Log.i(TAG, "success: " + fileFoto.getName());

                    updateFoto(foto.getId(), 1);//TODO
                } else {
                    Log.i(TAG, "Code: " + response.code() + " " + fileFoto.getName());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onfailure: " + t.getMessage() + " " + fileFoto.getName());
            }
        });
    }

    public boolean checkInternet(Context ctx) {
        boolean bandera = true;
        ConnectivityManager conMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null || !i.isConnected() || !i.isAvailable()) {
            bandera = false;
            Log.i(TAG, "no internet");
        } else {
            Log.i(TAG, "si internet");
        }

        return bandera;

    }

    /*****************************************/
    /*********** BASE DE DATOS **************/
    /*****************************************/
    public void insertFoto(Foto foto) {
        /*Abre base de datos en escritura*/
        try {
            fotosDB.openWrite();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*Inserta*/
        try {
            fotosDB.insert(foto);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*Cierra base de datos*/
        fotosDB.close();
    }

    public List<Foto> getAllFotos() {

        Log.i(TAG, "*********** FOTOS ***************");
        /*Abre base de datos en lectura*/
        try {
            fotosDB.openRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*Lee todos los registros*/
        List<Foto> listFotos = fotosDB.getAll();

        /*Cierra base de datos*/
        fotosDB.close();

        for (Foto f : listFotos) {
            Log.d("FOTOS DB", f.toString());
        }

        Log.i(TAG, "**************************************");
        return listFotos;
    }

    public List<Foto> getFotosSubidas() {

        Log.d(TAG, "**************FOTOS SUBIDAS*******");
        /*Abre base de datos en lectura*/
        try {
            fotosDB.openRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*Lee las fotos subidas*/
        List<Foto> listFotos = fotosDB.getSubidas();

        /*Cierra base de datos*/
        fotosDB.close();

        for (Foto f : listFotos) {
            Log.d(TAG, f.toString());
        }
        Log.i(TAG, "**************************************");
        return listFotos;
    }

    public List<Foto> getFotosNoSubidas() {
        Log.d(TAG, "**************FOTOS NO SUBIDAS*******");

        /*Abre base de datos en lectura*/
        try {
            fotosDB.openRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*Lee las fotos no subidas*/
        List<Foto> listFotos = fotosDB.getNoSubidas();

        /*Cierra base de datos*/
        fotosDB.close();

        for (Foto f : listFotos) {
            Log.d(TAG, f.toString());
        }

        Log.i(TAG, "**************************************");
        return listFotos;
    }

    public void updateFoto(long id, int subida) {
        /*Abre base de datos en escritura*/
        try {
            fotosDB.openWrite();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*Actualiza foto*/
        fotosDB.updateSubida(id, subida);
        Log.d(TAG, "Registro con ID " + id + " actualizado. Subida = " + subida);
        /*Cierra base de datos*/
        fotosDB.close();
    }

    /********************************************/
    /*********** MEMORIA & BATERIA **************/
    /********************************************/
    private int getMemoriaActual() {
        StatFs stat_fs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double avail_sd_space = (double) stat_fs.getAvailableBlocks() * (double) stat_fs.getBlockSize();
        double GB_Available = (avail_sd_space / 1073741824);

        String numberFormat = String.format("%f", GB_Available);
        return Integer.parseInt(numberFormat.split(",")[0]);

    }


    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            bateria = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 100);
            Log.d(TAG, bateria + "%");
        }
    };

}
