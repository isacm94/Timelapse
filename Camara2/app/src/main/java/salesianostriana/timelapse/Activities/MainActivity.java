package salesianostriana.timelapse.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import salesianostriana.timelapse.Constantes;
import salesianostriana.timelapse.Pojos.hrefAPI.Timelapse;
import salesianostriana.timelapse.R;

public class MainActivity extends AppCompatActivity {
    private Button buttonVincularProyecto;
    private boolean estaVinculado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /************ PERMISOS **************/
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {/* ... */}

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                }).check();

        buttonVincularProyecto = (Button) findViewById(R.id.button_vincular_proyecto);

        // Leemos preferencias para ver si está ya vinculado y lo guardamos en variable para usarla
        SharedPreferences sharedPreferences = getSharedPreferences(Constantes.PREF_PROYECTO_VINCULADO, MODE_PRIVATE);
        estaVinculado = sharedPreferences.getBoolean(Constantes.PREF_PROYECTO_VINCULADO, false);

        // Si está ya vinculado, se salta el paso de la vinculación mediante el scanner QR
        // y pasa directamente al activity donde se muestran los datos que se van recogiendo
        if (estaVinculado) {
            setTitle("Timelapse "+getNombreProyecto());
            Intent intent = new Intent(MainActivity.this, TimelapseActivity.class);
            startActivity(intent);
            this.finish();
        }

        // Si no, esperamos que se pulse el botón para llevar al usuario al scanner QR
        // donde se vinculará el proyecto
        buttonVincularProyecto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
                startActivity(intent);
            }
        });
    }

    public String getNombreProyecto() {
        SharedPreferences prefs =
                getSharedPreferences(Constantes.PREFERENCIAS_API, Context.MODE_PRIVATE);

        String nombre = prefs.getString(Constantes.PREF_NOMBRE_PROYECTO, "");

        return nombre;
    }
}
