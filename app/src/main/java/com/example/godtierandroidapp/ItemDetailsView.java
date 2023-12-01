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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.widget.Toast;


 import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Contains an expaned detailed view of the item including all its properties
 *
 * @author Alex
 * @version 1.0
 * @since 2023-11-05
 */
public class ItemDetailsView extends AppCompatActivity implements AddTagFragment.OnFragmentInteractionListener {
    private Item item;
    private int item_idx;
    private ArrayList<Tag> tag_list;
    ArrayList<ImageView> album;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private EditText description_field;
    private TextView date_of_purchase_field;
    private EditText estimated_value_field;
    private EditText make_field;
    private EditText model_field;
    private EditText serial_no_field;
    ArrayList<Uri> photo_field;
    private TextView tags_field;
    private Button item_details_confirm;
    private Button item_details_delete;
    private ImageView edit_item_photo;
    private ImageView item_photo;
    private ActivityResultLauncher<String> galleryLauncher;
    private ViewPager viewPager;
    private PagerAdapter myPagerAdapter;
    private static final String TAG = "ItemDetailsView";

    private Button item_add_tag;
    private Button item_add_photo;
    Button item_scan_barcode;
    ImageView iv;

    /**
     * Called when an item is selected to show its detailed view with all fields. Initializes
     * activity, and then retrieves the selected item and updates it with all its field info. Sets
     * up confirm and delete buttons.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details_view);

        ActivityCompat.requestPermissions(ItemDetailsView.this,
                new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

        item_details_delete = findViewById(R.id.item_detail_delete);

        // Get selected item from ItemList activity
        Intent intent = getIntent();
        item = (Item) intent.getSerializableExtra("item");
        if (item == null) {
            item = new Item();
            item_details_delete.setVisibility(View.GONE);
        }
        item_idx = intent.getIntExtra("item idx", -1);

        //Initialize fields and update with item's information
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

        item_add_tag = findViewById(R.id.add_tags);
        item_add_photo = findViewById(R.id.add_photo);
        item_scan_barcode = findViewById(R.id.scan_barcode);
        updateFields();

        viewPager = findViewById(R.id.viewPager);
        myPagerAdapter = new PagerAdapter(this, item);
        viewPager.setAdapter(myPagerAdapter);

        // temp attempt at displaying photos
        iv = findViewById(R.id.item_photo);

        if (item.photos().size() != 0) {
            item_photo.setVisibility(View.GONE);
            myPagerAdapter.notifyDataSetChanged();
            updateImages();
        }

//        if (item.photos().size() > 0) {
//            loadImageView();
//        }
        //updatePhoto();

        // Set click listener for add tag button
        item_add_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Tag> tag_list = (ArrayList<Tag>) intent.getExtras().getSerializable("tag_list");
                AddTagFragment fragment = AddTagFragment.newInstance((Serializable) tag_list);
                fragment.show(getSupportFragmentManager(), "ADD TAG");
            }
        });

        // Set click listener for add photo button
        item_add_photo.setOnClickListener(new View.OnClickListener()  {
            /**
             *
             * @param v
             */
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ItemDetailsView.this, PhotoActivity.class);
                i.putExtra("Edit", item.photos().size() > 0);
                i.putParcelableArrayListExtra("photoUri", item.photos());
                addPhotoLauncher.launch(i);
            }
        });

        // Set click listener for confirm button
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

//                if (photo_field!=null && !photo_field.isEmpty()) {
//                    item.photosSet(photo_field);
//                }

                Intent retIntent = new Intent();
                retIntent.putExtra("old item idx", item_idx); // will be -1 if new item
                retIntent.putExtra("new item", item);
                setResult(Activity.RESULT_OK, retIntent);
                try {
                    // Your code here
                    finish();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error in onClick: " + e.getMessage());

                }
            }
        });

        // Set click listener for delete button
        item_details_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent retIntent = new Intent();
                retIntent.putExtra("old item idx", item_idx);
                setResult(Activity.RESULT_OK, retIntent);
                finish();
            }
        });

        // open gallery to get data of the image
//        galleryLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        if (result.getResultCode() == RESULT_OK) {
//
//                            // Handle the result
//                            Intent data = result.getData();
//                            if (data != null) {
//                                Uri selectedImageUri = data.getData();
//                                if (selectedImageUri != null) {
//                                    // TO ensure the permission exist for later needs.
//                                    getContentResolver().takePersistableUriPermission(
//                                            selectedImageUri,
//                                            Intent.FLAG_GRANT_READ_URI_PERMISSION
//                                    );
//
//                                    // Set the selected image URI to the second ImageView
//                                    item.addPhoto(selectedImageUri);
//                                    myPagerAdapter.notifyDataSetChanged();
//                                    updateImages();
//                                }
//
//                            }
//                        }
//                    }
//                });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri == null) { return; }
                        item.addPhoto(uri);
                        myPagerAdapter.notifyDataSetChanged();
                        updateImages();
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

        item_scan_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void loadImageView() {
        if (item.photos() != null && !item.photos().isEmpty()) {
            album.clear();
            for (int i = 0; i < item.photos().size(); i++) {
                Uri photoUri = item.photos().get(i);
                ImageView imageView = new ImageView(this);
                album.add(imageView);
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
                }
            }
            myPagerAdapter.updateData(item.photos());
            viewPager.setAdapter(myPagerAdapter);
        }
    }

    public ActivityResultLauncher<Intent> addPhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent i = result.getData();
                    assert i != null;
                    if (item.photos().size() == 0) {
                        item_photo.setVisibility(View.GONE);
                    }
//                    photo_field = i.getParcelableArrayListExtra("updatedPhotoUri");
                    item.photosSet(i.getParcelableArrayListExtra("updatedPhotoUri"));
                    myPagerAdapter.notifyDataSetChanged();
                    updateImages();
                    }
                });


    // Call this method when you want to update the data set
    private void updateImages() {
        // New image resources
        ArrayList<Uri> newImageResources = item.photos();

        // Update the adapter's data set
        myPagerAdapter.updateData(newImageResources);
    }

    private void openGallery() {
//        // Create an intent to pick an image from the gallery
//        Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        galleryIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
//        galleryIntent.addFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
//        galleryIntent.setType("image/*");
//
//        // Launch the gallery activity with the intent using the ActivityResultLauncher
//        galleryLauncher.launch(galleryIntent);
        galleryLauncher.launch("image/*");
    }

    /**
     * Updates item fields
     */
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

    //protected void updatePhoto() {
    //    Uri uri = item.getPhoto(photo_index);
    //    if (uri != null) {
    //        iv.setImageBitmap(uri);
    //    }
    //}

    @Override
    public void onConfirmPressed(ArrayList<Tag> tag_list) {

    }

    public void onConfirmPressed(){}
}
