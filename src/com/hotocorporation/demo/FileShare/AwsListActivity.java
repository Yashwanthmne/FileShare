/*
 * Copyright 2015-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.hotocorporation.demo.FileShare;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;

import com.blikoon.qrcodescanner.QrCodeActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/*
 * This is the beginning screen that lets the user select if they want to upload or download
 */
public class AwsListActivity extends Activity {

    private static final String TAG = "AwsListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aws_list);
        initUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");

            Log.d("<YM>", "SCAN RESULT: " + result);
            new DownloadFileFromURL(this, "Test1").execute(result);
        }

    }

    private void initUI() {
        FrameLayout download = findViewById(R.id.buttonDownloadMain);
        FrameLayout upload = findViewById(R.id.buttonUploadMain);
        FrameLayout logout = findViewById(R.id.logout);
        FrameLayout qr     = findViewById(R.id.buttonQR);

        download.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(AwsListActivity.this, DownloadActivity.class);
                startActivity(intent);
            }
        });

        upload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(AwsListActivity.this, UploadActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent logout_intent = new Intent(AwsListActivity.this, MainActivity.class);
                startActivity(logout_intent);
            }
        });

        qr.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                requestWriteExternalStoragePermission();

            }
        });


    }

    private void  requestWriteExternalStoragePermission() {
        //ask for the permission in android M
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to start camera is not granted");

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Permission to access the Camera is required for this application to start the QR Code scanner")
                        .setTitle("Permission required");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(TAG, "Clicked");
                        makeRequest();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                makeRequest();
            }
        }
        else {
            Intent i = new Intent(AwsListActivity.this,QrCodeActivity.class);
            startActivityForResult( i,101);
            Log.i(TAG, "Permission to start camera is granted.");
        }

    }
    protected void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                100);
    }

}
