package com.example.godtierandroidapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends androidx.viewpager.widget.PagerAdapter {
    private Context context;
    private Item item;

    public PagerAdapter(Context context, Item item) {
        this.context = context;
        this.item = item;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_image, container, false);

        ImageView item_image = itemView.findViewById(R.id.item_photo);
        item_image.setImageURI(item.photos().get(position));

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
    @Override
    public int getCount() {
        return item.photos().size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public void updateData(ArrayList<Uri> newImageResources) {
        item.photosSet(newImageResources);
        notifyDataSetChanged();
    }
}
