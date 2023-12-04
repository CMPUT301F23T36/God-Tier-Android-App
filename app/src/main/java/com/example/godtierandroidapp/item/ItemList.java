package com.example.godtierandroidapp.item;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;


/**
 * Manages and stores a list of items. Allows for items to be added, removed , retrieved or updated
 * based on filtering and sorting criteria.
 *
 * @author Alex
 */
public class ItemList {
    /**
     * Interface defining filter criteria for items in an ItemList. Implementation requires a method
     * for passesFilter which returns a boolean value check for an Item passing the filter.
     */
    public interface FilterCriteria {
        boolean passesFilter(Item item);
    }

    /**
     * Called after this user's items are read from the database.
     */
    public interface DatabaseReadFinish {
        void onFinish();
    }

    private Comparator<Item> sortCriteria;
    private FilterCriteria filterCriteria;

    private ArrayList<Item> baseItemList;
    private ArrayList<Item> itemListSortedFiltered;

    DatabaseReference database;

    /**
     * Constructs an ItemList without an attached database.
     */
    public ItemList() {
        baseItemList = new ArrayList<>();
        itemListSortedFiltered = new ArrayList<>();
        sortCriteria = null;
        filterCriteria = null;
        database = null;
    }

    /**
     * Constructor with an attached database.
     * All changes to items will be synced with the database.
     *
     * @param database a reference to the "Items" database under the current user.
     * @param onFinish called when the database has been read.
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
     * Adds an item to the list. Updates the filtered and sorted item list accordingly.
     * @param item item to be added.
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
     * Updates item at index of the original list with the provided item.
     * @param idx index of item to be updated.
     * @param item new item to update index.
     */
    public void updateItem(int idx, Item item) {
        baseItemList.set(idx, item);
        remakeSortedFilteredList();
        updateDatabase();
    }


    /**
     * Retrieves item at the index of the sorted and filtered list.
     * @param index index of item to  retrieve
     * @return item at specified index
     */
    public Item getItem(int index) {
        if (index < 0 || index >= itemListSortedFiltered.size()) {
            return null;
        }
        return itemListSortedFiltered.get(index);
    }

    /**
     * Retrieves the size of the sorted and filtered list.
     * @return size of list
     */
    public int size() {
        return itemListSortedFiltered.size();
    }

    /**
     * Updates tags for provided item from both base, and sorted and filtered list.
     * @param item item to have its tags updated
     */
    public void updateTags(Item item) {
        baseItemList.get(baseItemList.indexOf(item)).setTags(item.getTags());
        int idx = itemListSortedFiltered.indexOf(item);
        if (idx >= 0) {
            itemListSortedFiltered.get(idx).setTags(item.getTags());
        }

        updateDatabase();
    }

    /**
     * Removes an item from the original and sorted and filtered list.
     * @param item item to be removed.
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
  * Retrieves sum of value of sorted and filtered items.
  * @return total float sum of item values.
  */
    public float getTotalValue() {
        float total = 0.0f;

        for (int i = 0; i < itemListSortedFiltered.size(); ++i) {
            total += itemListSortedFiltered.get(i).getEstimatedValue();
        }

        return total;
    }

    /**
     * retrieves the base list of items (no sorting and filtering).
     * @return list of items
     */
    public List<Item> getItems() {
        return baseItemList;
    }

    /**
     * Clears both lists of items.
     */
    public void clear() {
        baseItemList.clear();
        itemListSortedFiltered.clear();
        updateDatabase();
    }

    /**
     * Sets the filtering criteria and updates the sorted and filtered list.
     * @param newCriteria new filter criteria.
     */
    public void setFilter(FilterCriteria newCriteria) {
        filterCriteria = newCriteria;
        remakeSortedFilteredList();
    }

    /**
     * Sets the sorting criteria and updates sorted and filtered list.
     * @param newSortBy new sort criteria.
     */
    public void setSort(Comparator<Item> newSortBy) {
        sortCriteria = newSortBy;
        remakeSortedFilteredList();
    }

    /**
     * Removes filter criteria.
     */
    public void removeFilter() {
        setFilter(null);
    }

    /**
     * Removes sorting criteria.
     */
    public void removeSort() {
        setSort(null);
    }

    /**
     * Remakes an existing sorted and filtered list with updated criteria.
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
     * Checks if an item passes filter.
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
     * Updates the database if it exists.
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