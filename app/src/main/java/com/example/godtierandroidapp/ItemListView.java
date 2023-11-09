package com.example.godtierandroidapp;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
    }

    public void setFilter(ItemList.FilterCriteria filterFunction) {
        itemList.setFilter(filterFunction);
        itemAdapter.notifyDataSetChanged();
    }

    public void setSort(Comparator<Item> sortComparator) {
        itemList.setSort(sortComparator);
        itemAdapter.notifyDataSetChanged();
    }
}