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

    // Permisos de escritura/ lectura
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //Permisos de camara
    private static final int REQUEST_CAMERA = 2;
    private static String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyStoragePermissions(MainActivity.this);
        verifyCameraPermissions(MainActivity.this);

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
            Log.i("TIMELAPSE", "No tiene permiso de tarjeta SD");
            Toast.makeText(activity, "No tiene permiso de tarjeta SD", Toast.LENGTH_SHORT).show();

            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );

            Log.i("TIMELAPSE", "Solicitando permiso de tarjeta SD");
        } else {
            Log.i("TIMELAPSE", "Tiene permiso de tarjeta SD");
            Toast.makeText(activity, "Tiene permiso de tarjeta SD", Toast.LENGTH_SHORT).show();
        }
    }

    public static void verifyCameraPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("TIMELAPSE", "No tiene permiso de cámara");
            Toast.makeText(activity, "No tiene permiso de cámara", Toast.LENGTH_SHORT).show();

            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_CAMERA,
                    REQUEST_CAMERA
            );

            Log.i("TIMELAPSE", "Solicitando permiso");
        } else {
            Log.i("TIMELAPSE", "Tiene permiso de cámara");
            Toast.makeText(activity, "Tiene permiso de cámara", Toast.LENGTH_SHORT).show();
        }
    }
}
