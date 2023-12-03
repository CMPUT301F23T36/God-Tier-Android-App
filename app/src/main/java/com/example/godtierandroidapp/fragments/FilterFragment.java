package com.example.godtierandroidapp.fragments;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.example.godtierandroidapp.item.Item;
import com.example.godtierandroidapp.item.ItemList;
import com.example.godtierandroidapp.item.ItemListView;
import com.example.godtierandroidapp.R;
import com.example.godtierandroidapp.tag.Tag;

/**
* Represents a DialogFragment that applys filters to an ItemListView
*
* @author Alex
* @version 1.0
* @since 2023-11-09
 */
public class FilterFragment extends DialogFragment {
    /**

     * The filter function for this item associated with tag, description, and make

     */
    public class FilterFunction implements ItemList.FilterCriteria {
        private Tag tag;
        private String descString;
        private String makeString;

        /**
         * Creates a new filter function with specified tag, description, and/or make
         * @param tag item tag for filtering
         * @param desc word(s) in item description for filtering
         * @param make = item make for filtering
         */
        FilterFunction(Tag tag, String desc, String make) {
            this.tag = tag;
            this.descString = desc.trim().toLowerCase();
            this.makeString = make.trim().toLowerCase();
        }

        /**
         * Checks if item passes filter
         * @param item item in list to be checked
         * @return bool value if item passed or not
         */
        public boolean passesFilter(Item item) {
            if (tag != null && !item.hasTag(tag)) {
                return false;
            }
            if (descString != null) {
                if (!descString.isEmpty() && !item.getDescription().toLowerCase().contains(descString)) {
                    return false;
                }
            }
            if (makeString != null) {
                if (!makeString.isEmpty() && !item.getMake().toLowerCase().contains(makeString)) {
                    return false;
                }
            }

            return true;
        }
    }

    public FilterFragment(ItemListView itemListView) {
        this.itemListView = itemListView;
    }

    View dialogView;
    ItemListView itemListView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.item_filter_fragment, null);

        builder.setView(dialogView)
                .setMessage("Filter By:")
                .setPositiveButton("Apply", (dialog, which) -> {
                    itemListView.setFilter(makeFilterFunction());
                })
                .setNegativeButton("Clear", (dialog, which) -> {
                    itemListView.setFilter(null);
                });

        return builder.create();
    }

    private ItemList.FilterCriteria makeFilterFunction() {
        EditText tagEditText = dialogView.findViewById(R.id.tagFilter);
        EditText descEditText = dialogView.findViewById(R.id.descriptionFilter);
        EditText makeEditText = dialogView.findViewById(R.id.makeFilter);
        String tagText = tagEditText.getText().toString();
        String descText = descEditText.getText().toString();
        String makeText = makeEditText.getText().toString();

        Tag tag = null;
        if (!tagText.equals("")) {
            tag = new Tag(tagText);
        }

        return new FilterFunction(tag, descText, makeText);
    }
}
