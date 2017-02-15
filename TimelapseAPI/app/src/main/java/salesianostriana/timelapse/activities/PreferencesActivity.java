package salesianostriana.timelapse.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import salesianostriana.timelapse.PreferenciasFragment;

/**
 * Created by jarmada on 24/01/2017.
 */

/**
 * Activity encargado de lanzar las preferencias de la aplicación
 */
public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferenciasFragment())
                .commit();
        SharedPreferences shaPref = PreferenceManager.getDefaultSharedPreferences(this);
    }


}
