package com.example.godtierandroidapp;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemListView extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemListViewAdapter itemAdapter;
    private ItemList itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list_view);

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create a list of Item objects (you can replace this with your actual data)
        itemList = new ItemList();
        ArrayList<Tag> tags = new ArrayList<>(Arrays.asList(
                new Tag("tag1"),
                new Tag("tag2"),
                new Tag("tag3")
        ));
        itemList.addItem(new Item("Test item 1", 100.0, tags));
        itemList.addItem(new Item("Test item 2", 200.0, new ArrayList<>()));

        // Initialize the custom adapter
        itemAdapter = new ItemListViewAdapter(this, itemList);

        // Set the adapter for the RecyclerView
        recyclerView.setAdapter(itemAdapter);
    }
}