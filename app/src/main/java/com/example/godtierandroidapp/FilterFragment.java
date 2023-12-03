package com.example.godtierandroidapp;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Calendar;
import java.util.Date;

/**
* Represents a DialogFragment that applies filters to an ItemListView
*
* @author Alex
* @version 1.0
* @since 2023-11-09
 */
public class FilterFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    /**

     * The filter function for this item associated with tag, description, and make

     */
    public class FilterFunction implements ItemList.FilterCriteria {
        private Tag tag;
        private String descString;
        private String makeString;
        private Date fromDate;
        private Date toDate;

        /**
         * Creates a new filter function with specified tag, description, and/or make
         * @param tag item tag for filtering
         * @param desc word(s) in item description for filtering
         * @param make = item make for filtering
         */
        FilterFunction(Tag tag, String desc, String make, Date from, Date to) {
            this.tag = tag;
            this.descString = desc.trim().toLowerCase();
            this.makeString = make.trim().toLowerCase();
            this.fromDate = from;
            this.toDate = to;
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
            if (fromDate != null && item.getDateOfAcquisition().compareTo(fromDate) < 0) {
                return false;
            }
            if (toDate != null && item.getDateOfAcquisition().compareTo(toDate) > 0) {
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
    TextView fromDateTextView;
    Date fromDate;
    TextView toDateTextView;
    Date toDate;
    int fromToFlag; // 0 = fromDate, 1 = toDate

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

        fromDateTextView = (TextView) dialogView.findViewById(R.id.fromDateText);
        fromDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromToFlag = 0;
                showDatePicker();
            }
        });

        toDateTextView = (TextView) dialogView.findViewById(R.id.toDateText);
        toDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromToFlag = 1;
                showDatePicker();
            }
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

        return new FilterFunction(tag, descText, makeText, fromDate, toDate);
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private String dateToString(Date date) {
        StringBuilder str = new StringBuilder();

        int month = date.getMonth() + 1;
        int day = date.getDate();
        int year = date.getYear() + 1900;

        str.append(month);
        str.append("/");
        str.append(day);
        str.append("/");
        str.append(year);

        return str.toString();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Date newDate = new Date(year - 1900, month, dayOfMonth);
        if (fromToFlag == 0) {
            fromDate = newDate;
            StringBuilder str = new StringBuilder("From: ");
            str.append(dateToString(fromDate));
            fromDateTextView.setText(str.toString());
        } else {
            toDate = newDate;
            StringBuilder str = new StringBuilder("To: ");
            str.append(dateToString(toDate));
            toDateTextView.setText(str.toString());
        }
    }
}
