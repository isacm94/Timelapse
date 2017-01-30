package firebase.palma.com.batterylevelchecker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    /*Hay que implementar elegir la frecuencia con que la cámara echa fotos
    según el nivrel de batería, que esta opción se pueda configurar.
    También que se pueda cambiar la calidad de las fotos según vaya quedando menos memoria

    Hay que configurar: frecuencia de las fotos(respecto a batería)
    y calidad(respecto a memoria)
     */


    TextView textView_space;
    TextView batteryTxt;

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            batteryTxt.setText(String.valueOf(level) + " %");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Para comprobar el nivel de batería del dispositivo
        batteryTxt = (TextView) this.findViewById(R.id.textView_battery_level);
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        //Comprobación de la batería


        //Comprobar el espacio disponible en la tarjeta de memoria y en el teléfono
        StatFs stat_fs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double avail_sd_space = (double) stat_fs.getAvailableBlocks() * (double) stat_fs.getBlockSize();
        double GB_Available = (avail_sd_space / 1073741824);

        //double MB_available = (GB_Available/1048576);


        String numberFormat = String.format("%.4f", GB_Available);

        textView_space = (TextView) findViewById(R.id.textView_available_space);
        textView_space.setText(numberFormat + " bytes libres");


    }
}
