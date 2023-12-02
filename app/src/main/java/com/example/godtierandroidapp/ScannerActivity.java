package com.example.godtierandroidapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.CameraController;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.concurrent.ExecutionException;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class ScannerActivity extends AppCompatActivity implements
        EasyPermissions.PermissionCallbacks, View.OnClickListener{

    ImageView scan_view;
    Button scan_desc_btn, scan_serial_btn;
    BarcodeScannerOptions options;
    BarcodeScanner scanner;
    ProcessCameraProvider camera_process;
    PreviewView camera_preview;
    CameraController camera_controller;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private Uri imageUri;
    boolean edit;
    String date, description, make, model, serialNo, estValue, comment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        this.date = getIntent().getStringExtra("dateOfAcquisition");
        this.description = getIntent().getStringExtra("description");
        this.make = getIntent().getStringExtra("make");
        this.model = getIntent().getStringExtra("model");
        this.serialNo = getIntent().getStringExtra("serialNumber");
        this.estValue = getIntent().getStringExtra("estimatedValue");
        this.comment = getIntent().getStringExtra("comment");
        this.edit = getIntent().getBooleanExtra("Edit", false);

        scan_view = findViewById(R.id.scan_view);
        scan_desc_btn = findViewById(R.id.scan_desc_button);
        scan_serial_btn = findViewById(R.id.scan_serial_button);
        camera_preview = findViewById(R.id.barcodeView);

//        options = new BarcodeScannerOptions.Builder()
//                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
//                .build();
//        scanner = BarcodeScanning.getClient(options);

        scan_serial_btn.setOnClickListener(this);
        scan_desc_btn.setOnClickListener(this);
//        scan_serial_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                startCameraForScanType(true);
//            }
//        });
//
//        scan_desc_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
////                startCameraForScanType(false);
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        int vID = v.getId();
        if (vID == R.id.scan_serial_button) {
            startCamera();
            String old = this.serialNo;
            scanSerialNo();
            if (old != serialNo) {
                Intent retIntent = new Intent();
                retIntent.putExtra("New Serial No", serialNo);
                setResult(Activity.RESULT_OK, retIntent);
                finish();
            } else {
                finish();
            }
        }
        else if (vID == R.id.scan_desc_button) {
            startCamera();
            String old_desc = this.description;
            String old_make = this.make;
            String old_model = this.model;
            scanItemInfo();
            if (old_model != model || old_make != make || old_desc != description) {
                Intent retIntent = new Intent();
                retIntent.putExtra("New Make", make);
                retIntent.putExtra("New Model", model);
                retIntent.putExtra("New Description", description);
                setResult(Activity.RESULT_OK, retIntent);
                finish();
            } else {
                finish();
            }
        }
    }

    public void startCamera() {
        String camera_permission = Manifest.permission.CAMERA;

        if (EasyPermissions.hasPermissions(this, camera_permission)) {

            camera_preview.setVisibility(View.VISIBLE);

            ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture = ProcessCameraProvider.getInstance(this);
            cameraProviderListenableFuture.addListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        camera_process = cameraProviderListenableFuture.get();
                        selectCamera(camera_process);
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, ContextCompat.getMainExecutor(this));

        }  else {
            EasyPermissions.requestPermissions(this, "This feature requires access to camera", CAMERA_REQUEST_CODE, camera_permission);
        }
    }
    private void selectCamera(ProcessCameraProvider cameraProvider) {

        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(camera_preview.getSurfaceProvider());

        ImageCapture imageCapture = new ImageCapture.Builder().build();

        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                        .build();
        BarcodeScanner scanner = BarcodeScanning.getClient(options);

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                processBarcode(imageProxy);
            }
        });

        try {
            cameraProvider.unbindAll();
            Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalysis);
            Log.d("CameraStatus", "Camera opened successfully: " + camera);
//            if (!isDestroyed())
//                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalysis);

        } catch (Exception e) {
            Log.e("CameraStatus", "Error opening camera: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 2 methods scanProductInfo and scanSerialNo
    private void scanSerialNo() {
        if (camera_process != null) {
            ImageCapture imageCapture = new ImageCapture.Builder().build();
            imageCapture.takePicture(ContextCompat.getMainExecutor(this), new ImageCapture.OnImageCapturedCallback() {
                @Override
                public void onCaptureSuccess(@NonNull ImageProxy image) {
                    processBarcode(image);
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    exception.printStackTrace();
                }
            });
        }
    }

    private void scanItemInfo()  {
        if (camera_process != null) {
            ImageCapture imageCapture = new ImageCapture.Builder().build();
            imageCapture.takePicture(ContextCompat.getMainExecutor(this), new ImageCapture.OnImageCapturedCallback() {
                @Override
                public void onCaptureSuccess(@NonNull ImageProxy image) {
                    processBarcode(image);
                }
                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    exception.printStackTrace();
                }
            });
        }
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void processBarcode(ImageProxy image) {
        if (image.getImage() != null) {
            InputImage inputImage = InputImage.fromMediaImage(image.getImage(), image.getImageInfo().getRotationDegrees());

            scanner.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                        @Override
                        public void onSuccess(List<Barcode> barcodes) {
                            if (!barcodes.isEmpty()) {
                                Barcode scannedBarcode = barcodes.get(0);
                                String barcodeValue = scannedBarcode.getRawValue();

                                // Handle scanned barcode

                                if (barcodeValue != null && !barcodeValue.isEmpty()) {
                                    Intent retIntent = new Intent();
                                    // here figure out sending back the new info
                                    finish();
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Failed to scan barcode",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<List<Barcode>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<Barcode>> task) {
                            image.close();
                        }
                    });
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (camera_process != null) {
            camera_process.unbindAll();
            camera_process = null;
        }
    }
    /**
     *
     * @param requestCode The request code passed in {@link #requestPermissions(
     * android.app.Activity, String[], int)}
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @org.checkerframework.checker.nullness.qual.NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> permissions) {}
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> permissions) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this, permissions)){
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

}
