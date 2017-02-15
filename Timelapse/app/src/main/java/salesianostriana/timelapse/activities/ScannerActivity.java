package salesianostriana.timelapse.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import salesianostriana.timelapse.Constantes;
import salesianostriana.timelapse.Interfaces.ITrianaSatAPI;
import salesianostriana.timelapse.pojos.hrefAPI.ListProyectos;
import salesianostriana.timelapse.R;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView scannerView;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);   // Inicializa programáticamente el scannerView
        setContentView(scannerView);                // Pone el scannerView como el contentView
    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this); // Register ourselves as a handler for scan results
        scannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();           // Stop camera on pause
    }

    // Manejador de lo que hay que hacer cuando se obtiene un resultado tras escanear
    @Override
    public void handleResult(Result rawResult) {
        String tokenProyecto = rawResult.getText();

        // Log para debug
        Log.v(Constantes.PREF_TOKEN_PROYECTO, tokenProyecto);

        // Si no es un código QR
        if (rawResult.getBarcodeFormat() != BarcodeFormat.QR_CODE) {
            // Esta línea sirve para continuar con la cámara, si no, se bloquearía
            scannerView.resumeCameraPreview(this);
            Toast.makeText(this, "Enfoque un código QR", Toast.LENGTH_SHORT).show();
        } else {

            // Guardamos token en preferencias
            SharedPreferences sharedPreferences = getSharedPreferences(Constantes.PREFERENCIAS_API, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constantes.PREF_TOKEN_PROYECTO, tokenProyecto);
            editor.commit();

            // Llamar al método para hacer la petición GET mediante Retrofit
            peticionTokenProyecto(tokenProyecto);
        }
    }

    // Método para hacer petición para obtener el JSON del proyecto a través del token
    private void peticionTokenProyecto(final String tokenProyecto) {
        progressDialog = new ProgressDialog(ScannerActivity.this);
        progressDialog.setMessage("Vinculando proyecto...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ITrianaSatAPI.ENDPOINT_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ITrianaSatAPI service = retrofit.create(ITrianaSatAPI.class);

        Call<ListProyectos> call = service.obtenerProyecto(tokenProyecto);

        call.enqueue(new Callback<ListProyectos>() {
                         @Override
                         public void onResponse(Call<ListProyectos> call, Response<ListProyectos> response) {
                             try {
                                 String hrefProyecto = response.body().getEmbedded().getProyectos().get(0).getLinks().getProyecto().getHref();
                                 String nombreProyecto = response.body().getEmbedded().getProyectos().get(0).getNombre();

                                 // Guardamos en preferencias el HREF del proyecto
                                 SharedPreferences sharedPreferencesHref = getSharedPreferences(Constantes.PREFERENCIAS_API, MODE_PRIVATE);
                                 SharedPreferences.Editor editor = sharedPreferencesHref.edit();
                                 editor.putString(Constantes.PREF_HREF_PROYECTO, hrefProyecto);
                                 editor.putBoolean(Constantes.PREF_PROYECTO_VINCULADO, true);
                                 editor.putString(Constantes.PREF_NOMBRE_PROYECTO, nombreProyecto);
                                 editor.commit();

                                 // Intent a DatosActivity
                                 Intent intent = new Intent(ScannerActivity.this, TimelapseActivity.class);
                                 startActivity(intent);
                                 ScannerActivity.this.finish();

                             } catch (IndexOutOfBoundsException e) {
                                 mostrarDialogo();
                             } catch (NullPointerException e) {
                                 Toast.makeText(ScannerActivity.this, "Imposible conectar con el servidor", Toast.LENGTH_SHORT).show();
                                 scannerView.resumeCameraPreview(ScannerActivity.this);
                             } finally {
                                 progressDialog.dismiss();
                             }

                         }

                         @Override
                         public void onFailure(Call<ListProyectos> call, Throwable t) {
                             Toast.makeText(ScannerActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                             scannerView.resumeCameraPreview(ScannerActivity.this);
                             progressDialog.dismiss();
                         }

                     }
        );
    }

    public void mostrarDialogo() {
        builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.tiene_codigo_valido));
        builder.setMessage(getString(R.string.dar_alta_proyecto));
        builder.setCancelable(false);

        builder.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scannerView.resumeCameraPreview(ScannerActivity.this);
                    }
                });

        builder.create();
        builder.show();
    }
}