package com.example.godtierandroidapp;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class SelectTagFragment extends DialogFragment {
    private ArrayList<Tag> listOfTagObjects;
    private ArrayList<Item> listOfItemObjects;
    private boolean[] selectedTags;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public static SelectTagFragment newInstance(Serializable tag_list, Serializable item_array) {
        Bundle args = new Bundle();
        args.putSerializable("item_array", item_array);
        args.putSerializable("tag_list", tag_list);

        SelectTagFragment fragment = new SelectTagFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        listOfTagObjects = (ArrayList<Tag>) getArguments().getSerializable("tag_list");
        listOfItemObjects = (ArrayList<Item>) getArguments().getSerializable("item_array");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // set title
        builder.setTitle("Select Tags");

        // set dialog non cancelable
        builder.setCancelable(false);
        String[] choices = new String[listOfTagObjects.size()];
        selectedTags = new boolean[listOfTagObjects.size()];
        int tagIter = 0;
        for (Tag tag : listOfTagObjects) {
            boolean everyItemHasTag = true;
            for (Item item : listOfItemObjects) {
                everyItemHasTag = item.getTags().contains(tag);
                if (!everyItemHasTag) { break; }
            }
            choices[tagIter] = tag.getName();
            selectedTags[tagIter] = everyItemHasTag;
            ++tagIter;
        }
        builder.setMultiChoiceItems(choices, selectedTags, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if (b) {
                    for(Item item : listOfItemObjects) {
                        Tag newtag = listOfTagObjects.get(i);
                        if(!item.getTags().contains(newtag)){  // item does not have tag
                            item.addTag(newtag);
                        }
                    }
                } else {
                    for (Item item : listOfItemObjects) {
                        item.removeTag(listOfTagObjects.get(i));
                    }
                }
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(context.getClass() == ItemListView.class){
                    ItemListView itemListView = (ItemListView) context;
                    itemListView.updateTags(listOfItemObjects);
                }
                if(context.getClass() == ItemDetailsView.class) {
                    ItemDetailsView itemDetailsView = (ItemDetailsView) context;
                    itemDetailsView.updateTagField();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // dismiss dialog
                dialogInterface.dismiss();
            }
        });
        builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // use for loop
                for (Tag tag : listOfTagObjects) {
                    for (Item item : listOfItemObjects) {
                        item.removeTag(tag);
                    }
                }
                // clear text view value if in itemDetailsView
                if (context.getClass() == ItemDetailsView.class) {
                    ItemDetailsView itemDetailsView = (ItemDetailsView) context;
                    TextView tags_field = itemDetailsView.findViewById(R.id.tags_field);
                    tags_field.setText("");
                }
            }
        });
        // show dialog
        return builder.create();
    }
}
