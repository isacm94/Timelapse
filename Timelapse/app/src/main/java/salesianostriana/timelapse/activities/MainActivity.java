<<<<<<< HEAD:Timelapse/app/src/main/java/salesianostriana/timelapse/activities/MainActivity.java
package salesianostriana.timelapse.activities;

import android.Manifest;
=======
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

package salesianostriana.timelapse.Activities;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
>>>>>>> d5f6d3cbfcbbffe706cf45ac0299d8d0eeb67c62:Camara2/app/src/main/java/salesianostriana/timelapse/Activities/MainActivity.java
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
<<<<<<< HEAD:Timelapse/app/src/main/java/salesianostriana/timelapse/activities/MainActivity.java
=======
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
>>>>>>> d5f6d3cbfcbbffe706cf45ac0299d8d0eeb67c62:Camara2/app/src/main/java/salesianostriana/timelapse/Activities/MainActivity.java
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

<<<<<<< HEAD:Timelapse/app/src/main/java/salesianostriana/timelapse/activities/MainActivity.java
import salesianostriana.timelapse.Constantes;
=======
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import salesianostriana.timelapse.DemoCamService;
import salesianostriana.timelapse.Interfaces.ITrianaSatAPI;
import salesianostriana.timelapse.Pojos.PreferenciaAPI;
import salesianostriana.timelapse.Pojos.hrefAPI.ListProyectos;
>>>>>>> d5f6d3cbfcbbffe706cf45ac0299d8d0eeb67c62:Camara2/app/src/main/java/salesianostriana/timelapse/Activities/MainActivity.java
import salesianostriana.timelapse.R;

public class MainActivity extends AppCompatActivity {

    TextView estadoServicio;
    String TAG = "Fotos";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /******* PREFERENCIAS API ***********/
        String TOKEN = "asdfg435cdghs79846h741asdfg435cdg";

        setTokenPreferenciaAPI(TOKEN);

        if (getPreferenciaAPI().getUrl().isEmpty()) {
            getUrlAPI();
        }

        /************ PERMISOS **************/
 /*       Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {*//* ... *//*}

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {*//* ... *//*}

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {*//* ... *//*}
                }).check();*/

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.SYSTEM_ALERT_WINDOW
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();


<<<<<<< HEAD:Timelapse/app/src/main/java/salesianostriana/timelapse/activities/MainActivity.java
        // Leemos preferencias para ver si está ya vinculado y lo guardamos en variable para usarla
        SharedPreferences sharedPreferences = getSharedPreferences(Constantes.PREFERENCIAS_API, MODE_PRIVATE);
        estaVinculado = sharedPreferences.getBoolean(Constantes.PREF_PROYECTO_VINCULADO, false);

        // Si está ya vinculado, se salta el paso de la vinculación mediante el scanner QR
        // y pasa directamente al activity donde se muestran los datos que se van recogiendo
        if (estaVinculado) {
            Intent intent = new Intent(MainActivity.this, TimelapseActivity.class);
            startActivity(intent);
            this.finish();
        }
=======
        estadoServicio = (TextView) findViewById(R.id.estado_servicio);

        estadoServicio.setText("Servicio No Activo");
        estadoServicio.setTextColor(getResources().getColor(android.R.color.holo_red_light));
>>>>>>> d5f6d3cbfcbbffe706cf45ac0299d8d0eeb67c62:Camara2/app/src/main/java/salesianostriana/timelapse/Activities/MainActivity.java

        /********** CLICK boton lanzar servicio ***************/
        findViewById(R.id.btn_using_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Iniciando servicio...", Toast.LENGTH_SHORT).show();
                startService(new Intent(MainActivity.this, DemoCamService.class));
                if (isMyServiceRunning(DemoCamService.class)) {
                    estadoServicio.setText("Servicio Activo");
                    estadoServicio.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                } else {
                    estadoServicio.setText("Servicio No Activo");
                    estadoServicio.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                }

            }
        });

    }

    /*Consulta con retrofit para conseguir la URL de la API del proyecto donde hay subir las información de la foto*/
    public void getUrlAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ITrianaSatAPI.ENDPOINT_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ITrianaSatAPI service =
                retrofit.create(ITrianaSatAPI.class);

        Call<ListProyectos> call = service.obtenerProyecto(getPreferenciaAPI().getToken());
        call.enqueue(new Callback<ListProyectos>() {
            @Override
            public void onResponse(Call<ListProyectos> call,
                                   Response<ListProyectos> response) {

                if (response.isSuccessful()) {
                    String href = response.body().getEmbedded().getProyectos().get(0).getLinks().getSelf().getHref();

                    setURLPreferenciaAPI(href);
                } else {
                    Log.i(TAG, "Error Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ListProyectos> call, Throwable t) {
                Log.e(TAG, "Error onfailure: " + t.getMessage());
            }
        });
    }

    /*Consulta el token y la url del proyecto de la API en preferencias*/
    public PreferenciaAPI getPreferenciaAPI() {
        PreferenciaAPI preferencia;

        SharedPreferences prefs =
                getSharedPreferences("PreferenciasAPI", Context.MODE_PRIVATE);

        String token = prefs.getString("token", "");
        String url = prefs.getString("url", "");

        preferencia = new PreferenciaAPI(token, url);

        return preferencia;
    }
<<<<<<< HEAD:Timelapse/app/src/main/java/salesianostriana/timelapse/activities/MainActivity.java
=======

    /**
     * Guarda la url del proyecto de la API en preferencias
     *
     * @param url
     */
    public void setURLPreferenciaAPI(String url) {
        SharedPreferences prefs =
                getSharedPreferences("PreferenciasAPI", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("url", url);
        editor.commit();
    }

    /**
     * Guarda el token del proyecto de la API en preferencias
     *
     * @param token
     */
    public void setTokenPreferenciaAPI(String token) {
        SharedPreferences prefs =
                getSharedPreferences("PreferenciasAPI", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("token", token);
        editor.commit();
    }

    /************************************/
    /************* MENÚ *****************/
    /************************************/
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings: {
                Intent i = new Intent(this, PreferencesActivity.class);
                startActivity(i);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //muestraPreferencias();//Actualiza vista activity_main
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


>>>>>>> d5f6d3cbfcbbffe706cf45ac0299d8d0eeb67c62:Camara2/app/src/main/java/salesianostriana/timelapse/Activities/MainActivity.java
}
