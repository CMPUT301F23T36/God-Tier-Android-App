package com.example.godtierandroidapp;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ItemListView extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemListViewAdapter itemAdapter;
    private ItemList itemList;

    public void updateList() {
        if (itemAdapter != null) {
            itemAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list_view);

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
    }

    public void setFilter(ItemList.FilterCriteria filterFunction) {
        itemList.setFilter(filterFunction);
        itemAdapter.notifyDataSetChanged();
    }

    public void setSort(Comparator<Item> sortComparator) {
        itemList.setSort(sortComparator);
        itemAdapter.notifyDataSetChanged();
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
                            return;
                        }

                        itemList.removeItem(itemList.getItem(oldItemIdx));
                        itemAdapter.notifyDataSetChanged();

                        return;
                    }

                    if (oldItemIdx == -1) {
                        itemList.addItem(newItem);
                    } else {
                        itemList.updateItem(oldItemIdx, newItem);
                    }

                    itemAdapter.notifyDataSetChanged();
                }
            });

    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }
}
