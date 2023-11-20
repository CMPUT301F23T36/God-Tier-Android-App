package com.example.godtierandroidapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 *
 * @author Alex
 * @version 1.0
 * @since 2023-11-06
 */
public class TagListViewAdapter extends RecyclerView.Adapter<TagListViewAdapter.TagViewHolder> {
    private Context context;
    private Item item;
    private ArrayList<Tag> tag_list;

    public TagListViewAdapter(Context context) {
        this.context = context;
        this.item = null;
        this.tag_list = null;
    }
    public TagListViewAdapter(Context context, ArrayList<Tag> tagList) {
        this.context = context;
        this.item = null;
        this.tag_list = tagList;
    }

    public void setItem(Item item) {
        this.item = item;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tag_list_element, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagListViewAdapter.TagViewHolder holder, int position) {
        if(tag_list == null) {
            if (item != null) {
                Tag tag = item.getTags().get(position);

                // Bind data to the TextViews in the list item layout
                holder.textViewTagName.setText(tag.getName());
            } else {
                holder.textViewTagName.setText("ERROR: null item");
            }
        } else {
            for (Tag tag : tag_list) {
                holder.textViewTagName.setText(tag.getName());
            }
        }
    }

    @Override
    public int getItemCount() {
        if (item != null) {
            return item.getTags().size();
        } else {
            return 0;
        }
    }

    public class TagViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTagName;

        public TagViewHolder(View tagView) {
            super(tagView);
            textViewTagName = tagView.findViewById(R.id.textViewTagName);
        }
    }
}