package salesianostriana.timelapse;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by jarmada on 24/01/2017.
 */

public class PreferencesActivity extends AppCompatActivity {

    String bat, cal, memo, fre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferenciasFragment())
                .commit();
        SharedPreferences shaPref = PreferenceManager.getDefaultSharedPreferences(this);
    }


}
