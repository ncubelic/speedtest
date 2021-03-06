package com.lioncode.speed;

import android.Manifest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.lioncode.speed.com.lioncode.speed.service.CpuUsageTask;
import com.lioncode.speed.com.lioncode.speed.service.PingService;
import com.lioncode.speed.com.lioncode.speed.service.SpeedTestService;
import com.lioncode.speed.com.lioncode.speed.service.WifiInfoTask;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 2;
    private Button downloadBtn;
    private Button uploadBtn;
    private Button uploadDownloadBtn;

    private TextView pingLabel;

    private boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_speedtest);

        downloadBtn = (Button) findViewById(R.id.button_dl);
        uploadBtn = (Button) findViewById(R.id.button_ul);
        uploadDownloadBtn = (Button) findViewById(R.id.button_ul_dl);

        pingLabel = (TextView) findViewById(R.id.trip_time);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access to use this app.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }else{
            // Find Wifi info
            WifiInfoTask wifiInfoTask = new WifiInfoTask(this);
            wifiInfoTask.execute();

            // Start cpu usage handler
            CpuUsageTask cpuUsageTask = new CpuUsageTask(this);
            cpuUsageTask.start();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Find Wifi info
                    WifiInfoTask wifiInfoTask = new WifiInfoTask(this);
                    wifiInfoTask.execute();

                    // Start cpu usage handler
                    CpuUsageTask cpuUsageTask = new CpuUsageTask(this);
                    cpuUsageTask.start();

                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to use wifi scan in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
            }
        }
    }


    public void runDownloadTest(View view) {
        if (connected) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    downloadBtn.setEnabled(false);
                    uploadBtn.setEnabled(false);
                    uploadDownloadBtn.setEnabled(false);
                }
            });

            PingService pingService = new PingService();
            final String rtt = pingService.ping();
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pingLabel.setText(rtt);
                }
            });

            SpeedTestService speedTestService = new SpeedTestService(this);
            speedTestService.startDownload();
        }
    }

    public void runUploadTest(View view) {
        if (connected) {
            downloadBtn.setEnabled(false);
            uploadBtn.setEnabled(false);
            uploadDownloadBtn.setEnabled(false);

            PingService pingService = new PingService();
            final String rtt = pingService.ping();
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pingLabel.setText(rtt);
                }
            });

            SpeedTestService speedTestService = new SpeedTestService(this);
            speedTestService.startUpload();
        }
    }

    public void runUploadDownloadTest(View view) {
        if (connected) {
            downloadBtn.setEnabled(false);
            uploadBtn.setEnabled(false);
            uploadDownloadBtn.setEnabled(false);

            PingService pingService = new PingService();
            final String rtt = pingService.ping();
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pingLabel.setText(rtt);
                }
            });

            SpeedTestService speedTestService = new SpeedTestService(this);
            speedTestService.startUploadAndDownload();
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
