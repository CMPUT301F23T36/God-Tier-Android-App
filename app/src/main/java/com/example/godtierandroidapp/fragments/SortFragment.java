package com.example.godtierandroidapp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;

import com.example.godtierandroidapp.item.Item;
import com.example.godtierandroidapp.item.ItemListView;
import com.example.godtierandroidapp.R;

import java.util.Comparator;

/**
 * Provides a fragment that allows the user to select a method to sort an {@code ItemListView} by.
 *
 * @author Alex
 */
public class SortFragment extends DialogFragment {

    /**
     * @param itemListView The attached {@code ItemListView}.
     */
    public SortFragment(ItemListView itemListView) {
        this.itemListView = itemListView;
    }

    private View dialogView;
    private ItemListView itemListView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.item_sort_fragment, null);

        Spinner order = dialogView.findViewById(R.id.sort_order_dropdown);
        order.setSelection(0);

        builder.setView(dialogView)
                .setMessage("Sort By:")
                .setPositiveButton("Apply", (dialog, which) -> {
                    itemListView.setSort(makeSortComparator());
                })
                .setNegativeButton("Clear", (dialog, which) -> {
                    itemListView.setSort(null);
                });

        return builder.create();
    }

    /**
     * Creates a {@code Comparator<Item>} from the selected sort method.
     * @return A comparator for sorting the items.
     */
    private Comparator<Item> makeSortComparator() {
        Spinner sortTypeDropdown = dialogView.findViewById(R.id.sort_type_dropdown);
        Object selectedText = sortTypeDropdown.getSelectedItem();
        if (selectedText == null) {
            return null;
        }

        Comparator<Item> comparator;

        switch (selectedText.toString()) {
            case "Date":
                comparator = Comparator.comparing(Item::getDateOfAcquisition);
                break;
            case "Description":
                comparator = Comparator.comparing(Item::getDescription);
                break;
            case "Make":
                comparator = Comparator.comparing(Item::getMake);
                break;
            case "Value":
                comparator = Comparator.comparing(Item::getEstimatedValue);
                break;
            case "Tag":
                comparator = Comparator.comparing(Item::getTagCount).reversed();
                break;
            default:
                Log.e("SortFragment", "Invalid type dropdown: " + selectedText);
                return null;
        };

        Spinner sortOrderDropdown = dialogView.findViewById(R.id.sort_order_dropdown);
        Object selectedOrder = sortOrderDropdown.getSelectedItem();
        switch (selectedOrder.toString()) {
            case "Ascending":
                return comparator;
            case "Descending":
                return comparator.reversed();
            default:
                Log.e("SortFragment", "Invalid order dropdown: " + selectedOrder);
                return null;
        }
    }
}
