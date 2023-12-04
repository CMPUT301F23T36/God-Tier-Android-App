package com.example.godtierandroidapp.tag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.godtierandroidapp.item.Item;
import com.example.godtierandroidapp.R;

import java.util.ArrayList;

/**
 * Very straightforward class that adapts a list of tags into a view.
 * @author Alex
 */
public class TagListViewAdapter extends RecyclerView.Adapter<TagListViewAdapter.TagViewHolder> {
    private Context context;
    private Item item;
    private ArrayList<Tag> tag_list;

    public TagListViewAdapter(Context context) {
        this.context = context;
        this.item = null;
        this.tag_list = new ArrayList<>();
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
        if (item != null) {
            Tag tag = item.getTags().get(position);

            holder.textViewTagName.setText(tag.getName());
        } else {
            holder.textViewTagName.setText("ERROR: null item");
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

    /**
     * A container view for the adapted list of Tags.
     */
    public class TagViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTagName;

        public TagViewHolder(View tagView) {
            super(tagView);
            textViewTagName = tagView.findViewById(R.id.textViewTagName);
        }
    }
}