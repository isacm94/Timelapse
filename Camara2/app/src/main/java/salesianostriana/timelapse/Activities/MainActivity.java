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

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import salesianostriana.timelapse.DemoCamService;
import salesianostriana.timelapse.Pojos.Preferencia;
import salesianostriana.timelapse.R;

public class MainActivity extends AppCompatActivity {

    TextView textViewBateria, textViewFrecuencia, textViewMemoria, textViewCalidad,estadoServicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        estadoServicio = (TextView) findViewById(R.id.estado_servicio);
        textViewBateria = (TextView) findViewById(R.id.text_view_bateria);
        textViewFrecuencia = (TextView) findViewById(R.id.text_view_frecuencia);
        textViewMemoria = (TextView) findViewById(R.id.text_view_memoria);
        textViewCalidad = (TextView) findViewById(R.id.text_view_calidad);
        estadoServicio.setText("Servicio No Activo");
        estadoServicio.setTextColor(getResources().getColor(android.R.color.holo_red_light));

        //CLICK boton lanzar servricio
        findViewById(R.id.btn_using_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Iniciando servicio...", Toast.LENGTH_SHORT).show();
                startService(new Intent(MainActivity.this, DemoCamService.class));
                if(isMyServiceRunning(DemoCamService.class)){
                    estadoServicio.setText("Servicio Activo");
                    estadoServicio.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                }else{
                    estadoServicio.setText("Servicio No Activo");
                    estadoServicio.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                }

            }
        });

        //Muestra las preferencias guardadas
        //muestraPreferencias();
    }

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
/*
    public void muestraPreferencias(){
        Preferencia preferencia = getPreferencia();

        textViewBateria.setText(getString(R.string.title_bateria) + ": " + preferencia.getBateria());
        textViewFrecuencia.setText(getString(R.string.title_frecuencia) + ": " + preferencia.getFrecuencia());
        textViewMemoria.setText(getString(R.string.title_memoria) + ": " + preferencia.getMemoria());
        textViewCalidad.setText(getString(R.string.title_calidad) + ": " + preferencia.getCalidad());
    }
*/
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
}
