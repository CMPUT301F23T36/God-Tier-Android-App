package com.example.godtierandroidapp;



import static android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Contains an expaned detailed view of the item including all its properties
 *
 * @author Alex
 * @version 1.0
 * @since 2023-11-05
 */
public class ItemDetailsView extends AppCompatActivity implements AddTagFragment.OnFragmentInteractionListener, DatePickerDialog.OnDateSetListener {
    private Item item;
    private int item_idx;
  
    private ArrayList<Item> itemArrayList;
    private ArrayList<Tag> listOfTagObjects;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private EditText description_field;
    private TextView date_of_purchase_field;
    private Date date_of_purchase;
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

    private Button item_add_tag;
    private Button item_add_photo;
    int ACTIVITY_REQUEST_CODE = 1;
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

        this.listOfTagObjects = (ArrayList<Tag>) getIntent().getSerializableExtra("tag_list");
        this.itemArrayList = new ArrayList<>();
        this.itemArrayList.add(item);

        date_of_purchase = item.getDateOfAcquisition();

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
      
        // temp attempt at displaying photos
        iv = findViewById(R.id.item_photo);
        //updatePhoto();
      
        tags_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectTagFragment fragment = SelectTagFragment.newInstance((Serializable) listOfTagObjects, (Serializable) itemArrayList);

                fragment.show(getSupportFragmentManager(), "Select Tags");
            }
        });

        // Set click listener for date field
        date_of_purchase_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();

                datePickerFragment.show(getSupportFragmentManager(), "datePickerFragment");
            }
        });
       
        // Set click listener for add tag button
        item_add_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTagFragment fragment = new AddTagFragment();

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
                item.setDateOfAcquisition(date_of_purchase);
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
                retIntent.putExtra("new tag list", listOfTagObjects);
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

        if (item.photos().size() != 0) {
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

        item_scan_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        viewPager = findViewById(R.id.viewPager);
        myPagerAdapter = new PagerAdapter(this, item);
        viewPager.setAdapter(myPagerAdapter);
    }

    public ActivityResultLauncher<Intent> addPhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent i = result.getData();
                        assert i != null;
                        item.photosSet(i.getParcelableArrayListExtra("updatedPhotoUri"));
                        myPagerAdapter.notifyDataSetChanged();
                        updateImages();
                    // item.photosSet(getIntent().getParcelableArrayListExtra("updatedPhotoUri"));
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
        // Create an intent to pick an image from the gallery
        Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
        galleryIntent.addFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        galleryIntent.setType("image/*");

        // Launch the gallery activity with the intent using the ActivityResultLauncher
        galleryLauncher.launch(galleryIntent);
    }

    /**
     * Updates item fields
     */
    protected void updateFields() {
        description_field.setText(item.getDescription());
        estimated_value_field.setText(String.valueOf(item.getEstimatedValue()));
        make_field.setText(item.getMake());
        model_field.setText(item.getModel());
        serial_no_field.setText(item.getSerialNumber());
        updateTagField();
        updateDateField();
    }

    protected void updateTagField(){
        // Initialize string builder
        boolean isEmpty = true;
        List<Tag> tagList = item.getTags();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("| ");
        // use for loop
        for (int j = 0; j < tagList.size(); j++) {
            // concat array value
            stringBuilder.append(tagList.get(j).getName());
            stringBuilder.append(" | ");
            isEmpty = false;
        }
        if(isEmpty){ tags_field.setText(""); }
        else { tags_field.setText(stringBuilder.toString()); }
        // set text on textView
    }

    protected void updateDateField() {
        date_of_purchase_field.setText(String.valueOf(date_of_purchase));
    }
  
    public Item getItem(){
        return item;
    }

    //protected void updatePhoto() {
    //    Uri uri = item.getPhoto(photo_index);
    //    if (uri != null) {
    //        iv.setImageBitmap(uri);
    //    }
    //}

    @Override
    public void onConfirmPressed(Tag newTag){
        listOfTagObjects.add(newTag);
        item.addTag(newTag);
        updateTagField();

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Date newDate = new Date(year - 1900, month, dayOfMonth);
        date_of_purchase = newDate;
        updateDateField();
    }
}
