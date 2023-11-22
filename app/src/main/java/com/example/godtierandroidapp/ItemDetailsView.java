package com.example.godtierandroidapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

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
    private ArrayList<Tag> listOfTagObjects;

    private EditText description_field;
    private TextView date_of_purchase_field;
    private EditText estimated_value_field;
    private EditText make_field;
    private EditText model_field;
    private EditText serial_no_field;
    private TextView tags_field;
    private Button item_details_confirm;
    private Button item_details_delete;
    private Button item_add_tag;
    private ArrayList<Integer> tagList = new ArrayList<>();

    private ArrayList<String> tagStrings = new ArrayList<>();
    private boolean[] selectedTags;

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

        item_details_delete = findViewById(R.id.item_detail_delete);
        listOfTagObjects = (ArrayList<Tag>) getIntent().getSerializableExtra("tag_list");

        for(int i=0; i<listOfTagObjects.size();++i){
            tagStrings.add(listOfTagObjects.get(i).getName());
        }
        selectedTags = new boolean[tagStrings.size()];

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
        item_add_tag = findViewById(R.id.add_tags);
        updateFields();



        tags_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                // Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ItemDetailsView.this);

                // set title
                builder.setTitle("Select Tags");

                // set dialog non cancelable
                builder.setCancelable(false);

                builder.setMultiChoiceItems(tagStrings.toArray(new String[tagStrings.size()]),selectedTags,new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if (b) {
                            tagList.add(i);

                            Collections.sort(tagList);
                        } else {
                            tagList.remove(Integer.valueOf(i));
                        }
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Initialize string builder
                        StringBuilder stringBuilder = new StringBuilder();
                        // use for loop
                        for (int j = 0; j < tagList.size(); j++) {
                            // concat array value
                            stringBuilder.append(tagStrings.get(tagList.get(i)));
                            // check condition
                            if (j != tagList.size() - 1) {
                                // When j value  not equal
                                // to lang list size - 1
                                // add comma
                                stringBuilder.append(", ");
                            }
                        }
                        // set text on textView
                        tags_field.setText(stringBuilder.toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // dismiss dialog
                        dialogInterface.dismiss();
                    }
                });
                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // use for loop
                        for (int j = 0; j < selectedTags.length; j++) {
                            // remove all selection
                            selectedTags[j] = false;
                            // clear language list
                            tagList.clear();
                            // clear text view value
                            tags_field.setText("");
                        }
                    }
                });
                // show dialog
                builder.show();
            }
        });

        // Set click listener for add tag button
        item_add_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTagFragment fragment = AddTagFragment.newInstance((Serializable) listOfTagObjects);
                fragment.show(getSupportFragmentManager(), "ADD TAG");
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

                Intent retIntent = new Intent();
                retIntent.putExtra("old item idx", item_idx); // will be -1 if new item
                retIntent.putExtra("new item", item);
                setResult(Activity.RESULT_OK, retIntent);
                finish();
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

    public Item getItem(){
        return item;
    }

    @Override
    public void onConfirmPressed(ArrayList<Tag> tag_list) {

    }

    public void onConfirmPressed(){}
}
