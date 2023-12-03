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
import java.util.Date;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

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
                        int valueType = barcode.getValueType();

                        switch (valueType) {
                            case Barcode.TYPE_ISBN:

                                String sno = barcode.getDisplayValue();
                                Log.d(TAG, "Barcode value: " + sno);
                                Intent snoIntent = new Intent();
                                snoIntent.putExtra("serial number", sno);
                                snoIntent.putExtra("edit serial number", true);
                                setResult(Activity.RESULT_OK, snoIntent);
                                finish();
                                break;

                            case Barcode.TYPE_URL:

//                                String productBarcodeData = barcode.getDisplayValue();
                                String url = barcode.getUrl().getUrl();
                                Log.d(TAG, "URL: " + url);
                                new ParseTextFileTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);

//                                String productBarcodeData = parseTextFile(url);
//                                Log.d(TAG, "Barcode value: " + productBarcodeData);
//                                ProductData productData = parseProductBarcode(productBarcodeData);

//                                if (productData != null) {
//                                    Intent infoIntent = new Intent();
//                                    infoIntent.putExtra("edit info", true);
//
//                                    if (!productData.descEmpty()) {
//                                        String new_description = productData.getDescription();
//                                        infoIntent.putExtra("edit description", true);
//                                        infoIntent.putExtra("description", new_description);
//                                    }
//
//                                    if (!productData.makeEmpty()) {
//                                        String new_make = productData.getMake();
//                                        infoIntent.putExtra("edit make", true);
//                                        infoIntent.putExtra("make", new_make);
//                                    }
//
//                                    if (!productData.modelEmpty()) {
//                                        String new_model = productData.getModel();
//                                        infoIntent.putExtra("edit model", true);
//                                        infoIntent.putExtra("model", new_model);
//                                    }
//
//                                    setResult(Activity.RESULT_OK, infoIntent);
//                                }
//
//                                finish();
                                break;

                            default:
                                Log.d(TAG, "Barcode not supported");
                                Toast.makeText(ScannerActivity.this, "Barcode not supported", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Barcode scanning failed", e));
    }

    private static boolean isTextFile(String url) {
        return url.toLowerCase().endsWith(".txt");
    }

    private class ParseTextFileTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return parseTextFile(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            // Process the result on the main thread
            handleParsedTextFileResult(result);
        }
    }

    private void handleParsedTextFileResult(String result) {

        if (result != null) {
            ProductData productData = parseProductBarcode(result);

            if (productData != null) {
                Intent infoIntent = new Intent();
                infoIntent.putExtra("edit info", true);

                if (!productData.descEmpty()) {
                    String new_description = productData.getDescription();
                    infoIntent.putExtra("edit description", true);
                    infoIntent.putExtra("description", new_description);
                }

                if (!productData.makeEmpty()) {
                    String new_make = productData.getMake();
                    infoIntent.putExtra("edit make", true);
                    infoIntent.putExtra("make", new_make);
                }

                if (!productData.modelEmpty()) {
                    String new_model = productData.getModel();
                    infoIntent.putExtra("edit model", true);
                    infoIntent.putExtra("model", new_model);
                }

                setResult(Activity.RESULT_OK, infoIntent);
            }
            finish();
            }
        }


    private static String parseTextFile(String url) {
        try {
            URL textFileUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) textFileUrl.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                reader.close();
                return stringBuilder.toString();
            } else {
                System.out.println("Failed to fetch text file data. HTTP response code: " + connection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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