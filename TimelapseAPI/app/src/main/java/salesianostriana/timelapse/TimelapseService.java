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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import salesianostriana.timelapse.bd.FotosDatabase;
import salesianostriana.timelapse.hiddenCamera.CameraConfig;
import salesianostriana.timelapse.hiddenCamera.CameraError;
import salesianostriana.timelapse.hiddenCamera.HiddenCameraService;
import salesianostriana.timelapse.hiddenCamera.HiddenCameraUtils;
import salesianostriana.timelapse.hiddenCamera.config.CameraFacing;
import salesianostriana.timelapse.hiddenCamera.config.CameraImageFormat;
import salesianostriana.timelapse.hiddenCamera.config.CameraResolution;
import salesianostriana.timelapse.Interfaces.ITrianaSatAPI;
import salesianostriana.timelapse.pojos.fotoInfoAPI.FotoInfo;
import salesianostriana.timelapse.bd.Foto;
import salesianostriana.timelapse.pojos.Preferencia;

import static android.R.attr.data;

/**
 * Servicio que realiza la captura y la subida de fotos en segundo plano.
 */
public class TimelapseService extends HiddenCameraService {

    String TAG = "TIMELAPSE_INFO";
    int cont = 1, bateria;
    Preferencia preferencia;
    FotosDatabase fotosDB;
    File ruta_sd = new File(Environment.getExternalStorageDirectory() + "/Android/data/salesianostriana.timelapse/files");
    File ruta_sd_resize = new File(Environment.getExternalStorageDirectory() + "/Android/data/salesianostriana.timelapse/resize");
    boolean estaSubiendo = false;

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

        subirFoto();

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

                            //Si está parada la subida, la reinicia
                            if (!estaSubiendo) {
                                subirFoto();
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
        String filename = "IMG_" + timeStamp + ".jpg";

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

            resizeFoto(bitmapImage, filename);//Guarda la foto redimensionada en el teléfono
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

    /**
     * Redimensiona la imagen haciendola más pequeña. Esta imagen es la que será subida al servidor.
     * Después de que sea subida, será eliminada del teléfono.
     * ruta_sd_resize --> donde se guardará las fotos redimensionadas
     *
     * @param bitmapImage Imagen
     * @param filename    Nombre de la imagen
     */
    public void resizeFoto(Bitmap bitmapImage, String filename) {
        int width = 1500, height = 2000;
        if (!ruta_sd_resize.exists()) {
            ruta_sd_resize.mkdir();
        }

        File fileImage = new File(ruta_sd_resize, filename);

        FileOutputStream fos = null;
        try {//Guarda la imagen redimensionada

            fos = new FileOutputStream(fileImage);
            bitmapImage = Bitmap.createScaledBitmap(bitmapImage, width, height,false);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.close();
        } catch (Exception e) {
            Log.e(TAG, "Error copiando archivo");

            e.printStackTrace();
        }

    }


    /**
     * Sube el archivo de la última foto capturada al servidor, si va bien la subida empieza la subida de la información a la API
     */
    public void subirFoto() {
        if (!checkInternet(getApplicationContext())) {//Si no hay internet cancela la subida
            estaSubiendo = false;
            return;
        }

        final Foto foto = getLastFotoNoSubida();

        if (foto == null) {//Si no hay fotos, cancela la subida
            estaSubiendo = false;
            Log.i(TAG, "No hay fotos no subidas");
            return;
        }

        final File fileFoto = new File(ruta_sd_resize, foto.getNombre());//Foto que subirá

        estaSubiendo = true;//Si llega hasta aqui, se podrá hacer la subida

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
                MultipartBody.Part.createFormData("fichero", fileFoto.getName(), requestFile);//'fichero' es el nombre en el que el servidor recibe la foto

        // finally, execute the request
        Call<ResponseBody> call = service.subirFoto(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    Log.i(TAG, "Retrofit success: " + fileFoto.getName());

                    subirFotoInfo(foto);//Sube información de la foto a la API
                } else {
                    Log.i(TAG, "Retrofit Error Code: " + response.code() + " " + fileFoto.getName());
                    subirFoto();//Intenta otra vez la subida
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Retrofit onfailure: " + t.getMessage() + " " + fileFoto.getName());
                subirFoto();//Intenta otra vez la subida
            }
        });

    }

    /**
     * Sube la información de la foto
     *
     * @param foto Objeto que lleva parte de la información de la foto
     */
    public void subirFotoInfo(final Foto foto) {
        String urlFoto = "http://www.salesianos-triana.com/dam/trianasat/files/" + foto.getNombre();//Construye URL foto

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ITrianaSatAPI.ENDPOINT_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ITrianaSatAPI service =
                retrofit.create(ITrianaSatAPI.class);

        String FORMAT_DATE = "dd-MM-yy_HH:mm:ss";
        String fecha_subida = new SimpleDateFormat(FORMAT_DATE).format(Calendar.getInstance().getTime());


        final FotoInfo fotoInfo = new FotoInfo(foto.getFechaMilisegundos(), fecha_subida, urlFoto, foto.getBateria(), getHrefProyecto());

        Call<ResponseBody> call = service.subirFotoInfo(fotoInfo);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    Log.i(TAG, "Retrofit InfoFoto success: " + foto.getNombre());

                    updateFoto(foto.getId(), 1);//Establece la foto como subida en la base de datos

                    //Elimina la foto redimensionada
                    final File fileFoto = new File(ruta_sd_resize, foto.getNombre());
                    if (fileFoto.delete()) {
                        Log.i(TAG, fileFoto.getAbsolutePath() + ": Borrado");
                    } else
                        Log.i(TAG, fileFoto.getAbsolutePath() + ": No borrado");

                    subirFoto();//Inicia subida
                } else {
                    Log.i(TAG, "Retrofit Error InfoFoto Code: " + response.code() + ", " + response.message() + ", " + foto.getNombre());
                    subirFoto();//Inicia subida
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Retrofit InfoFoto onfailure: " + t.getMessage() + " " + foto.getNombre());
                subirFoto();//Inicia subida
            }
        });
    }

    /**
     * Comprueba si hay internet
     *
     * @param ctx Contexto
     * @return
     */
    public boolean checkInternet(Context ctx) {
        boolean bandera = true;
        ConnectivityManager conMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null || !i.isConnected() || !i.isAvailable()) {
            bandera = false;
            Log.i(TAG, "No hay internet, no se puede realizar la subida");
        } else {
            Log.i(TAG, "Hay internet, iniciando subida");
        }

        return bandera;

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
