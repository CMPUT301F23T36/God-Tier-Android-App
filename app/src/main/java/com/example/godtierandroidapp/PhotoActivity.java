package com.example.godtierandroidapp;

import androidx.activity.result.ActivityResult;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;

import android.app.Activity;
import android.content.Context;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import android.view.View;

import androidx.camera.view.PreviewView;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import androidx.camera.lifecycle.ProcessCameraProvider;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.firestore.admin.v1.Index;

public class PhotoActivity extends AppCompatActivity implements
        PhotoFragment.OnFragmentInteractionListener,
        EasyPermissions.PermissionCallbacks, View.OnClickListener {

    final static int max_array_size = 4;
    ArrayList<ImageView> album;
    ArrayList<Uri> photo_uri;
    int photo_index = 0, camera_animation;
    TextView curr_photo_count;
    ImageCapture ic;
    ImageView item_photo_1, item_photo_2, item_photo_3, item_photo_4;
    private static final int CAMERA_REQUEST_CODE = 100;
    PreviewView camera_preview;
    ProcessCameraProvider camera_process;
    ConstraintLayout camera_layout, gallery_layout;
    boolean existing_photo;
    Button cancel_btn, save_btn, capture_photo_btn, exit_camera_btn, add_photo_btn;
    String date, description, make, model, serialNo, estValue, comment;
    ActivityResultLauncher<String> gallery;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        this.date = getIntent().getStringExtra("dateOfAcquisition");
        this.description = getIntent().getStringExtra("description");
        this.make = getIntent().getStringExtra("make");
        this.model = getIntent().getStringExtra("model");
        this.serialNo = getIntent().getStringExtra("serialNumber");
        this.estValue = getIntent().getStringExtra("estimatedValue");
        this.comment = getIntent().getStringExtra("comment");
        this.photo_uri = getIntent().getParcelableArrayListExtra("photoUri");
        this.existing_photo = getIntent().getBooleanExtra("Edit",false);

        item_photo_1 = findViewById(R.id.item_photo_1);
        item_photo_1.setOnClickListener(this);

        item_photo_2 = findViewById(R.id.item_photo_2);
        item_photo_2.setOnClickListener(this);

        item_photo_3 = findViewById(R.id.item_photo_3);
        item_photo_3.setOnClickListener(this);

        item_photo_4 = findViewById(R.id.item_photo_4);
        item_photo_4.setOnClickListener(this);

        curr_photo_count = findViewById(R.id.photo_count);
        String text = countNonEmptyUris(photo_uri) + "/4 Images";
        curr_photo_count.setText(text);

        for (int i = 0; i < max_array_size; i ++) {
            try {
                photo_uri.get(i);
            }catch (IndexOutOfBoundsException e) {
                photo_uri.add(i, null);
            }
        }
        album = new ArrayList<>();
        album.add(item_photo_1);
        album.add(item_photo_2);
        album.add(item_photo_3);
        album.add(item_photo_4);

        camera_preview = findViewById(R.id.camera_preview);
        camera_layout = findViewById(R.id.camera_view);
        camera_layout.setVisibility(View.GONE);
        gallery_layout = findViewById(R.id.gallery_constraint);
        camera_animation = getResources().getInteger(android.R.integer.config_shortAnimTime);

        cancel_btn = findViewById(R.id.cancel_edit);
        cancel_btn.setOnClickListener(this);

        save_btn = findViewById(R.id.save_edit);
        save_btn.setOnClickListener(this);

        add_photo_btn = findViewById(R.id.add_photo_btn);
        add_photo_btn.setOnClickListener(this);

        capture_photo_btn = findViewById(R.id.capture_photo_button);
        capture_photo_btn.setOnClickListener(this);

        exit_camera_btn = findViewById(R.id.exit_camera_button);
        exit_camera_btn.setOnClickListener(this);

        gallery = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    /**
                     *
                     * @param o
                     */
                    @Override
                    public void onActivityResult(Uri o) {
                        if (o == null) {
                            return;
                        }
                        addPhotoToAlbum(o);
                    }
                });

        if (existing_photo) {
            Log.d("PHOTOS", "Loading photos from database");
            loadPhotos();
        }
    }

    private void loadPhotos() {
        if (photo_uri != null && !photo_uri.isEmpty()) {
            for (int i = 0; i < Math.min(album.size(), photo_uri.size()); i++) {
                Uri photoUri = photo_uri.get(i);
                ImageView imageView = album.get(i);
                if (photoUri != null) {
                    RequestOptions requestOptions = new RequestOptions()
                            .placeholder(R.drawable.ic_android_black_24dp) // Placeholder image while loading
                            .error(R.drawable.error_image); // Image to show in case of error
                    Glide.with(this)
                            .load(photoUri)
                            .apply(requestOptions)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(imageView);
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    imageView.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    /**
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        int vID = v.getId();
        if (vID == R.id.add_photo_btn) {
            if (countNonEmptyUris(photo_uri)== 4) {
                Toast.makeText(getApplicationContext(),"Album Full (4/4)",Toast.LENGTH_LONG).show();
                return;
            }
            photo_index = countNonEmptyUris(photo_uri);
            Bundle bundle = new Bundle();
            bundle.putBoolean("image",false);
            PhotoFragment pf = new PhotoFragment();
            pf.setArguments(bundle);
            pf.show(getSupportFragmentManager(), "Add_photo");
        } else if (vID == R.id.cancel_edit) { finish(); }

        // && existing_photo
        else if (vID == R.id.save_edit) {
            Intent retIntent = new Intent();
            retIntent.putParcelableArrayListExtra("updatedPhotoUri", photo_uri);
            setResult(Activity.RESULT_OK, retIntent);
            finish();}

//        else if (vID == R.id.save_edit) {
//
//            HashMap<String, Object> item_hash = new HashMap<String, Object>();
//            item_hash.put("dateOfAcquisition",this.date);
//            item_hash.put("description",this.description);
//            item_hash.put("make",this.make);
//            item_hash.put("model",this.model);
//            item_hash.put("serialNumber",this.serialNo);
//            item_hash.put("estimatedValue",this.estValue);
//            item_hash.put("comment",this.comment);
//            item_hash.put("photo", this.photo_uri);
//
//            Intent retIntent = new Intent();
//            retIntent.putExtra("editedItem", item_hash);
//            setResult(Activity.RESULT_OK, retIntent);
//            finish();

//            Intent i = new Intent(this, ItemListView.class);
//            startActivity(i); }
         else if (vID == R.id.capture_photo_button) {

            capturePhoto();
            camera_layout.setVisibility(View.GONE);
        } else if (vID == R.id.exit_camera_button) {

            camera_layout.setVisibility(View.GONE);
            controlCameraView(false);
        }
        if (v.getVisibility() != View.INVISIBLE) {
            if (vID == R.id.item_photo_1) {

                photo_index = 0;
                deletePhoto();
            } else if (vID == R.id.item_photo_2) {

                photo_index = 1;
                deletePhoto();
            } else if (vID == R.id.item_photo_3) {

                photo_index = 2;
                deletePhoto();
            } else if (vID == R.id.item_photo_4) {

                photo_index = 3;
                deletePhoto();
            }
        }
    }
    /**
     *
     */
    @Override
    public void selectGallery() { gallery.launch("image/*"); }

    /**
     *
     * @param photoUri
     */
    private void addPhotoToAlbum(Uri photoUri) {
        ImageView image = album.get(photo_index);
        if (photo_uri.size() < max_array_size && photo_uri.size() == photo_index) {
            photo_uri.add(photoUri);
        }
        photo_uri.set(photo_index,photoUri);
        image.setImageURI(photoUri);
        image.setVisibility(View.VISIBLE);
        String name = "image" + photo_index;
        String text = countNonEmptyUris(photo_uri) + "/4 Images";
        curr_photo_count.setText(text);
    }

    private int countNonEmptyUris(List<Uri> uris) {
        int count = 0;
        for (Uri uri : uris) {
            if (uri != null) {
                count++;
            }
        }
        return count;
    }

    /**
     *
     */
    public void selectDelete() {
        for (int i = photo_index; i < album.size()-1; ++i) {

            if (album.get(i+1).getVisibility() == View.INVISIBLE) {
                album.get(i).setVisibility(View.INVISIBLE);
                photo_uri.set(i, null);
                break;
            } else {
                album.get(i).setImageURI(photo_uri.get(i+1));
                photo_uri.set(i, photo_uri.get(i+1));
            }
        }
        String curr_count = countNonEmptyUris(photo_uri) + "/4 Images";
        curr_photo_count.setText(curr_count);
    }

    /**
     *
     */
    private void deletePhoto() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("image",true);
        PhotoFragment pf = new PhotoFragment();
        pf.setArguments(bundle);
        pf.show(getSupportFragmentManager(), "DELETE");
    }

    /**
     *
     */
    @Override
    public void startCamera() {
        String camera_permission = Manifest.permission.CAMERA;

        if (EasyPermissions.hasPermissions(this, camera_permission)) {

            controlCameraView(true);
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
    /**
     *
     * @param cameraProvider
     */
    private void selectCamera(ProcessCameraProvider cameraProvider) {

        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(camera_preview.getSurfaceProvider());
        ic = new ImageCapture.Builder().build();

        try {
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(this,cameraSelector,preview, ic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private void capturePhoto() {
        if (ic == null) return;
        ic.takePicture(ContextCompat.getMainExecutor(getBaseContext()), new ImageCapture.OnImageCapturedCallback() {
            /**
             * @param image
             *
             */
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                super.onCaptureSuccess(image);
                Bitmap image_bit = image.toBitmap();
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(image_bit, image.getWidth(), image.getHeight(), true);
                Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                savePhotoItem(rotatedBitmap);
                controlCameraView(false);
            }

            /**
             *
             * @param exception An {@link ImageCaptureException} that contains the type of error, the
             *                  error message and the throwable that caused it.
             */
            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                super.onError(exception);
                exception.printStackTrace();
                Toast.makeText(getApplicationContext(),"Failed to capture photo",Toast.LENGTH_SHORT).show();
                controlCameraView(false);
            }
        });
    }

    /**
     *
     * @param image_bit
     */
    private void savePhotoItem(Bitmap image_bit) {
        Uri photoUri = getImageUri(this, image_bit);
        addPhotoToAlbum(photoUri);
        ImageView image = album.get(photo_index);
        if (photo_uri.size() < 4 && photo_uri.size() == photo_index) {
            photo_uri.add(photoUri);
        }
        photo_uri.set(photo_index,photoUri);
        image.setImageURI(photoUri);
        image.setVisibility(View.VISIBLE);
        String name = "image" + photo_index;
        String curr_count = countNonEmptyUris(photo_uri) + "/4 Images";
        curr_photo_count.setText(curr_count);

        // send to firebase storage

    }

    /**
     *
     */
    private void controlCameraView(boolean opening) {
        View open;
        View close;
        if (opening){
            open = camera_layout;
            close = gallery_layout;
        } else {
            open = gallery_layout;
            close = camera_layout;
        }
        open.setAlpha(0f);
        open.setVisibility(View.VISIBLE);
        open.animate()
                .alpha(1f)
                .setDuration(camera_animation)
                .setListener(null);
        close.animate()
                .alpha(0f)
                .setDuration(camera_animation)
                .setListener(new AnimatorListenerAdapter() {
                    /**
                     * @param animation The animation which reached its end.
                     */
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        close.setVisibility(View.GONE);
                    }
                });
    }

    /**
     *
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (camera_process != null) {
            camera_process.unbindAll();
            camera_process = null;
        }
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
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
