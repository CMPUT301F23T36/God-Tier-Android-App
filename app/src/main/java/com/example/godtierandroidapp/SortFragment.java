package com.example.godtierandroidapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;

import java.util.Comparator;

public class SortFragment extends DialogFragment {
    SortFragment(ItemListView itemListView) {
        this.itemListView = itemListView;
    }

    View dialogView;
    ItemListView itemListView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.item_sort_fragment, null);

        Spinner order = dialogView.findViewById(R.id.sort_order_dropdown);
        order.setSelection(0);

        builder.setView(dialogView)
                .setMessage("Enter text:")
                .setPositiveButton("OK", (dialog, which) -> {
                    itemListView.setSort(makeSortComparator());
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    itemListView.setSort(null);
                });

        return builder.create();
    }

    private Comparator<Item> makeSortComparator() {
        Spinner sortTypeDropdown = dialogView.findViewById(R.id.sort_type_dropdown);
        Object selectedText = sortTypeDropdown.getSelectedItem();
        if (selectedText == null) {
            return null;
        }

        Comparator<Item> comparator;

        switch (selectedText.toString()) {
            case "date":
                comparator = Comparator.comparing(Item::getDateOfAcquisition);
                break;
            case "description":
                comparator = Comparator.comparing(Item::getDescription);
                break;
            case "make":
                comparator = Comparator.comparing(Item::getMake);
                break;
            case "value":
                comparator = Comparator.comparing(Item::getEstimatedValue);
                break;
            default:
                Log.d("SortFragment", "Invalid type dropdown: " + selectedText);
                return null;
        };

        Spinner sortOrderDropdown = dialogView.findViewById(R.id.sort_order_dropdown);
        Object selectedOrder = sortOrderDropdown.getSelectedItem();
        switch (selectedOrder.toString()) {
            case "ascending":
                return comparator;
            case "descending":
                return comparator.reversed();
            default:
                Log.d("SortFragment", "Invalid order dropdown: " + selectedOrder);
                return null;
        }
    }
}
