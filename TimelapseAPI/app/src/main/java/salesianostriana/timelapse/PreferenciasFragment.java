package salesianostriana.timelapse;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by jarmada on 24/01/2017.
 */

public class PreferenciasFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
    }
}
