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
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import salesianostriana.timelapse.TimelapseService;
import salesianostriana.timelapse.R;

public class TimelapseActivity extends AppCompatActivity {

    TextView estadoServicio;
    String TAG = "Fotos";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timelapse);

        /******* PREFERENCIAS API ***********/
        //String TOKEN = "asdfg435cdghs79846h741asdfg435cdg";

        //setTokenPreferenciaAPI(TOKEN);

        //if (getHrefProyecto().getUrl().isEmpty()) {
           // getUrlAPI();
        //}


        estadoServicio = (TextView) findViewById(R.id.estado_servicio);

        estadoServicio.setText("Servicio No Activo");
        estadoServicio.setTextColor(getResources().getColor(android.R.color.holo_red_light));

        /********** CLICK boton lanzar servicio ***************/
        findViewById(R.id.btn_using_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TimelapseActivity.this, "Iniciando servicio...", Toast.LENGTH_SHORT).show();
                startService(new Intent(TimelapseActivity.this, TimelapseService.class));
                if (isMyServiceRunning(TimelapseService.class)) {
                    estadoServicio.setText("Servicio Activo");
                    estadoServicio.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                } else {
                    estadoServicio.setText("Servicio No Activo");
                    estadoServicio.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                }

            }
        });

    }

    /*Consulta con retrofit para conseguir la URL de la API del proyecto donde hay subir las información de la foto*//*
    public void getUrlAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ITrianaSatAPI.ENDPOINT_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ITrianaSatAPI service =
                retrofit.create(ITrianaSatAPI.class);

        Call<ListProyectos> call = service.obtenerProyecto(getHrefProyecto().getToken());
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

    *//*Consulta el token y la url del proyecto de la API en preferencias*//*
    public PreferenciaAPI getHrefProyecto() {
        PreferenciaAPI preferencia;

        SharedPreferences prefs =
                getSharedPreferences("PreferenciasAPI", Context.MODE_PRIVATE);

        String token = prefs.getString("token", "");
        String url = prefs.getString("url", "");

        preferencia = new PreferenciaAPI(token, url);

        return preferencia;
    }

    *//**
     * Guarda la url del proyecto de la API en preferencias
     *
     * @param url
     *//*
    public void setURLPreferenciaAPI(String url) {
        SharedPreferences prefs =
                getSharedPreferences("PreferenciasAPI", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("url", url);
        editor.commit();
    }

    *//**
     * Guarda el token del proyecto de la API en preferencias
     *
     * @param token
     *//*
    public void setTokenPreferenciaAPI(String token) {
        SharedPreferences prefs =
                getSharedPreferences("PreferenciasAPI", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("token", token);
        editor.commit();
    }*/

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

        //muestraPreferencias();//Actualiza vista activity_timelapse
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


}
