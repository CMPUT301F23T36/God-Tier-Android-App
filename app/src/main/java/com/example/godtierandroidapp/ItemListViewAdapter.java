package com.example.godtierandroidapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;


import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ItemListViewAdapter extends RecyclerView.Adapter<ItemListViewAdapter.ItemViewHolder> {

    private ItemListView itemListView;
    private Context context;
    private ItemList itemList;
    private boolean isSelectMode = false;
    private ArrayList<Item> selectedItems = new ArrayList<>();

    public ItemListViewAdapter(ItemListView context, ItemList itemList) {
        this.context = context;
        this.itemList = itemList;
        itemListView = (ItemListView)context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_element, parent, false);
        return new ItemViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.getItem(position);
        itemListView.findViewById(R.id.clear_item_button).setOnClickListener(v -> {
            itemListView.clearList(selectedItems);
            isSelectMode = false;
            itemListView.findViewById(R.id.clear_item_button).setVisibility(View.INVISIBLE);
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isSelectMode = true;
                itemListView.findViewById(R.id.clear_item_button).setVisibility(View.VISIBLE);
                if (selectedItems.contains(item)) {
                    holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                    selectedItems.remove(item);
                } else {
                    holder.itemView.setBackgroundColor(Color.GRAY);
                    selectedItems.add(item);
                }

                if (selectedItems.size() == 0) {
                    isSelectMode = false;
                    itemListView.findViewById(R.id.clear_item_button).setVisibility(View.INVISIBLE);
                };
                return true;
            }
        });
        holder.itemView.setOnClickListener((view) -> {
            if (isSelectMode) {
                if (selectedItems.contains(item)) {
                    holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                    selectedItems.remove(item);
                } else {
                    holder.itemView.setBackgroundColor(Color.GRAY);
                    selectedItems.add(item);
                }

                if (selectedItems.size() == 0){
                    isSelectMode = false;
                    itemListView.findViewById(R.id.clear_item_button).setVisibility(View.INVISIBLE);
                }
            }
            else {
                Intent intent = new Intent(context, ItemDetailsView.class);
                intent.putExtra("item", item);
                intent.putExtra("item idx", position);
                itemListView.itemEditLauncher.launch(intent);
            }
        });

        // Bind data to the TextViews in the list item layout
        holder.textViewDescription.setText(item.getDescription());
        holder.textViewEstimatedValue.setText("Estimated Value: $" + item.getEstimatedValue());
        holder.tagViewListAdapter.setItem(item);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDescription;
        TextView textViewEstimatedValue;

        TagListViewAdapter tagViewListAdapter;
        private RecyclerView tagViewList;

        public ItemViewHolder(Context context, View itemView) {
            super(itemView);
            // Initialize the RecyclerView
            tagViewList = itemView.findViewById(R.id.tagList);
            tagViewList.setLayoutManager(new LinearLayoutManager(
                    context,
                    RecyclerView.HORIZONTAL,
                    false
            ));

            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewEstimatedValue = itemView.findViewById(R.id.textViewEstimatedValue);
            tagViewListAdapter = new TagListViewAdapter(context);
            tagViewList.setAdapter(tagViewListAdapter);
        }
    }
}