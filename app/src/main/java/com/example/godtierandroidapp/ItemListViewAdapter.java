package com.example.godtierandroidapp;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;


import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;

public class ItemListViewAdapter extends RecyclerView.Adapter<ItemListViewAdapter.ItemViewHolder> {

    private Context context;
    private List<Item> itemList;

    public ItemListViewAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_element, parent, false);
        return new ItemViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDetailsView.class);
            intent.putExtra("item", item);
            context.startActivity(intent);
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