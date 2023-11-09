package com.example.godtierandroidapp;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

public class FilterFragment extends DialogFragment {
    public class FilterFunction implements ItemList.FilterCriteria {
        private Tag tag;

        FilterFunction(Tag tag) {
            this.tag = tag;
        }

        public boolean passesFilter(Item item) {
            if (tag != null && !item.hasTag(tag)) {
                return false;
            }

            return true;
        }
    }

    FilterFragment(ItemListView itemListView) {
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
        String tagText = tagEditText.getText().toString();
        Tag tag = null;
        if (!tagText.equals("")) {
            tag = new Tag(tagText);
        }

        return new FilterFunction(tag);
    }
}
