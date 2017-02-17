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

package salesianostriana.timelapselocal;

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

import salesianostriana.timelapselocal.bd.FotosDatabase;
import salesianostriana.timelapselocal.hiddenCamera.CameraConfig;
import salesianostriana.timelapselocal.hiddenCamera.CameraError;
import salesianostriana.timelapselocal.hiddenCamera.HiddenCameraService;
import salesianostriana.timelapselocal.hiddenCamera.HiddenCameraUtils;
import salesianostriana.timelapselocal.hiddenCamera.config.CameraFacing;
import salesianostriana.timelapselocal.hiddenCamera.config.CameraImageFormat;
import salesianostriana.timelapselocal.hiddenCamera.config.CameraResolution;
import salesianostriana.timelapselocal.bd.Foto;
import salesianostriana.timelapselocal.pojos.Preferencia;

/**
 * Servicio que realiza la captura y la subida de fotos en segundo plano.
 */
public class TimelapseService extends HiddenCameraService {

    String TAG = "TIMELAPSE_INFO", NOMBRE_PROYECTO = "";
    int cont = 1, bateria;
    Preferencia preferencia;
    FotosDatabase fotosDB;
    File ruta_sd = new File(Environment.getExternalStorageDirectory() + "/Android/data/salesianostriana.timelapselocal/files");


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Primera fúnción que ejecuta el servicio
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        fotosDB = new FotosDatabase(this);
        preferencia = getPreferencia();

        Log.i(TAG, "Preferencia: " + preferencia);

        NOMBRE_PROYECTO = getNombreProyecto();


        /***************** CÁMARA *****************/
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            if (HiddenCameraUtils.canOverDrawOtherApps(this)) {
                final CameraConfig cameraConfig = new CameraConfig()
                        .getBuilder(this)
                        .setCameraFacing(CameraFacing.REAR_FACING_CAMERA)
                        .setCameraResolution(CameraResolution.HIGH_RESOLUTION)
                        .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                        .build();

                startCamera(cameraConfig);

                Toast.makeText(this, "Servicio iniciado", Toast.LENGTH_SHORT).show();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

                            int memoriaRestanteActual = getMemoriaDisponible();

                            //Establece la calidad en la que se será tomada la foto según la memoria restante
                            if (memoriaRestanteActual <= preferencia.getMemoria()) {
                                switch (preferencia.getCalidad()) {
                                    case "Alta":
                                        Log.i(TAG, "Calidad Alta");
                                        cameraConfig.getBuilder(getApplicationContext()).setCameraResolution(CameraResolution.HIGH_RESOLUTION);
                                        break;
                                    case "Media":
                                        Log.i(TAG, "Calidad Media");
                                        cameraConfig.getBuilder(getApplicationContext()).setCameraResolution(CameraResolution.MEDIUM_RESOLUTION);
                                        break;
                                    case "Baja":
                                        Log.i(TAG, "Calidad Baja");
                                        cameraConfig.getBuilder(getApplicationContext()).setCameraResolution(CameraResolution.LOW_RESOLUTION);
                                        break;
                                    default:
                                        cameraConfig.getBuilder(getApplicationContext()).setCameraResolution(CameraResolution.HIGH_RESOLUTION);
                                        break;
                                }
                            }

                            startCamera(cameraConfig);//Inicia cámara

                            //5 segundos para que se pueda preparar la cámara
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            //Sólo hará fotos si el número de fotos guardadas es menos al elegido por el usuario en preferencias
                            if (getCountFotos() < preferencia.getNumFotos()) {
                                if (bateria <= preferencia.getBateria() && bateria != 0) {

                                    Log.i(TAG, "Frecuencia: Ha entrado en la configuración de preferencias");
                                    takePicture();
                                    Log.i(TAG, "Foto " + cont + " hecha");

                                    handler.postDelayed(this, preferencia.getFrecuencia() * 1000);//Convierte los segundos guardados en preferencias en milisegundos

                                } else {
                                    Log.i(TAG, "Frecuencia: Ha entrado en la configuración por defecto");
                                    takePicture();
                                    Log.i(TAG, "Foto " + cont + " hecha");
                                    handler.postDelayed(this, 5000);//5 Segundos por defecto
                                }
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

    /**
     * Función a la que llega cuando se ha hecho una foto
     *
     * @param imageFile Archivo de la imagen
     */
    @Override
    public void onImageCapture(@NonNull File imageFile) {
        Matrix matrix = new Matrix();//Rota la imagen
        matrix.postRotate(90);

        //Transforma el archivo en bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        copyImage(bitmap);//Copia la imagen en la tarjeta SD

        stopSelf();//Para las capturas de imágenes
    }

    /**
     * Copia la imagen en la memoria SD e inserta la información de la foto en la base de datos
     * ruta_sd --> ruta donde copia la imagen
     *
     * @param bitmapImage
     */
    private void copyImage(Bitmap bitmapImage) {

        String FORMAT_DATE = "dd-MM-yy_HH:mm:ss";
        String timeStamp = new SimpleDateFormat(FORMAT_DATE).format(Calendar.getInstance().getTime());
        long milisegundos = System.currentTimeMillis();
        String filename = "IMG_" + NOMBRE_PROYECTO + "_" + timeStamp + ".jpg";

        if (!ruta_sd.exists()) {
            ruta_sd.mkdir();
        }

        // Create imageDir
        File fileImage = new File(ruta_sd, filename);

        FileOutputStream fos = null;
        try {//Transforma bitmap a file y lo guarda
            fos = new FileOutputStream(fileImage);

            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            insertFoto(new Foto(fileImage.getName(), milisegundos, (double) bateria, 0));//Inserta foto en la base de datos

            Log.i(TAG, "Foto " + cont + " copiada Ruta: " + fileImage.getAbsolutePath());

            cont++;

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




    /*****************************************/
    /*********** BASE DE DATOS **************/
    /*****************************************/

    /**
     * Añade una foto a la base de datos
     *
     * @param foto
     */
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

    /**
     * Consulta la última foto no subida
     *
     * @return
     */
    public Foto getLastFotoNoSubida() {

        /*Abre base de datos en lectura*/
        try {
            fotosDB.openRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*Lee última foto no subida*/
        Foto foto = fotosDB.getLastNoSubida();

        /*Cierra base de datos*/
        fotosDB.close();

        return foto;
    }

    /**
     * Devuelve el número total de fotos guardadas
     *
     * @return
     */
    public int getCountFotos() {

        /*Abre base de datos en lectura*/
        try {
            fotosDB.openRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*Lee el número de fotos*/
        int contFotos = fotosDB.getCount();

        /*Cierra base de datos*/
        fotosDB.close();

        return contFotos;
    }

    /**
     * Actualiza el estado de subida de una foto
     *
     * @param id     ID de la foto en la base de datos
     * @param subida 0 --> no subida, 1 --> subida
     */
    public void updateFoto(long id, int subida) {
        /*Abre base de datos en escritura*/
        try {
            fotosDB.openWrite();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*Actualiza foto*/
        fotosDB.updateSubida(id, subida);
        //Log.i(TAG, "Registro con ID " + id + " actualizado. Subida = " + subida);

        /*Cierra base de datos*/
        fotosDB.close();
    }

    /********************************************/
    /*********** MEMORIA & BATERIA **************/
    /********************************************/

    /**
     * Consulta los gigabytes de memoria restantes
     *
     * @return
     */
    private int getMemoriaDisponible() {
        StatFs stat_fs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double avail_sd_space = (double) stat_fs.getAvailableBlocks() * (double) stat_fs.getBlockSize();
        double GB_Available = (avail_sd_space / 1073741824);

        String numberFormat = String.format("%f", GB_Available);
        return Integer.parseInt(numberFormat.split(",")[0]);
    }

    /**
     * Consulta la bateria restante
     */
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            bateria = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 100);
            //Log.i(TAG, bateria + "%");
        }
    };

    /********************************************/
    /*********** OTROS **************************/
    /********************************************/

    /**
     * Consulta las preferencias guardadas
     *
     * @return
     */
    public Preferencia getPreferencia() {
        Preferencia preferencia;

        SharedPreferences shaPref = PreferenceManager.getDefaultSharedPreferences(this);

        String bateria = shaPref.getString("bateria", "");
        String calidad = shaPref.getString("calidad", "");
        String memoria = shaPref.getString("memoria", "");
        String frecuencia = shaPref.getString("frecuencia", "");
        String numFotos = shaPref.getString("num_fotos", "");

        preferencia = new Preferencia(bateria, calidad, memoria, frecuencia, numFotos);

        return preferencia;
    }

    /**
     * Consulta la url del proyecto de la API en preferencias
     */
    public String getHrefProyecto() {
        SharedPreferences prefs =
                getSharedPreferences(Constantes.PREFERENCIAS_API, Context.MODE_PRIVATE);

        String url = prefs.getString(Constantes.PREF_HREF_PROYECTO, "");

        return url;
    }

    /**
     * Consulta nombre del proyecto vinculado
     */
    public String getNombreProyecto() {
        SharedPreferences prefs =
                getSharedPreferences(Constantes.PREFERENCIAS_API, Context.MODE_PRIVATE);

        return prefs.getString(Constantes.PREF_NOMBRE_PROYECTO, "");
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
}
