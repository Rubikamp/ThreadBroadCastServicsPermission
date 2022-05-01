package info.sanaebadi.threadandservices;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ImageView imageResult;
    private Button buttonDownload;
    ProgressDialog progressDialog;
    URL url = null;
    InputStream inputStream = null;
    Bitmap bitmap = null;

    private MyReceiver myReceiver;
    private Button buttonsMSPermission;
    private final int SMS_REQUEST_CODE = 100;
    private Button buttonStartService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageResult = findViewById(R.id.imageview_result);
        buttonDownload = findViewById(R.id.button_download);
        buttonsMSPermission = findViewById(R.id.button_permission);
        buttonStartService = findViewById(R.id.button_service);

        buttonDownload.setOnClickListener(view -> {
            AsyncTaskExample asyncTaskExample = new AsyncTaskExample();
            asyncTaskExample.execute("https://www.tutorialspoint.com/images/tp-logo-diamond.png");

        });

        buttonsMSPermission.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestSMSPermission();

            } else {
                Toast.makeText(MainActivity.this, "Permission allowed before.", Toast.LENGTH_SHORT).show();

            }
        });

        buttonStartService.setOnClickListener(view -> {
            Intent serviceIntent=new Intent(MainActivity.this,MyService.class);
            startService(serviceIntent);

        });
    }

    class AsyncTaskExample extends AsyncTask<String, String, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait...It is downloading");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                url = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                inputStream = httpURLConnection.getInputStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                bitmap = BitmapFactory.decodeStream(inputStream, null, options);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (imageResult != null) {
                progressDialog.hide();
                imageResult.setImageBitmap(bitmap);
            } else {
                progressDialog.show();
            }
        }
    }

    private void requestSMSPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.RECEIVE_SMS)) {
            new AlertDialog.Builder(this)
                    .setTitle("sms permission")
                    .setMessage("for read the phone sms, please confrim.")
                    .setPositiveButton("Allow", (dialogInterface, i) -> reqPermission())
                    .setNegativeButton("Deny", (dialogInterface, i) -> dialogInterface.dismiss())
                    .create()
                    .show();

        } else {
            reqPermission();

        }

    }

    private void reqPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.RECEIVE_SMS}, SMS_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission allowed.", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();

            }

        }
    }

    @Override
    protected void onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            myReceiver = new MyReceiver();
            registerReceiver(myReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            super.onResume();
        }
    }

    @Override
    protected void onPause() {
        unregisterReceiver(myReceiver);
        super.onPause();
    }
}