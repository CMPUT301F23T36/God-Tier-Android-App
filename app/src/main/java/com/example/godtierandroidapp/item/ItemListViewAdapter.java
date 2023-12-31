package com.example.godtierandroidapp.item;

import static android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;


import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.godtierandroidapp.R;
import com.example.godtierandroidapp.fragments.SelectTagFragment;
import com.example.godtierandroidapp.tag.TagListViewAdapter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Adapter that shows the items of an {@code ItemList} into an {@code ItemViewHolder}.
 */
public class ItemListViewAdapter extends RecyclerView.Adapter<ItemListViewAdapter.ItemViewHolder> {

    private ItemListView itemListView;
    private Context context;
    private ItemList itemList;
    private boolean isSelectMode = false;
    private ArrayList<Item> selectedItems = new ArrayList<>();
    private ArrayList<ItemViewHolder> views = new ArrayList<>();

    /**
     * Constructor.
     * @param context The ItemListView to update.
     * @param itemList The ItemList to read from.
     */
    public ItemListViewAdapter(ItemListView context, ItemList itemList) {
        this.context = context;
        this.itemList = itemList;
        this.itemListView = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_element, parent, false);
        return new ItemViewHolder(context, view);
    }

    /**
     * Updates the item with the required click and long click callbacks.
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.getItem(position);
        holder.itemView.setBackgroundColor(item.getColor());
        itemListView.findViewById(R.id.clear_item_button).setOnClickListener(v -> {
            for(int i=0;i<views.size();++i){
                ItemViewHolder views1 = views.get(i);
                views1.itemView.setBackgroundColor(Color.TRANSPARENT);
                item.setColor(Color.TRANSPARENT);
            }
            itemListView.clearList(selectedItems);
            selectedItems.clear();
            isSelectMode = false;
            itemListView.findViewById(R.id.clear_item_button).setVisibility(View.INVISIBLE);
            itemListView.findViewById(R.id.add_tags_button).setVisibility(View.INVISIBLE);
        });
        itemListView.findViewById(R.id.add_tags_button).setOnClickListener(v -> {
            SelectTagFragment fragment = new SelectTagFragment(itemListView.tags, selectedItems);

            fragment.show(itemListView.getSupportFragmentManager(), "Select Tags");
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isSelectMode = true;
                itemListView.findViewById(R.id.clear_item_button).setVisibility(View.VISIBLE);
                itemListView.findViewById(R.id.add_tags_button).setVisibility(View.VISIBLE);
                if (selectedItems.contains(item)) {
                    holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                    item.setColor(Color.TRANSPARENT);
                    views.remove(holder);
                    selectedItems.remove(item);
                } else {
                    holder.itemView.setBackgroundColor(Color.GRAY);
                    item.setColor(Color.GRAY);
                    views.add(holder);
                    selectedItems.add(item);
                }

                if (selectedItems.size() == 0) {
                    isSelectMode = false;
                    itemListView.findViewById(R.id.clear_item_button).setVisibility(View.INVISIBLE);
                    itemListView.findViewById(R.id.add_tags_button).setVisibility(View.INVISIBLE);
                };
                return true;
            }
        });
        holder.itemView.setOnClickListener((view) -> {
            if (isSelectMode) {
                if (selectedItems.contains(item)) {
                    holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                    item.setColor(Color.TRANSPARENT);
                    views.remove(holder);
                    selectedItems.remove(item);
                } else {
                    holder.itemView.setBackgroundColor(Color.GRAY);
                    item.setColor(Color.GRAY);
                    views.add(holder);
                    selectedItems.add(item);
                }

                if (selectedItems.size() == 0){
                    isSelectMode = false;
                    itemListView.findViewById(R.id.clear_item_button).setVisibility(View.INVISIBLE);
                    itemListView.findViewById(R.id.add_tags_button).setVisibility(View.INVISIBLE);
                }
            }
            else {
                Intent intent = new Intent(context, ItemDetailsView.class);
                intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                intent.putExtra("item", item);
                intent.putExtra("item idx", position);
                intent.putExtra("tag_list", itemListView.tags);
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