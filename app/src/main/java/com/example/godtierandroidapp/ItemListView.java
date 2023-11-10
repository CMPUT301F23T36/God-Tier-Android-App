package com.example.godtierandroidapp;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Date;

public class ItemListView extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemListViewAdapter itemAdapter;
    private ItemList itemList;
    private TextView totalValue;

    public void updateList() {
        if (itemAdapter != null) {
            itemAdapter.notifyDataSetChanged();
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        totalValue.setText("Total value: " + decimalFormat.format(itemList.getTotalValue()));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list_view);

        totalValue = findViewById(R.id.totalValue);

        // init ItemList ---------------------------------------------------------------------------

        itemList = new ItemList();
        ArrayList<Tag> tags = new ArrayList<>(Arrays.asList(
                new Tag("tag1"),
                new Tag("tag2"),
                new Tag("tag3")
        ));
        itemList.addItem(new Item("Test item 1", 100.0, tags));
        itemList.addItem(new Item("Test item 2", 200.0, new ArrayList<>()));
        itemList.addItem(new Item("Test item 3", 50.0, new ArrayList<>()));

        itemList.addItem(new Item(new Date(2011, 2, 28),
                "Printer 1",
                "Samsung",
                "T1000",
                "1a57494ds9",
                250.00,
                "Sample Comment",
                new ArrayList<>(),
                new ArrayList<>()));
        itemList.addItem(new Item(new Date(2013, 9, 4),
                "Printer 2",
                "LG",
                "ODR66",
                "735hj27365d",
                200.00,
                "Sample Comment",
                new ArrayList<>(),
                new ArrayList<>()));
        itemList.addItem(new Item(new Date(2022, 5, 11),
                "Laptop 1",
                "ASUS",
                "GPro 909",
                "867378649",
                1400.00,
                "Sample Comment",
                new ArrayList<>(),
                new ArrayList<>()));
        itemList.addItem(new Item(new Date(2017, 11, 25),
                "Laptop 2",
                "Samsung",
                "Mk 2319",
                "98697868768",
                900.00,
                "Sample Comment",
                new ArrayList<>(),
                new ArrayList<>()));
        itemList.addItem(new Item(new Date(2017, 11, 25),
                "Bicycle",
                "Ghost",
                "Interloper 5",
                "1234567ababa",
                600.00,
                "Sample Comment",
                new ArrayList<>(),
                new ArrayList<>()));
        itemList.addItem(new Item(new Date(2017, 11, 25),
                "Unicycle",
                "Rasmataz",
                "SR88",
                "774466335re",
                400.00,
                "Sample Comment",
                new ArrayList<>(),
                new ArrayList<>()));
        itemList.addItem(new Item(new Date(2017, 11, 25),
                "Scooter",
                "Birdman Scoots",
                "Mk 1235",
                "545545",
                120.00,
                "Sample Comment",
                new ArrayList<>(),
                new ArrayList<>()));
        itemList.addItem(new Item(new Date(2018, 12, 26),
                "Laptop 3",
                "Apple",
                "Macbook Air 12",
                "888888888888",
                1800.00,
                "Sample Comment",
                new ArrayList<>(),
                new ArrayList<>()));

        // init view and ItemListViewAdapter -------------------------------------------------------

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new ItemListViewAdapter(this, itemList);
        recyclerView.setAdapter(itemAdapter);

        // init button callbacks -------------------------------------------------------------------

        findViewById(R.id.sort_button).setOnClickListener(v -> {
            SortFragment sortFragment = new SortFragment(this);
            sortFragment.show(getSupportFragmentManager(), "SortFragment");
        });

        findViewById(R.id.filter_button).setOnClickListener(v -> {
            FilterFragment filterFragment = new FilterFragment(this);
            filterFragment.show(getSupportFragmentManager(), "FilterFragment");
        });

        findViewById(R.id.add_item_button).setOnClickListener(v -> {
            Intent intent = new Intent(this, ItemDetailsView.class);
            itemEditLauncher.launch(intent);
        });
        findViewById(R.id.clear_item_button).setOnClickListener(v -> {
            clearList();
        });
    }
    public void clearList() {
        itemList.clear();
        updateList();
    }

    public void setFilter(ItemList.FilterCriteria filterFunction) {
        itemList.setFilter(filterFunction);
        updateList();
    }

    public void setSort(Comparator<Item> sortComparator) {
        itemList.setSort(sortComparator);
        updateList();
    }

    public ActivityResultLauncher<Intent> itemEditLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent intent = result.getData();
                int oldItemIdx = intent.getIntExtra("old item idx", -1);
                Item newItem = (Item) intent.getSerializableExtra("new item");

                if (newItem == null) {
                    if (oldItemIdx == -1) {
                        Log.d(
                                "ItemListView",
                                "null Item returned from ItemDetailsView"
                        );

                        itemList.removeItem(itemList.getItem(oldItemIdx));
                        updateList();
                        return;
                    }

                    itemList.removeItem(itemList.getItem(oldItemIdx));
                    updateList();
                    return;
                }

                if (oldItemIdx == -1) {
                    itemList.addItem(newItem);
                } else {
                    itemList.updateItem(oldItemIdx, newItem);
                }

                updateList();
            }
        });
    }