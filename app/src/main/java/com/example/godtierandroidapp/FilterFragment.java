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
        private String descString;
        private String makeString;


        FilterFunction(Tag tag, String desc, String make) {
            this.tag = tag;
            this.descString = desc.trim().toLowerCase();
            this.makeString = make.trim().toLowerCase();
        }

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
