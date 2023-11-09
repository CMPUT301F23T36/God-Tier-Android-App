package com.example.godtierandroidapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ItemList {
    public interface FilterCriteria {
        boolean passesFilter(Item item);
    }

    private Comparator<Item> sortCriteria;
    private FilterCriteria filterCriteria;

    private List<Item> baseItemList;
    private List<Item> itemListSortedFiltered;

    public ItemList() {
        baseItemList = new ArrayList<>();
        itemListSortedFiltered = new ArrayList<>();
        sortCriteria = null;
        filterCriteria = null;
    }

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
    }

    public Item getItem(int index) {
        if (index < 0 || index >= itemListSortedFiltered.size()) {
            return null;
        }
        return itemListSortedFiltered.get(index);
    }

    public int size() {
        return itemListSortedFiltered.size();
    }

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
    }

    public void setFilter(FilterCriteria newCriteria) {
        filterCriteria = newCriteria;
        remakeSortedFilteredList();
    }

    public void setSort(Comparator<Item> newSortBy) {
        sortCriteria = newSortBy;
        remakeSortedFilteredList();
    }

    public void removeFilter() {
        setFilter(null);
    }

    public void removeSort() {
        setSort(null);
    }

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

    private boolean passesFilter(Item item) {
        if (filterCriteria != null) {
            return filterCriteria.passesFilter(item);
        } else {
            return true;
        }
    }
}
