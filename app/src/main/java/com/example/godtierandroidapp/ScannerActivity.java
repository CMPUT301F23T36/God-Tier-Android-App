package com.example.godtierandroidapp;


import static com.example.godtierandroidapp.ProductBarcodeParser.parseProductBarcode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.common.InputImage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScannerActivity extends AppCompatActivity {

    private static final String TAG = "BarcodeScannerDemo";
    private static final int REQUEST_CODE_PERMISSIONS = 10;

    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};

    private Camera camera;
    private ExecutorService cameraExecutor;
    private PreviewView previewView;
    private ImageView imageView;

    private Button btnCapture;
    private Button btnCancel;
    HashMap<String, ArrayList<String>> barcodeData = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        ArrayList<String> item_data = new ArrayList<>();
        item_data.add("Banana");
        item_data.add("2009");
        item_data.add("Blue Java");
        barcodeData.put("6009832100999", item_data);

        imageView = findViewById(R.id.imageView);
        previewView = findViewById(R.id.previewView);
        btnCapture = findViewById(R.id.btnCapture);
        btnCancel = findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener((View v) -> cancelImageCapture());
        btnCapture.setOnClickListener((View v) -> captureImage());

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            startCamera();
        }

        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private ImageCapture imageCapture;

    private void captureImage() {
        // Create output file to store the captured image
        File outputDirectory = getOutputDirectory();
        File outputFile = new File(outputDirectory, new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(new Date()) + ".jpg");

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(outputFile).build();

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                // Image capture successful, now analyze the captured image
                analyzeCapturedImage(outputFile);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e(TAG, "Error capturing image", exception);
            }
        });
    }

    private void analyzeCapturedImage(File imageFile) {
        // Create an InputImage from the captured image file
        InputImage image;
        try {
            image = InputImage.fromFilePath(this, android.net.Uri.fromFile(imageFile));
        } catch (IOException e) {
            Log.e(TAG, "Error creating InputImage from file", e);
            return;
        }

        // Call the analyze method with the captured image
        analyze(image);
    }

    private void analyze(InputImage image) {
        // Use the provided BarcodeAnalyzer or any other logic to analyze the image
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                        .enableAllPotentialBarcodes()
                        .build();

        BarcodeScanner scanner = BarcodeScanning.getClient(options);

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    for (Barcode barcode : barcodes) {
                        Rect bounds = barcode.getBoundingBox();
                        Point[] corners = barcode.getCornerPoints();

                        String raw = barcode.getRawValue();
                        Log.d(TAG, "Barcode raw value" + raw);

                        // int valueType = barcode.getValueType();

                        String sno = barcode.getDisplayValue();
                        Log.d(TAG, "Barcode value: " + sno);
                        Intent retIntent = new Intent();
                        retIntent.putExtra("serial number", sno);
                        retIntent.putExtra("edit serial number", true);
                        if (barcodeData.containsKey(sno)) {
                            ArrayList<String> item_info = barcodeData.get(sno);
                            if (item_info != null) {
                                retIntent.putExtra("description", item_info.get(0));
                                retIntent.putExtra("make", item_info.get(1));
                                retIntent.putExtra("model", item_info.get(2));
                            }
                        }
                        setResult(Activity.RESULT_OK, retIntent);
                        finish();
                        break;

                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Barcode scanning failed", e));
    }
    
    private File getOutputDirectory() {
        File[] mediaDirs = getExternalMediaDirs();
        if (mediaDirs.length > 0) {
            File mediaDir = new File(mediaDirs[0], "GodTierAndroidApp");
            mediaDir.mkdirs();
            return mediaDir;
        }
        return null;
    }


    private void cancelImageCapture() {
        finish();
    }

    private void startCamera() {
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder().build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, new BarcodeAnalyzer());

        imageCapture = new ImageCapture.Builder()
                .build();

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                cameraProvider.unbindAll();

                camera = cameraProvider.bindToLifecycle(
                        (LifecycleOwner) this,
                        cameraSelector,
                        preview,
                        imageAnalysis,
                        imageCapture // Add imageCapture here
                );
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
            } catch (Exception e) {
                Log.e(TAG, "Error starting camera", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != getPackageManager().PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private class BarcodeAnalyzer implements ImageAnalysis.Analyzer {
        @ExperimentalGetImage
        @Override
        public void analyze(@NonNull ImageProxy imageProxy) {
            Log.d("Analyzing", imageProxy.toString());

            Image mediaImage = imageProxy.getImage();
            if (mediaImage != null) {
                InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

                BarcodeScannerOptions options =
                        new BarcodeScannerOptions.Builder()
                                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                                .build();

                BarcodeScanner scanner = BarcodeScanning.getClient(options);

                scanner.process(image)
                        .addOnSuccessListener(barcodes -> {
                            for (Barcode barcode : barcodes) {
                                String value = barcode.getDisplayValue();
                                Log.d(TAG, "Barcode value: " + value);

                                // Display the barcode value on UI (you can customize this part)
                                runOnUiThread(() -> {
                                    Toast.makeText(ScannerActivity.this, "Barcode: " + value, Toast.LENGTH_SHORT).show();
                                });
                            }
                        })
                        .addOnFailureListener(e -> Log.e(TAG, "Barcode scanning failed", e))
                        .addOnCompleteListener(task -> imageProxy.close());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}