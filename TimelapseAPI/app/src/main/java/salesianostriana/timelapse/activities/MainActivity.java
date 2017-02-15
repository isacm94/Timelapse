package salesianostriana.timelapse.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import salesianostriana.timelapse.Constantes;
import salesianostriana.timelapse.R;

/**
 * Activity en el que se comprueba si está vinculado el proyecto.
 * Si lo está lanza directamente el TimelapseActivity sino el ScannerActivity para coger el código QR y vincularlo
 */
public class MainActivity extends AppCompatActivity {
    private Button buttonVincularProyecto;
    private boolean estaVinculado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

        buttonVincularProyecto = (Button) findViewById(R.id.button_vincular_proyecto);

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
}
