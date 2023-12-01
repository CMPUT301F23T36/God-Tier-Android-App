package com.example.godtierandroidapp;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;


/**
 * Manages and stores a list of items. Allows for items to be added, removed , retrieved or updated
 * based on filtering and sorting criteria.
 *
 * @author Alex
 * @version 1.0
 * @since 2023-11-06
 */
public class ItemList {
    /**
     * Interface defining filter criteria for items in an ItemList. Implementation requires a method
     * for passesFilter which returns a boolean value check for an Item passing the filter.
     */
    public interface FilterCriteria {
        boolean passesFilter(Item item);
    }

    public interface DatabaseReadFinish {
        void onFinish();
    }

    private Comparator<Item> sortCriteria;
    private FilterCriteria filterCriteria;

    private ArrayList<Item> baseItemList;
    private ArrayList<Item> itemListSortedFiltered;

    DatabaseReference database;


    /**
     * Constructs an ItemList without filter or sort criteria
     */
    public ItemList() {
        baseItemList = new ArrayList<>();
        itemListSortedFiltered = new ArrayList<>();
        sortCriteria = null;
        filterCriteria = null;
        database = null;
    }

    /**
     *
     * @param database a reference to the "Items" database under the current user
     */
    public ItemList(DatabaseReference database, DatabaseReadFinish onFinish) {
        baseItemList = new ArrayList<>();
        itemListSortedFiltered = new ArrayList<>();
        sortCriteria = null;
        filterCriteria = null;

        this.database = database;
        database.get().addOnSuccessListener((DataSnapshot data) -> {
            baseItemList = new ArrayList<>();
            try {
                Object value = data.getValue();
                if (value != null) {
                    ArrayList<HashMap> items = (ArrayList<HashMap>) value;
                    Log.d("Database items", items.toString());

                    for (HashMap item : items) {
                        baseItemList.add(new Item(item));
                    }

                    remakeSortedFilteredList();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (onFinish != null) {
                onFinish.onFinish();
            }
        });
    }

    /**
     * Adds an item with specified filters and sorting application
     * @param item item of interest to be added to list
     */
    public void addItem(Item item) {
        baseItemList.add(item);
        if (passesFilter(item)) {
            if (sortCriteria != null) {
                int index = Collections.binarySearch(itemListSortedFiltered, item, sortCriteria);
                if (index < 0) {
                    index = -index - 1;
                }
                itemListSortedFiltered.add(index, item);
            } else {
                itemListSortedFiltered.add(item);
            }
        }
        updateDatabase();
    }

    /**
     * Updates item at index of sorted and filtered list with provided item
     * @param idx index of item to be updated
     * @param item new item to update index
     */
    public void updateItem(int idx, Item item) {
        baseItemList.set(idx, item);
        remakeSortedFilteredList();
        updateDatabase();
    }


    /**
     * Retrieves item at index of sorted and filtered list
     * @param index index of item to  retrieve
     * @return item at specfied index
     */
    public Item getItem(int index) {
        if (index < 0 || index >= itemListSortedFiltered.size()) {
            return null;
        }
        return itemListSortedFiltered.get(index);
    }

    /**
     * Retrieves the integer size of sorted and filtered list
     * @return size of list
     */
    public int size() {
        return itemListSortedFiltered.size();
    }

    /**
     * Updates tags for provided item from both base, and sorted and filtered list
     * @param item item to have its tags updated
     */
    public void updateTags(Item item){
        baseItemList.get(baseItemList.indexOf(item)).setTags(item.getTags());
        itemListSortedFiltered.get(itemListSortedFiltered.indexOf(item)).setTags(item.getTags());
    }

    /**
     * Removes item from both base, and sorted and filtered list
     * @param item item to be removed
     */
    public void removeItem(Item item) {
        for (int i = 0; i < baseItemList.size(); ++i) {
            if (baseItemList.get(i) == item) {
                baseItemList.remove(i);
                break;
            }
        }

        for (int i = 0; i < itemListSortedFiltered.size(); ++i) {
            if (itemListSortedFiltered.get(i) == item) {
                itemListSortedFiltered.remove(i);
                break;
            }
        }
        updateDatabase();
    }

  /**
  * Retrieves sum of value of items in list view
  * @return total float sum of item values
  */
    public float getTotalValue() {
        float total = 0.0f;

        for (int i = 0; i < itemListSortedFiltered.size(); ++i) {
            total += itemListSortedFiltered.get(i).getEstimatedValue();
        }

        return total;
    }

      /**
     * retrieves the base list of items (no sorting and filtering)
     * @return list of items
     */
    public List<Item> getItems(){
        return baseItemList;
    }

      /**
     * Clears both lists of items
     */
    public void clear() {
        baseItemList.clear();
        itemListSortedFiltered.clear();
        updateDatabase();
    }

    /**
     * Sets the filtering criteria and updates sorted and filtered list
     * @param newCriteria new filter criteria
     */
    public void setFilter(FilterCriteria newCriteria) {
        filterCriteria = newCriteria;
        remakeSortedFilteredList();
    }

    /**
     * Sets the sorting criteria and updates sorted and filtered list
     * @param newSortBy new sort criteria
     */
    public void setSort(Comparator<Item> newSortBy) {
        sortCriteria = newSortBy;
        remakeSortedFilteredList();
    }

    /**
     * Removes filter criteria
     */
    public void removeFilter() {
        setFilter(null);
    }

    /**
     * Removes sorting criteria
     */
    public void removeSort() {
        setSort(null);
    }

    /**
     * Remakes an existing sorted and filtered list with updated criteria
     */
    private void remakeSortedFilteredList() {
        itemListSortedFiltered.clear();

        for (int i = 0; i < baseItemList.size(); ++i) {
            Item item = baseItemList.get(i);
            if (passesFilter(item)) {
                itemListSortedFiltered.add(item);
            }
        }

        if (sortCriteria != null) {
            itemListSortedFiltered.sort(sortCriteria);
        }
    }

    /**
     * Checks if item passes filter
     * @param item item in list to be checked
     * @return bool value if item passed or not
     */
    private boolean passesFilter(Item item) {
        if (filterCriteria != null) {
            return filterCriteria.passesFilter(item);
        } else {
            return true;
        }
    }

    /**
     * Update the database if it exists
     */
    private void updateDatabase() {
        if (database != null) {
            Log.d("ItemList", "Updated database");
            try {
                database.setValue(baseItemList)
                        .addOnSuccessListener((Void v) -> {
                            Log.d("ItemList", "write items success");
                        }).addOnFailureListener((Exception e) -> {
                            Log.d("ItemList", e.getMessage());
                        });
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
