package com.example.godtierandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ItemDetailsView extends AppCompatActivity {
    private Item item;
    private int item_idx;

    private TextView description_field;
    private TextView date_of_purchase_field;
    private TextView estimated_value_field;
    private TextView make_field;
    private TextView model_field;
    private TextView serial_no_field;
    private TextView tags_field;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        item = (Item) intent.getSerializableExtra("item");
        item_idx = intent.getIntExtra("item idx", -1);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details_view);
        description_field = findViewById(R.id.description_field);
        date_of_purchase_field = findViewById(R.id.date_of_purchase_field);
        estimated_value_field = findViewById(R.id.estimated_value_field);
        make_field = findViewById(R.id.make_field);
        model_field = findViewById(R.id.model_field);
        serial_no_field = findViewById(R.id.serial_no_field);
        tags_field = findViewById(R.id.tags_field);

        updateFields();
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
