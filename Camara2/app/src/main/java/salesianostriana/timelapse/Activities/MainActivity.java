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
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import salesianostriana.timelapse.DemoCamService;
import salesianostriana.timelapse.FotosDB;
import salesianostriana.timelapse.Pojos.Foto;
import salesianostriana.timelapse.R;

public class MainActivity extends AppCompatActivity {

    TextView estadoServicio;
    FotosDB fotosDB;
    String TAG = "Fotos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        estadoServicio = (TextView) findViewById(R.id.estado_servicio);

        estadoServicio.setText("Servicio No Activo");
        estadoServicio.setTextColor(getResources().getColor(android.R.color.holo_red_light));

        //CLICK boton lanzar servricio
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

        /*BASE DE DATOS LOCAL*/
        fotosDB = new FotosDB(this);//Inicializa base de datos

        insertFoto(new Foto("probando.png", 12344L, 60.0, 1));
        insertFoto(new Foto("probando2.png", 23452345L, 40.0, 0));
        insertFoto(new Foto("probando3.png", 3452345L, 30.0, 1));
        insertFoto(new Foto("probando4.png", 244352L, 20.0, 0));
        insertFoto(new Foto("probando5.png", 345L, 40.0, 1));

        List<Foto> fotosSubidas = getFotosSubidas();
        List<Foto> fotosNoSubidas = getFotosNoSubidas();

        updateFoto(fotosNoSubidas.get(1).getId(), 1);

        getFotosSubidas();
        getFotosNoSubidas();

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
}
