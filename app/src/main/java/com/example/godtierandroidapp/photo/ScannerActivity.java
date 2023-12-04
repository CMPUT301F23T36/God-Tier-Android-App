package com.example.godtierandroidapp.photo;

import androidx.annotation.NonNull;
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

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.godtierandroidapp.R;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.common.InputImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Allows the user to scan a barcode to generate a serial number.
 *
 * @author Vinayan, Alex
 */
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

    /**
     * takes a photo with imageCapture and starts analysis.
     */
    private void captureImage() {
        File outputDirectory = getOutputDirectory();
        File outputFile = new File(outputDirectory, new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(new Date()) + ".jpg");

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(outputFile).build();

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                analyzeCapturedImage(outputFile);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e(TAG, "Error capturing image", exception);
            }
        });
    }

    /**
     * Analyze a given image file.
     * @param imageFile the file to analyze
     */
    private void analyzeCapturedImage(File imageFile) {
        InputImage image;
        try {
            image = InputImage.fromFilePath(this, android.net.Uri.fromFile(imageFile));
        } catch (IOException e) {
            Log.e(TAG, "Error creating InputImage from file", e);
            return;
        }

        analyze(image);
    }

    /**
     * Analyze a given image, returning from the activity on success.
     * @param image the image to analyze
     */
    private void analyze(InputImage image) {
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                        .enableAllPotentialBarcodes()
                        .build();

        BarcodeScanner scanner = BarcodeScanning.getClient(options);

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    for (Barcode barcode : barcodes) {
                        String raw = barcode.getRawValue();
                        if (raw != null && !raw.equals("")) {
                            Log.d(TAG, "Barcode raw value" + raw);
                            String sno = barcode.getDisplayValue();
                            Log.d(TAG, "Barcode value: " + sno);
                            Intent retIntent = new Intent();
                            retIntent.putExtra("serial number", sno);
                            setResult(Activity.RESULT_OK, retIntent);
                            finish();
                            break;
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ScannerActivity.this, "Unable to find barcode. Try again.", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * @return The directory where images should be saved
     */
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

    /**
     * Initializes the camera.
     */
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

    /**
     * checks if all permissions are granted.
     */
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