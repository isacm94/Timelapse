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

package rogeliorb.camara2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidhiddencamera.HiddenCameraFragment;

public class MainActivity extends AppCompatActivity {

    private HiddenCameraFragment mHiddenCameraFragment;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyStoragePermissions(MainActivity.this);
        //checkPermiso();

        /*ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
        );*/


        findViewById(R.id.btn_using_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(MainActivity.this, DemoCamService.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mHiddenCameraFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(mHiddenCameraFragment).commit();
            mHiddenCameraFragment = null;
        }
    }


    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(activity, "No tiene permiso", Toast.LENGTH_SHORT).show();
            Log.i("TIMELAPSE", "No tiene permiso");

            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );

            Log.i("TIMELAPSE", "Solicitando permiso");
        } else {
            Toast.makeText(activity, "Tiene permiso", Toast.LENGTH_SHORT).show();
            Log.i("TIMELAPSE", "Tiene permiso");
        }
    }


    public void checkPermiso() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user asynchronously -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                //showExplanation();
                //mDialogHandler.sendEmptyMessage(100);

                Log.i("TIMELAPSE", "Aqui");

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_EXTERNAL_STORAGE);

                // REQUEST_READWRITE_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.

                Log.i("TIMELAPSE", "alli");
            }
        } else {
            Log.i("TIMELAPSE", "Tiene permiso");
        }
    }

    private static void requestPermission(final Context context) {

        final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.

            new AlertDialog.Builder(context)
                    .setMessage("¿Desear dar permiso para la tarjeta sd?")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_WRITE_EXTERNAL_STORAGE);
                        }
                    }).show();

            Log.i("TIMELAPSE", "Diálogo");
        } else {
            // permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);

            Log.i("TIMELAPSE", "ha solicitado permiso");
        }
    }

}
