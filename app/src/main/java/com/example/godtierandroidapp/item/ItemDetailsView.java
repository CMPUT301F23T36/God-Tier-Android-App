package com.example.godtierandroidapp.item;

import static android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;

import com.example.godtierandroidapp.R;
import com.example.godtierandroidapp.photo.PagerAdapter;
import com.example.godtierandroidapp.photo.PhotoActivity;
import com.example.godtierandroidapp.photo.ScannerActivity;
import com.example.godtierandroidapp.fragments.AddTagFragment;
import com.example.godtierandroidapp.fragments.DatePickerFragment;
import com.example.godtierandroidapp.fragments.SelectTagFragment;
import com.example.godtierandroidapp.tag.Tag;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Contains an expanded, detailed view of the item, including all its properties.
 *
 * @author Alex, Travis, Boris, Vinayan, Luke
 */
public class ItemDetailsView extends AppCompatActivity implements
        AddTagFragment.OnFragmentInteractionListener, DatePickerDialog.OnDateSetListener
{
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
    ArrayList<Uri> photo_field;
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
    Button item_scan_barcode;

    /**
     * Called when an item is selected to show its detailed view with all fields. Initializes
     * activity, and then retrieves the selected item and updates it with all its field info.
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

        if (item.photos().size() != 0) {
            item_photo.setVisibility(View.GONE);
        }
      
        tags_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectTagFragment fragment = new SelectTagFragment(listOfTagObjects, itemArrayList);

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

        serial_no_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {
                item.setSerialNumber(s.toString());
                if (checkMatchingSerialNumber()) {
                    updateNonSerialNumberFields();
                }
            }
        });

        // Set click listener for add photo button
        item_add_photo.setOnClickListener(new View.OnClickListener()  {
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
            }
        });

        item_scan_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ItemDetailsView.this, ScannerActivity.class);
                i.putExtra("Edit", true);
                scanLauncher.launch(i);
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
                }
            });

    public ActivityResultLauncher<Intent> scanLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent i = result.getData();
                    assert i != null;
                    String scannedSerialNumber = i.getStringExtra("serial number");
                    if (scannedSerialNumber != null) {
                        if (!scannedSerialNumber.equals("")) {
                            item.setSerialNumber(scannedSerialNumber);
                        }
                    }
                    checkMatchingSerialNumber();
                    updateFields();
                }
            });


    /**
     * Updates the shown images.
     */
    private void updateImages() {
        // New image resources
        ArrayList<Uri> newImageResources = item.photos();

        // Update the adapter's data set
        myPagerAdapter.updateData(newImageResources);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
        galleryIntent.addFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        galleryIntent.setType("image/*");

        // Launch the gallery activity with the intent using the ActivityResultLauncher
        galleryLauncher.launch(galleryIntent);
    }

    /**
     * Updates shown item fields.
     */
    protected void updateFields() {
        serial_no_field.setText(item.getSerialNumber());
        updateNonSerialNumberFields();
    }

    /**
     * Because we are watching the value of the serial number field, it can cause an infinite loop
     * with watching/updating/changing the field in some circumstances.
     */
    private void updateNonSerialNumberFields() {
        description_field.setText(item.getDescription());
        estimated_value_field.setText(String.format("%.2f", item.getEstimatedValue()));
        make_field.setText(item.getMake());
        model_field.setText(item.getModel());
        updateTagField();
        updateDateField();
    }

    /**
     * @return if the item's serial number matches any predefined values
     * and fields of the item have been updated, this returns true.
     */
    private boolean checkMatchingSerialNumber() {
        String sno = item.getSerialNumber();
        if (sno.equals("722510168000")) {
            item.setDescription("O'Keeffe's Working Hands Hand Cream");
            return true;
        } else if (sno.equals("X002SR9KF5")) {
            item.setDescription("Gateron Yellow Mechanical Switches");
            item.setEstimatedValue(15.99f);
            item.setMake("Gateron");
            item.setModel("4000");
            return true;
        }

        return false;
    }

    /**
     * Updates the shown tags.
     */
    public void updateTagField() {
        List<Tag> tagList = item.getTags();
        if(tagList.isEmpty()){
            tags_field.setText("");
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("| ");
            for (int j = 0; j < tagList.size(); j++) {
                stringBuilder.append(tagList.get(j).getName());
                stringBuilder.append(" | ");
            }

            tags_field.setText(stringBuilder.toString());
        }
    }

    /**
     * Updates the date.
     */
    protected void updateDateField() {
        StringBuilder str = new StringBuilder();

        int month = date_of_purchase.getMonth() + 1;
        int day = date_of_purchase.getDate();
        int year = date_of_purchase.getYear() + 1900;

        str.append(month);
        str.append("/");
        str.append(day);
        str.append("/");
        str.append(year);

        date_of_purchase_field.setText(str.toString());
    }
  
    public Item getItem(){
        return item;
    }

    /**
     * On confirm for adding a Tag.
     */
    @Override
    public void onConfirmPressed(Tag newTag) {
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
