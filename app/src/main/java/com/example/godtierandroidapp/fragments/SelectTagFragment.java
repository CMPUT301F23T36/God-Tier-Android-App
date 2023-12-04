package com.example.godtierandroidapp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.godtierandroidapp.item.Item;
import com.example.godtierandroidapp.item.ItemDetailsView;
import com.example.godtierandroidapp.item.ItemListView;
import com.example.godtierandroidapp.R;
import com.example.godtierandroidapp.tag.Tag;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Provides a fragment that allows the user to select tags from a list of existing tags,
 * and apply them to a list of items.
 *
 * Currently looks at the context's class to update the tag list. This should be fixed to either:
 * - explicitly pass in the ItemList to update.
 * - return the list of updated items.
 *
 * @author Boris
 */
public class SelectTagFragment extends DialogFragment {
    private ArrayList<Tag> listOfTagObjects;
    private ArrayList<Item> listOfItemObjects;
    private boolean[] selectedTags;
    private Context context;

    private ArrayList<Tag> tagsToAdd;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    /**
     * @param tag_list list of tags to pick from.
     * @param item_array list of items to apply the picked tags to.
     */
    public SelectTagFragment(ArrayList<Tag> tag_list, ArrayList<Item> item_array) {
        Bundle args = new Bundle();
        args.putSerializable("item_array", item_array);
        args.putSerializable("tag_list", tag_list);
        setArguments(args);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        listOfTagObjects = (ArrayList<Tag>) getArguments().getSerializable("tag_list");
        listOfItemObjects = (ArrayList<Item>) getArguments().getSerializable("item_array");
        tagsToAdd = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Tags");

        builder.setCancelable(false);

        // remove tag from choices if all items contain that tag, I think... - Alex.
        String[] choices = new String[listOfTagObjects.size()];
        selectedTags = new boolean[listOfTagObjects.size()];
        int tagIter = 0;
        for (Tag tag : listOfTagObjects) {
            boolean everyItemHasTag = true;
            for (Item item : listOfItemObjects) {
                everyItemHasTag = item.getTags().contains(tag);
                if (!everyItemHasTag) { break; }
            }
            if (everyItemHasTag) { tagsToAdd.add(tag); }
            choices[tagIter] = tag.getName();
            selectedTags[tagIter] = everyItemHasTag;

            ++tagIter;
        }

        builder.setMultiChoiceItems(choices, selectedTags, (dialogInterface, i, selected) -> {
            if (selected) {
                tagsToAdd.add(listOfTagObjects.get(i));
            } else {
                tagsToAdd.remove(listOfTagObjects.get(i));
            }
        });
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            for (Item item : listOfItemObjects) {
                item.setTags(tagsToAdd);
            }

            UpdateTagsOnContext();
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.setNeutralButton("Clear All", (dialogInterface, i) -> {
            for (Tag tag : listOfTagObjects) {
                for (Item item : listOfItemObjects) {
                    item.removeTag(tag);
                }
            }

            UpdateTagsOnContext();
        });
        return builder.create();
    }

    /**
     * Update the context's tags.
     */
    private void UpdateTagsOnContext() {
        if (context.getClass() == ItemListView.class) {
            ItemListView itemListView = (ItemListView) context;
            itemListView.updateTags(listOfItemObjects);
        } else if (context.getClass() == ItemDetailsView.class) {
            ItemDetailsView itemDetailsView = (ItemDetailsView) context;
            itemDetailsView.updateTagField();
        }
    }
}
