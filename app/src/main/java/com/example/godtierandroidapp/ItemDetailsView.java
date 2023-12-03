package com.example.godtierandroidapp;

import static android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.widget.Toast;


import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ItemDetailsView extends AppCompatActivity {
    private Item item;
    private int item_idx;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private EditText description_field;
    private TextView date_of_purchase_field;
    private EditText estimated_value_field;
    private EditText make_field;
    private EditText model_field;
    private EditText serial_no_field;
    private TextView tags_field;
    private Button item_details_confirm;
    private Button item_details_delete;
    private ImageView edit_item_photo;
    private ImageView item_photo;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ViewPager viewPager;
    private PagerAdapter myPagerAdapter;
    private static final String TAG = "ItemDetailsView";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details_view);

        ActivityCompat.requestPermissions(ItemDetailsView.this,
                new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

        item_details_delete = findViewById(R.id.item_detail_delete);

        Intent intent = getIntent();
        item = (Item) intent.getSerializableExtra("item");
        if (item == null) {
            item = new Item();
            item_details_delete.setVisibility(View.GONE);
        }
        item_idx = intent.getIntExtra("item idx", -1);

        description_field = findViewById(R.id.description_field);
        date_of_purchase_field = findViewById(R.id.date_of_purchase_field);
        estimated_value_field = findViewById(R.id.estimated_value_field);
        make_field = findViewById(R.id.make_field);
        model_field = findViewById(R.id.model_field);
        serial_no_field = findViewById(R.id.serial_no_field);
        tags_field = findViewById(R.id.tags_field);
        item_details_confirm = findViewById(R.id.item_detail_confirm);
        edit_item_photo = findViewById(R.id.edit_photo);
        item_photo = findViewById(R.id.item_photo);
        updateFields();


        item_details_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setDescription(description_field.getText().toString());
                try{ item.setEstimatedValue(Double.parseDouble(estimated_value_field.getText().toString()));
                } catch (NumberFormatException e) {
                    // not valid double
                }
                item.setMake(make_field.getText().toString());
                item.setModel(model_field.getText().toString());
                item.setSerialNumber(serial_no_field.getText().toString());

                Intent retIntent = new Intent();
                retIntent.putExtra("old item idx", item_idx); // will be -1 if new item
                retIntent.putExtra("new item", item);
                setResult(Activity.RESULT_OK, retIntent);
                try {
                    // Your code here
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error in onClick: " + e.getMessage());

                }
            }
        });

        item_details_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent retIntent = new Intent();
                retIntent.putExtra("old item idx", item_idx);
                setResult(Activity.RESULT_OK, retIntent);
                finish();
            }
        });
        if (item.getUri().size() != 0) {
            item_photo.setVisibility(View.GONE);
        }


        // open gallery to get data of the image
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {

                            // Handle the result
                            Intent data = result.getData();
                            if (data != null) {
                                Uri selectedImageUri = data.getData();
                                if (selectedImageUri != null) {
                                    // TO ensure the permission exist for later needs.
                                    getContentResolver().takePersistableUriPermission(
                                            selectedImageUri,
                                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    );

                                    // Set the selected image URI to the second ImageView
                                    item.addPhoto(selectedImageUri);
                                    myPagerAdapter.notifyDataSetChanged();
                                    updateImages();
                                }

                            }
                        }
                    }
                });

        // Set OnClickListener for the second ImageView to open the gallery
        edit_item_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item_photo.setVisibility(View.GONE);
                openGallery();
//                // Check if the READ_EXTERNAL_STORAGE permission is granted
//                if (ContextCompat.checkSelfPermission(ItemDetailsView.this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    // Permission is not granted, request it
//                    ActivityCompat.requestPermissions(ItemDetailsView.this,
//                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
//                } else if (ContextCompat.checkSelfPermission(ItemDetailsView.this, Manifest.permission.READ_MEDIA_IMAGES)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(ItemDetailsView.this,
//                            new String[]{Manifest.permission.READ_MEDIA_IMAGES},
//                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
//                } else {
//                    // Permission is already granted, proceed with opening the gallery
//                    openGallery();
//                }
            }
        });



        viewPager = findViewById(R.id.viewPager);
        myPagerAdapter = new PagerAdapter(this, item);
        viewPager.setAdapter(myPagerAdapter);
    }


    // Call this method when you want to update the data set
    private void updateImages() {
        // New image resources
        List<Uri> newImageResources = item.getUri();

        // Update the adapter's data set
        myPagerAdapter.updateData(newImageResources);
    }

    private void openGallery() {
        // Create an intent to pick an image from the gallery
        Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
        galleryIntent.addFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        galleryIntent.setType("image/*");

        // Launch the gallery activity with the intent using the ActivityResultLauncher
        galleryLauncher.launch(galleryIntent);
    }


    protected void updateFields() {
        description_field.setText(item.getDescription());
        date_of_purchase_field.setText(String.valueOf(item.getDateOfAcquisition()));
        estimated_value_field.setText(String.valueOf(item.getEstimatedValue()));
        make_field.setText(item.getMake());
        model_field.setText(item.getModel());
        serial_no_field.setText(item.getSerialNumber());

        // TODO
        StringBuilder tags = new StringBuilder(new String());
        if (item.getTags().size() > 0) {
            tags.append(item.getTags().get(0).getName());
        }
        for (int i = 1; i < item.getTags().size(); ++i) {
            tags.append(" ").append(item.getTags().get(i).getName());
        }
        tags_field.setText(tags.toString());


    }



}
