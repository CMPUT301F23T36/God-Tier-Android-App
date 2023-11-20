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

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Date;

/**
 * AppCompatActivity of a list view of items. Allows for users to view, add, and manage items,
 * alongside sorting and filtering functionality.
 *
 * @author Alex
 * @version 1.0
 * @since 2023-11-06
 */
public class ItemListView extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemListViewAdapter itemAdapter;
    private ItemList itemList;
    private TextView totalValue;
    private ArrayList<Tag> tags = new ArrayList<>(Arrays.asList(
            new Tag("tag1"),
            new Tag("tag2"),
            new Tag("tag3")
    ));

    /**
     * Updates list view adapter with changes in item list
     */
    public void updateList() {
        if (itemAdapter != null) {
            itemAdapter.notifyDataSetChanged();
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        totalValue.setText("Total value: " + decimalFormat.format(itemList.getTotalValue()));
    }

    /**
     * Called on activity start. Initializes activity, sets up list and associated view. Sets up
     * buttons for adding, clearing, filtering, and sorting
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list_view);

        totalValue = findViewById(R.id.totalValue);

        // init ItemList ---------------------------------------------------------------------------

        itemList = new ItemList();

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
            intent.putExtra("tag_list",(Serializable) tags);
            itemEditLauncher.launch(intent);
        });
    }

    /**
     * Clears item list and updates list view
     */
    public void clearList(ArrayList<Item> itemsToRemove) {
        for (Item i : itemsToRemove){
            itemList.removeItem(i);
        }
        updateList();
    }

    /**
     * Sets filter criteria for items and updates list with filtered list
     * @param filterFunction filter criteria
     */
    public void setFilter(ItemList.FilterCriteria filterFunction) {
        itemList.setFilter(filterFunction);
        updateList();
    }

    /**
     * Sets sort criteria for items and updates list with sorted list
     * @param sortComparator sort criteria
     */
    public void setSort(Comparator<Item> sortComparator) {
        itemList.setSort(sortComparator);
        updateList();
    }

    /**
     * Launches activity result when adding or editing items in list.
     */
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