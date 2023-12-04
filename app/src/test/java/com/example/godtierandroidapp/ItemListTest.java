package com.example.godtierandroidapp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.example.godtierandroidapp.item.Item;
import com.example.godtierandroidapp.item.ItemList;

@RunWith(RobolectricTestRunner.class)
public class ItemListTest {

    private ItemList itemList;
    private ArrayList<Item> mockItems;

    @Before
    public void setup() {
        itemList = new ItemList(); // Passing null for DatabaseReference and DatabaseReadFinish
        mockItems = createMockItems();
    }

    private ArrayList<Item> createMockItems() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("Item1", 100.0, new ArrayList<>()));
        items.add(new Item("Item2", 200.0, new ArrayList<>()));
        items.add(new Item("Item3", 30.0, new ArrayList<>()));
        return items;
    }

    @Test
    public void testAddItem() {
        assertEquals(0, itemList.size());

        for (Item item : mockItems) {
            itemList.addItem(item);
        }

        assertEquals(mockItems.size(), itemList.size());
    }

    @Test
    public void testUpdateItem() {
        itemList.addItem(mockItems.get(0));
        itemList.addItem(mockItems.get(1));

        Item updatedItem = new Item("updatedItem", 100.0, new ArrayList<>());
        itemList.updateItem(0, updatedItem);

        assertEquals(updatedItem, itemList.getItem(0));
    }

    @Test
    public void testGetItem() {
        itemList.addItem(mockItems.get(0));

        Item retrievedItem = itemList.getItem(0);
        assertNotNull(retrievedItem);
        assertEquals(mockItems.get(0), retrievedItem);

        Item nonExistentItem = itemList.getItem(1);
        assertNull(nonExistentItem);
    }

    @Test
    public void testRemoveItem() {
        itemList.addItem(mockItems.get(0));
        itemList.addItem(mockItems.get(1));

        assertEquals(2, itemList.size());

        itemList.removeItem(mockItems.get(0));

        assertEquals(1, itemList.size());
        assertNotNull(itemList.getItem(0));
    }

    @Test
    public void testGetTotalValue() {
        double sum = 0.0;
        for (Item item : mockItems) {
            itemList.addItem(item);
            sum += item.getEstimatedValue();
        }

        // Assuming getEstimatedValue() returns 1.0 for each item
        assertEquals(sum, itemList.getTotalValue(), 0.0001);
    }

    @Test
    public void testGetItems() {
        for (Item item : mockItems) {
            itemList.addItem(item);
        }

        assertEquals(mockItems, itemList.getItems());
    }

    @Test
    public void testClear() {
        for (Item item : mockItems) {
            itemList.addItem(item);
        }

        assertEquals(mockItems.size(), itemList.size());

        itemList.clear();

        assertEquals(0, itemList.size());
    }

    @Test
    public void testSetFilter() {
        itemList.addItem(mockItems.get(0));
        itemList.addItem(mockItems.get(1));

        ItemList.FilterCriteria filterCriteria = item -> item.getDescription().equals("Item1");
        itemList.setFilter(filterCriteria);

        assertEquals(1, itemList.size());
        assertEquals(mockItems.get(0), itemList.getItem(0));
    }

    @Test
    public void testSetSort() {
        itemList.addItem(mockItems.get(2));
        itemList.addItem(mockItems.get(0));
        itemList.addItem(mockItems.get(1));

        Comparator<Item> sortCriteria = Comparator.comparing(Item::getDescription);
        itemList.setSort(sortCriteria);

        assertEquals(mockItems.get(0), itemList.getItem(0));
        assertEquals(mockItems.get(1), itemList.getItem(1));
        assertEquals(mockItems.get(2), itemList.getItem(2));
    }

    @Test
    public void testRemoveFilter() {
        itemList.addItem(mockItems.get(0));
        itemList.addItem(mockItems.get(1));

        ItemList.FilterCriteria filterCriteria = item -> item.getDescription().equals("Item1");
        itemList.setFilter(filterCriteria);

        assertEquals(1, itemList.size());

        itemList.removeFilter();

        assertEquals(2, itemList.size());
    }

    @Test
    public void testRemoveSort() {
        itemList.addItem(mockItems.get(2));
        itemList.addItem(mockItems.get(0));
        itemList.addItem(mockItems.get(1));

        Comparator<Item> sortCriteria = Comparator.comparing(Item::getDescription);
        itemList.setSort(sortCriteria);

        assertEquals(mockItems.get(0), itemList.getItem(0));
        assertEquals(mockItems.get(1), itemList.getItem(1));
        assertEquals(mockItems.get(2), itemList.getItem(2));

        itemList.removeSort();

        assertEquals(mockItems.get(2), itemList.getItem(0));
        assertEquals(mockItems.get(0), itemList.getItem(1));
        assertEquals(mockItems.get(1), itemList.getItem(2));
    }
}
