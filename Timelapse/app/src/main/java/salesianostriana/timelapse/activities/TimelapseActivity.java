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

package salesianostriana.timelapse.activities;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import salesianostriana.timelapse.Constantes;
import salesianostriana.timelapse.FotosDatabase;
import salesianostriana.timelapse.R;
import salesianostriana.timelapse.TimelapseService;

import static salesianostriana.timelapse.Constantes.PREFERENCIAS_API;

public class TimelapseActivity extends AppCompatActivity {

    TextView estadoServicio;
    String TAG = "Fotos";
    FotosDatabase fotosDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timelapse);

        setTitle("Timelapse " + getNombreProyecto());

        fotosDatabase = new FotosDatabase(this);


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_preferencias: {
                Intent i = new Intent(this, PreferencesActivity.class);
                startActivity(i);
                return true;
            }
            case R.id.action_desvincular: {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(this);

                builder.setTitle(getString(R.string.action_desvincular_proyecto));
                builder.setMessage(getString(R.string.desvincular_mensaje));
                builder.setCancelable(false);

                builder.setPositiveButton(getString(R.string.aceptar),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                resetAplicacion();
                                finish();
                                Intent intent = new Intent(TimelapseActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });


                builder.setNegativeButton(getString(R.string.cancelar),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builder.create();
                builder.show();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void resetAplicacion() {
        //Elimina preferencias API
        SharedPreferences sharedPreferencesApi = getSharedPreferences(PREFERENCIAS_API, MODE_PRIVATE);
        SharedPreferences.Editor editorApi = sharedPreferencesApi.edit();
        editorApi.clear();
        editorApi.commit();

        //Elimina preferencias del PreferencesActivity
        SharedPreferences sharedPreferencesActivity = getSharedPreferences(PREFERENCIAS_API, MODE_PRIVATE);
        SharedPreferences.Editor editorPreferencesActivity = sharedPreferencesActivity.edit();
        editorPreferencesActivity.clear();
        editorPreferencesActivity.commit();

        //Vacía BD
        fotosDatabase.openWrite();
        fotosDatabase.deleteAll();
        fotosDatabase.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    public String getNombreProyecto() {
        SharedPreferences prefs =
                getSharedPreferences(Constantes.PREFERENCIAS_API, Context.MODE_PRIVATE);

        return prefs.getString(Constantes.PREF_NOMBRE_PROYECTO, "");
    }
}
