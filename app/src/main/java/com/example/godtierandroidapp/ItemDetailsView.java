package com.example.godtierandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;

public class ItemDetailsView extends AppCompatActivity {
    private Item item;

    private EditText description_field;
    private TextView date_of_purchase_field;
    private EditText estimated_value_field;
    private EditText make_field;
    private EditText model_field;
    private EditText serial_no_field;
    private TextView tags_field;
    private Button item_details_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        item = (Item) intent.getSerializableExtra("item");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details_view);
        description_field = findViewById(R.id.description_field);
        date_of_purchase_field = findViewById(R.id.date_of_purchase_field);
        estimated_value_field = findViewById(R.id.estimated_value_field);
        make_field = findViewById(R.id.make_field);
        model_field = findViewById(R.id.model_field);
        serial_no_field = findViewById(R.id.serial_no_field);
        tags_field = findViewById(R.id.tags_field);
        item_details_confirm = findViewById(R.id.item_detail_confirm);
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
                finish();
            }
        });
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
            tags.append(item.getTags().get(i).getName()).append(" ");
        }
        tags_field.setText(tags.toString());
    }
}
